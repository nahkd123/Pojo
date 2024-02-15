package io.github.nahkd123.pojo.api.editor;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.IntConsumer;
import java.util.function.IntFunction;
import java.util.function.IntSupplier;

import org.bukkit.Material;

public final class EditableList implements Editable {
	private NodeDescription description;
	private IntSupplier sizeGetter;
	private IntFunction<Editable> converter;
	private IntConsumer adder;
	private IntConsumer remover;
	private BiConsumer<Integer, Integer> swapper;

	public EditableList(NodeDescription description, IntSupplier sizeGetter, IntFunction<Editable> converter, IntConsumer adder, IntConsumer remover, BiConsumer<Integer, Integer> swapper) {
		this.description = description;
		this.sizeGetter = sizeGetter;
		this.converter = converter;
		this.adder = adder;
		this.remover = remover;
		this.swapper = swapper;
	}

	public static EditableList fromStringList(NodeDescription description, List<String> list) {
		// @formatter:off
		return new EditableList(
			description,
			list::size,
			index -> new EditableString(
				new NodeDescription(Material.PAPER, "Text at position #" + index),
				() -> list.get(index),
				s -> list.set(index, s == null? "" : s)),
			index -> list.add(index, ""),
			list::remove,
			(from, to) -> {
				String s = list.remove((int) from);
				list.add(to, s);
			});
		// @formatter:on
	}

	@Override
	public NodeDescription getDescription() { return description; }

	public int size() {
		return sizeGetter.getAsInt();
	}

	public void addNew(int index) {
		adder.accept(index);
	}

	public void appendNew() {
		addNew(size() - 1);
	}

	public Editable get(int index) {
		return converter.apply(index);
	}

	public void remove(int index) {
		remover.accept(index);
	}

	public void swap(int from, int to) {
		swapper.accept(from, to);
	}

	public List<Editable> toList() {
		List<Editable> list = new ArrayList<>();
		for (int i = 0; i < size(); i++) list.add(get(i));
		return list;
	}

	@Override
	public List<String> getPreviewLines() {
		return toList().stream()
			.flatMap(e -> e.getPreviewLines().stream())
			.toList();
	}
}

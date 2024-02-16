package io.github.nahkd123.pojo.api.editor;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

public final class EditableObject implements Editable {
	private NodeDescription description;
	private Supplier<List<Editable>> fieldsGetter;

	// TODO hacky way to override preview lines
	private Supplier<List<String>> customPreviewLines = null;

	public EditableObject(NodeDescription description, Supplier<List<Editable>> fieldsGetter) {
		this.description = description;
		this.fieldsGetter = fieldsGetter;
	}

	public EditableObject(NodeDescription description, List<Editable> fields) {
		this(description, () -> fields);
	}

	@Override
	public NodeDescription getDescription() { return description; }

	public List<Editable> getFields() { return fieldsGetter.get(); }

	@Override
	public List<String> getPreviewLines() {
		if (customPreviewLines != null) return customPreviewLines.get();

		return fieldsGetter.get().stream()
			.flatMap(s -> Stream.concat(
				Stream.of("&7" + s.getDescription().name() + ":"),
				s.getPreviewLines().stream().map(ss -> "  " + ss)))
			.toList();
	}

	public static <T> EditableObject enumToBooleans(NodeDescription description, Collection<T> values, Supplier<T> getter, Consumer<T> setter, Function<T, NodeDescription> describer) {
		// @formatter:off
		EditableObject obj = new EditableObject(
			description,
			values.stream().map(v -> (Editable) new EditableBool(
				describer.apply(v),
				() -> v == getter.get() || (v != null && v.equals(getter.get())),
				s -> {
					if (!s) return;
					setter.accept(v);
				}))
			.toList()
		);
		// @formatter:on

		obj.customPreviewLines = () -> Collections.singletonList("&a" + describer.apply(getter.get()).name());
		return obj;
	}
}

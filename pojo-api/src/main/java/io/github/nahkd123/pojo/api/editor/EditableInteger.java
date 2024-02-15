package io.github.nahkd123.pojo.api.editor;

import java.util.Collections;
import java.util.List;
import java.util.function.IntConsumer;
import java.util.function.IntPredicate;
import java.util.function.IntSupplier;

public final class EditableInteger implements Editable {
	private NodeDescription description;
	private IntSupplier getter;
	private IntConsumer setter;
	private IntPredicate validator;

	public EditableInteger(NodeDescription description, IntSupplier getter, IntConsumer setter, IntPredicate validator) {
		this.description = description;
		this.getter = getter;
		this.setter = setter;
		this.validator = validator;
	}

	public EditableInteger(NodeDescription description, IntSupplier getter, IntConsumer setter) {
		this(description, getter, setter, s -> true);
	}

	@Override
	public NodeDescription getDescription() { return description; }

	public int getValue() { return getter.getAsInt(); }

	public void setValue(int value) {
		setter.accept(value);
	}

	public IntPredicate getValidator() { return validator; }

	@Override
	public List<String> getPreviewLines() { return Collections.singletonList("&e" + getter.getAsInt()); }
}

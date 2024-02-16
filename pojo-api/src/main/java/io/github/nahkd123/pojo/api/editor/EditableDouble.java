package io.github.nahkd123.pojo.api.editor;

import java.util.Collections;
import java.util.List;
import java.util.function.DoubleConsumer;
import java.util.function.DoublePredicate;
import java.util.function.DoubleSupplier;

public final class EditableDouble implements Editable {
	private NodeDescription description;
	private DoubleSupplier getter;
	private DoubleConsumer setter;
	private DoublePredicate validator;

	public EditableDouble(NodeDescription description, DoubleSupplier getter, DoubleConsumer setter, DoublePredicate validator) {
		this.description = description;
		this.getter = getter;
		this.setter = setter;
		this.validator = validator;
	}

	public EditableDouble(NodeDescription description, DoubleSupplier getter, DoubleConsumer setter) {
		this(description, getter, setter, s -> true);
	}

	@Override
	public NodeDescription getDescription() { return description; }

	public double getValue() { return getter.getAsDouble(); }

	public void setValue(double value) {
		setter.accept(value);
	}

	public DoublePredicate getValidator() { return validator; }

	@Override
	public List<String> getPreviewLines() { return Collections.singletonList("&e" + getter.getAsDouble()); }
}

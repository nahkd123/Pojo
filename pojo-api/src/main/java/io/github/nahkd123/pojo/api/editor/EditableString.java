package io.github.nahkd123.pojo.api.editor;

import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

public final class EditableString implements Editable {
	private NodeDescription description;
	private Supplier<String> getter;
	private Consumer<String> setter;
	private Predicate<String> validator;

	public EditableString(NodeDescription description, Supplier<String> getter, Consumer<String> setter, Predicate<String> validator) {
		this.description = description;
		this.getter = getter;
		this.setter = setter;
		this.validator = validator;
	}

	public EditableString(NodeDescription description, Supplier<String> getter, Consumer<String> setter) {
		this(description, getter, setter, s -> true);
	}

	@Override
	public NodeDescription getDescription() { return description; }

	public void setValue(String value) {
		setter.accept(value);
	}

	public String getValue() { return getter.get(); }

	public Predicate<String> getValidator() { return validator; }

	@Override
	public List<String> getPreviewLines() {
		String v = getValue();
		return Collections.singletonList(v != null ? "&f&o" + v : "&7&o(empty)");
	}
}

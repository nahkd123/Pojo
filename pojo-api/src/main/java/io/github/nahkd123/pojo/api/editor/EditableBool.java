package io.github.nahkd123.pojo.api.editor;

import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public final class EditableBool implements Editable {
	private NodeDescription description;
	private Supplier<Boolean> getter;
	private Consumer<Boolean> setter;

	public EditableBool(NodeDescription description, Supplier<Boolean> getter, Consumer<Boolean> setter) {
		this.description = description;
		this.getter = getter;
		this.setter = setter;
	}

	@Override
	public NodeDescription getDescription() { return description; }

	public void setValue(boolean value) {
		setter.accept(value);
	}

	public boolean getValue() { return getter.get(); }

	@Override
	public List<String> getPreviewLines() { return Collections.singletonList(getter.get() ? "&aYes" : "&cNo"); }
}

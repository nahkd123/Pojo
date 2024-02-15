package io.github.nahkd123.pojo.api.item.standard.component;

import io.github.nahkd123.pojo.api.editor.NodeDescription;

public interface EditorComponentsFactory<T> extends DefaultedComponentsFactory<T> {
	public NodeDescription getEditorDescription();
}

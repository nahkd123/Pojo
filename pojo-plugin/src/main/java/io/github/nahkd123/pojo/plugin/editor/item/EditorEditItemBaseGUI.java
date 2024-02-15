package io.github.nahkd123.pojo.plugin.editor.item;

import io.github.nahkd123.pojo.api.item.PojoItem;
import io.github.nahkd123.pojo.plugin.editor.EditorGUI;
import io.github.nahkd123.pojo.plugin.editor.EditorSession;

public abstract class EditorEditItemBaseGUI extends EditorGUI {
	private PojoItem item;

	public EditorEditItemBaseGUI(EditorSession session, EditorEditItemTarget current, PojoItem item, int rows) {
		super(session, current, rows);
		this.item = item;
	}

	public PojoItem getItem() { return item; }
}

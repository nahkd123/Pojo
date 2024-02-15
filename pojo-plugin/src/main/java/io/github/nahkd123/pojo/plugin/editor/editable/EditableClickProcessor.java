package io.github.nahkd123.pojo.plugin.editor.editable;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import io.github.nahkd123.pojo.api.editor.Editable;
import io.github.nahkd123.pojo.plugin.editor.EditorGUI;
import io.github.nahkd123.pojo.plugin.editor.EditorSession;

public interface EditableClickProcessor<T extends Editable> {
	default void onClick(T editable, InventoryClickEvent event, EditableEditorGUI gui) {
		EditorSession session = gui.getSession();
		EditableEditorTarget target = new EditableEditorTarget(gui, editable);
		session.replaceTarget(gui.getCurrent(), target);
		EditorGUI newGui = target.createGUI(session);
		event.getWhoClicked().openInventory(newGui.getInventory());
	}

	public ItemStack createIcon(T editable);
}

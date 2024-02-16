package io.github.nahkd123.pojo.plugin.editor.editable;

import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import io.github.nahkd123.pojo.api.editor.Editable;
import io.github.nahkd123.pojo.api.editor.EditableList;
import io.github.nahkd123.pojo.plugin.editor.EditorGUI;
import io.github.nahkd123.pojo.plugin.editor.EditorSession;
import io.github.nahkd123.pojo.plugin.gui.StackBuilder;

public class EditableListEditorGUI extends ListLikeEditableEditorGUI {
	public static final ItemStack ADD_ELEMENT = new StackBuilder(new ItemStack(Material.LIME_STAINED_GLASS_PANE))
		.name("&a+ &eAdd new element")
		.appendLore(
			"&eClick &7to create a new element",
			"&7at this position.")
		.getStack();

	private EditableList editable;

	public EditableListEditorGUI(EditorGUI previous, EditorSession session, EditableEditorTarget current, EditableList editable) {
		super(previous, session, current, editable.toList());
		this.editable = editable;
	}

	@Override
	public void refresh() {
		allEditables = editable.toList();
		super.refresh();
	}

	@Override
	protected ItemStack createAddIcon() {
		return ADD_ELEMENT;
	}

	@Override
	protected ItemStack createIconForElement(Editable editable) {
		return new StackBuilder(super.createIconForElement(editable))
			.appendLore(
				"&eRight click &7to remove",
				"&eShift + Left click &7to move forward",
				"&eShift + Right click &7to move backward")
			.getStack();
	}

	@Override
	protected void onClickAdd(InventoryClickEvent event) {
		editable.addNew(editable.size());
		getCurrent().save();
		refresh();
	}

	@Override
	protected void onClickEditable(Editable editable, InventoryClickEvent event) {
		if (!event.isShiftClick() && event.isRightClick()) {
			int idx = allEditables.indexOf(editable);
			if (idx == -1) return;
			this.editable.remove(idx);
			getCurrent().save();
			refresh();
			return;
		}

		if (event.isShiftClick()) {
			int direction = event.isLeftClick() ? -1 : event.isRightClick() ? 1 : 0;
			if (direction == 0) return;
			int idx = allEditables.indexOf(editable);
			if (idx == -1) return;
			int newIdx = idx + direction;
			if (newIdx < 0 || newIdx >= allEditables.size()) return;

			this.editable.swap(idx, newIdx);
			getCurrent().save();
			refresh();
			return;
		}

		super.onClickEditable(editable, event);
	}
}

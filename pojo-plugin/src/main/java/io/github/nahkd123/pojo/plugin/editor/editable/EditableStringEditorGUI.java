package io.github.nahkd123.pojo.plugin.editor.editable;

import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import io.github.nahkd123.pojo.api.editor.EditableString;
import io.github.nahkd123.pojo.api.utils.TextUtils;
import io.github.nahkd123.pojo.plugin.editor.EditorGUI;
import io.github.nahkd123.pojo.plugin.editor.EditorSession;
import io.github.nahkd123.pojo.plugin.gui.StackBuilder;

public class EditableStringEditorGUI extends EditableEditorGUI {
	public static final ItemStack CLEAR_TEXT = new StackBuilder(new ItemStack(Material.BARRIER))
		.name("&cClear text content")
		.appendLore(
			"&eClick &7to set the text content",
			"&7to &o(empty)&7.")
		.getStack();

	private EditableString editable;

	public EditableStringEditorGUI(EditorGUI previous, EditorSession session, EditableEditorTarget current, EditableString editable) {
		super(previous, session, current, 4);
		this.editable = editable;
		placeButtons();
	}

	@Override
	public void refresh() {
		super.refresh();
		placeButtons();
	}

	protected void placeButtons() {
		getInventory().setItem(9 * 2 + 3, new StackBuilder(new ItemStack(Material.PAPER))
			.name("&eSet text content")
			.appendLore(
				"",
				"&7Current content: &f&o" + (editable.getValue() != null ? editable.getValue() : "&7&o(empty)"),
				"",
				"&eLeft click &7to edit")
			.getStack());
		getInventory().setItem(9 * 2 + 6, CLEAR_TEXT);
	}

	@Override
	public void onInventoryClick(InventoryClickEvent event) {
		super.onInventoryClick(event);
		if (event.getClickedInventory() != getInventory()) return;

		if (event.getSlot() == 9 * 2 + 3) {
			event.getWhoClicked().closeInventory();
			event.getWhoClicked().sendMessage(TextUtils.colorize("&8================"));
			event.getWhoClicked().sendMessage(TextUtils.colorize("&7Enter the text content:"));

			getSession().setHeldInput((session, input) -> {
				if (!editable.getValidator().test(input)) {
					event.getWhoClicked().sendMessage(TextUtils.colorize("&cInvalid input. &fPlease re-enter with "
						+ "valid input:"));
					return;
				}

				session.setHeldInput(null);
				editable.setValue(input);
				getCurrent().save();
				event.getWhoClicked().openInventory(getInventory());
				refresh();
				event.getWhoClicked().sendMessage(TextUtils.colorize("&8================"));
			});

			return;
		}

		if (event.getSlot() == 9 * 2 + 6) {
			editable.setValue(null);
			refresh();
			return;
		}
	}
}

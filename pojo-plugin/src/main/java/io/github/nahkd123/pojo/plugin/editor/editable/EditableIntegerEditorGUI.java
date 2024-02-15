package io.github.nahkd123.pojo.plugin.editor.editable;

import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import io.github.nahkd123.pojo.api.editor.EditableInteger;
import io.github.nahkd123.pojo.api.utils.TextUtils;
import io.github.nahkd123.pojo.plugin.editor.EditorGUI;
import io.github.nahkd123.pojo.plugin.editor.EditorSession;
import io.github.nahkd123.pojo.plugin.gui.StackBuilder;

public class EditableIntegerEditorGUI extends EditableEditorGUI {
	private EditableInteger editable;

	public EditableIntegerEditorGUI(EditorGUI previous, EditorSession session, EditableEditorTarget current, EditableInteger editable) {
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
		getInventory().setItem(9 * 2 + 2, new StackBuilder(new ItemStack(Material.PAPER))
			.name("&eSet integer")
			.appendLore(
				"",
				"&7Current value: &b" + editable.getValue(),
				"",
				"&eLeft click &7to edit")
			.getStack());

		getInventory().setItem(9 * 2 + 4, new StackBuilder(new ItemStack(Material.GOLD_INGOT))
			.name("&eIncrease/decrease value")
			.appendLore(
				"",
				"&7Current value: &b" + editable.getValue(),
				"",
				"&eLeft click/Right click &7to &a+1&7/&c-1",
				"&eShift + Left click/Right click &7to &a+10&7/&c-10")
			.getStack());

		getInventory().setItem(9 * 2 + 6, new StackBuilder(new ItemStack(Material.GOLD_NUGGET))
			.name("&eSet integer to 0")
			.appendLore(
				"",
				"&7Current value: &b" + editable.getValue(),
				"",
				"&eLeft click &7to set to 0")
			.getStack());
	}

	@Override
	public void onInventoryClick(InventoryClickEvent event) {
		super.onInventoryClick(event);
		if (event.getClickedInventory() != getInventory()) return;

		if (event.getSlot() == 9 * 2 + 2) {
			event.getWhoClicked().closeInventory();
			event.getWhoClicked().sendMessage(TextUtils.colorize("&8================"));
			event.getWhoClicked().sendMessage(TextUtils.colorize("&7Enter the new value:"));

			getSession().setHeldInput((session, input) -> {
				int v;
				try {
					v = Integer.parseInt(input);
				} catch (NumberFormatException e) {
					event.getWhoClicked().sendMessage(TextUtils.colorize("&cInvalid integer. &fPlease re-enter with "
						+ "valid integer:"));
					return;
				}

				if (!editable.getValidator().test(v)) {
					event.getWhoClicked().sendMessage(TextUtils.colorize("&cInvalid integer. &fPlease re-enter with "
						+ "valid integer:"));
					return;
				}

				session.setHeldInput(null);
				editable.setValue(v);
				getCurrent().save();
				event.getWhoClicked().openInventory(getInventory());
				refresh();
				event.getWhoClicked().sendMessage(TextUtils.colorize("&8================"));
			});

			return;
		}

		if (event.getSlot() == 9 * 2 + 4) {
			int direction = event.isLeftClick() ? 1 : -1;
			if (event.isShiftClick()) direction *= 10;
			editable.setValue(editable.getValue() + direction);
			refresh();
			return;
		}

		if (event.getSlot() == 9 * 2 + 6) {
			editable.setValue(0);
			refresh();
			return;
		}
	}
}

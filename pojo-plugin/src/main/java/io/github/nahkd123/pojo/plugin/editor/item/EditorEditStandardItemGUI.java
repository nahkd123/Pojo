package io.github.nahkd123.pojo.plugin.editor.item;

import java.time.LocalDateTime;

import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import io.github.nahkd123.pojo.api.editor.EditableObject;
import io.github.nahkd123.pojo.api.item.standard.StandardPojoItem;
import io.github.nahkd123.pojo.api.item.standard.component.Component;
import io.github.nahkd123.pojo.api.item.standard.component.EditorSupportedComponent;
import io.github.nahkd123.pojo.api.utils.TextUtils;
import io.github.nahkd123.pojo.plugin.editor.EditorGUI;
import io.github.nahkd123.pojo.plugin.editor.EditorSession;
import io.github.nahkd123.pojo.plugin.editor.browse.EditorBrowseItemsTarget;
import io.github.nahkd123.pojo.plugin.editor.editable.EditableEditorTarget;
import io.github.nahkd123.pojo.plugin.gui.StackBuilder;
import io.github.nahkd123.pojo.plugin.recycle.RecycleBin;

public class EditorEditStandardItemGUI extends EditorEditItemBaseGUI {
	public static final ItemStack ADD_COMPONENT = new StackBuilder(new ItemStack(Material.LIME_STAINED_GLASS_PANE))
		.name("&a+ &eAdd Component")
		.appendLore("&eClick &7to browse and add component")
		.getStack();
	public static final ItemStack GET_ITEM = new StackBuilder(new ItemStack(Material.GOLD_INGOT))
		.name("&eGet item")
		.appendLore("&eClick &7to get item to", "&7your inventory")
		.getStack();
	public static final ItemStack RECYCLE_BIN = new StackBuilder(new ItemStack(Material.CAULDRON))
		.name("&cMove to Recycle Bin")
		.appendLore(
			"&eClick &7to move this item to",
			"&7server's recycle bin",
			"",
			"&7Recycle Bin is located inside",
			"&7Pojo's plugin data folder.")
		.getStack();

	private EditorEditItemTarget current;
	private StandardPojoItem item;

	public EditorEditStandardItemGUI(EditorSession session, EditorEditItemTarget current, StandardPojoItem item) {
		super(session, current, item, 6);
		this.current = current;
		this.item = item;
		placeSidebar();
		placeComponents(getComponentsPage());
	}

	@Override
	public StandardPojoItem getItem() { return item; }

	@Override
	public void refresh() {
		super.refresh();
		placeSidebar();
		placeComponents(getComponentsPage());
	}

	protected void placeSidebar() {
		for (int i = 0; i < 5; i++) getInventory().setItem(9 + 9 * i, FILL_BLACK);
		getInventory().setItem(9 * 1, item.createNew(true));
		getInventory().setItem(9 * 2, GET_ITEM);
		getInventory().setItem(9 * 3, RECYCLE_BIN);

		int page = getComponentsPage();
		int pageSize = 8 * 5;
		int maxPage = 1 + item.getComponents().size() / pageSize;

		if (page > 0) getInventory().setItem(9 * 4, new StackBuilder(new ItemStack(Material.ARROW))
			.name("&7<- &fPrevious Page &8| &f" + (page + 1) + "&7/" + maxPage)
			.getStack());
		if (page < maxPage - 1) getInventory().setItem(9 * 5, new StackBuilder(new ItemStack(Material.ARROW))
			.name("&fNext Page &8| &f" + (page + 1) + "&7/" + maxPage + " ->")
			.getStack());
	}

	protected void placeComponents(int page) {
		for (int i = 0; i < 8 * 5; i++) {
			int line = i / 8;
			int col = i % 8;
			int slot = 9 * (line + 1) + col + 1;
			int idx = 8 * 5 * getComponentsPage() + i;

			if (idx < item.getComponents().size()) {
				Component<?> component = item.getComponents().get(i);
				ItemStack icon = component instanceof EditorSupportedComponent<?> esc
					? new StackBuilder(new ItemStack(esc.getEditorDescription().icon()))
						.name("&6Component: &e" + esc.getEditorDescription().name())
						.appendLore("&7Contents:")
						.appendLore(new EditableObject(null, esc.getEditorNodes())
							.getPreviewLines().stream()
							.map(s -> "  &f" + s)
							.toArray(String[]::new))
						.appendLore(
							"",
							"&eLeft click &7to edit this component",
							"&eRight click &7to delete",
							"&eShift + Left click &7to move forward",
							"&eShift + Right click &7to move backward")
						.getStack()
					: new StackBuilder(new ItemStack(Material.PAPER))
						.name("&6Component: &e&o" + component.getClass().getCanonicalName())
						.appendLore(
							"&7This component can't be edited with",
							"&7in-game editor.",
							"",
							"&eRight click &7to delete",
							"&eShift + Left click &7to move forward",
							"&eShift + Right click &7to move backward")
						.getStack();
				getInventory().setItem(slot, icon);
			} else if (i == item.getComponents().size()) {
				getInventory().setItem(slot, ADD_COMPONENT);
			} else {
				getInventory().setItem(slot, null);
			}
		}
	}

	public int getComponentsPage() { return current.getAuxPage0(); }

	public void setComponentsPage(int page) {
		current.setAuxPage0(page);
	}

	@Override
	public void onInventoryClick(InventoryClickEvent event) {
		super.onInventoryClick(event);
		if (event.getClickedInventory() != getInventory()) return;

		int clickedSlot = event.getSlot();
		int cx = clickedSlot % 9;
		int cy = clickedSlot / 9;

		if (cx >= 1 && cy >= 1) {
			int idx = 8 * 5 * getComponentsPage() + (cy - 1) * 8 + cx - 1;

			if (idx == item.getComponents().size()) {
				ComponentsBrowserGUI gui = new ComponentsBrowserGUI(this, getSession(), current);
				event.getWhoClicked().openInventory(gui.getInventory());
				return;
			} else if (idx < item.getComponents().size()) {
				Component<?> component = item.getComponents().get(idx);

				if (!event.isShiftClick()) {
					if (event.isLeftClick()) {
						if (!(component instanceof EditorSupportedComponent<?> esc)) return;
						EditableObject proxy = new EditableObject(esc.getEditorDescription(), esc.getEditorNodes());
						EditableEditorTarget target = new EditableEditorTarget(this, proxy);
						getSession().replaceTarget(current, target);
						EditorGUI gui = target.createGUI(getSession());
						event.getWhoClicked().openInventory(gui.getInventory());
					}

					if (event.isRightClick()) {
						int position = item.getComponents().indexOf(component);
						if (position == -1) return;
						item.getComponents().remove(position);
						current.save();
						refresh();
					}
				} else {
					int position = item.getComponents().indexOf(component);
					if (position == -1) return;
					int direction = event.isLeftClick() ? -1 : event.isRightClick() ? 1 : 0;
					if (direction == 0) return;

					int newPos = position + direction;
					if (newPos < 0 || newPos >= item.getComponents().size()) return;

					item.getComponents().remove(position);
					item.getComponents().add(newPos, component);
					current.save();
					refresh();
				}

				return;
			}
		}

		if (clickedSlot == 9 * 2) {
			ItemStack stack = item.createNew(false);
			event.getWhoClicked().getInventory().addItem(stack);
			return;
		}

		if (clickedSlot == 9 * 3) {
			RecycleBin recycleBin = getSession().getPlugin().getRecycleBin();
			if (!recycleBin.throwToTrash("items", LocalDateTime.now(), item.getId(), item)) {
				event.getWhoClicked().sendMessage(TextUtils.colorize("&cFailed to move to recycle bin; "
					+ "item deletion has been aborted. Please check console for errors."));
				event.getWhoClicked().closeInventory();
				return;
			}

			current.getRegistry().unregister(item.getId());
			EditorBrowseItemsTarget target = new EditorBrowseItemsTarget(current.getRegistry(), 0);
			getSession().replaceTarget(current, target);
			EditorGUI gui = target.createGUI(getSession());
			event.getWhoClicked().openInventory(gui.getInventory());
			event.getWhoClicked()
				.sendMessage(TextUtils.colorize("&eItem '" + item.getId() + "' has been moved to Recycle Bin!"));
			return;
		}

		int page = getComponentsPage();
		int pageSize = 8 * 5;
		int maxPage = 1 + item.getComponents().size() / pageSize;

		if (clickedSlot == 9 * 4 && page > 0) {
			setComponentsPage(getComponentsPage() - 1);
			refresh();
			return;
		}

		if (clickedSlot == 9 * 5 && page < maxPage - 1) {
			setComponentsPage(getComponentsPage() + 1);
			refresh();
			return;
		}
	}
}

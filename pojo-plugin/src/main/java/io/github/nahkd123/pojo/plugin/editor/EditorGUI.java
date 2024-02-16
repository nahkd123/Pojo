package io.github.nahkd123.pojo.plugin.editor;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import io.github.nahkd123.pojo.plugin.editor.browse.EditorBrowseItemsTarget;
import io.github.nahkd123.pojo.plugin.gui.PluginGUI;
import io.github.nahkd123.pojo.plugin.gui.StackBuilder;

public abstract class EditorGUI implements PluginGUI {
	public static final ItemStack NEW_EDITOR_TAB = new StackBuilder(new ItemStack(Material.LIME_STAINED_GLASS_PANE))
		.name("&a+ &eNew Editor Tab")
		.appendLore(
			"&7Open items browser to pick an item and edit.",
			"&7&oOnly items editing are supported for now!",
			"",
			"&eClick &7to open items browser")
		.getStack();

	private Inventory inventory;
	private EditorSession session;
	private EditorTarget current;

	public EditorGUI(EditorSession session, EditorTarget current, int rows) {
		this.session = session;
		this.current = current;
		this.inventory = Bukkit.createInventory(this, rows * 9, "Editor >> " + current.getTargetName());
		placeTabs();
	}

	@Override
	public Inventory getInventory() { return inventory; }

	public EditorSession getSession() { return session; }

	public EditorTarget getCurrent() { return current; }

	public void refresh() {
		placeTabs();
	}

	protected void placeTabs() {
		// 1st and 9th slot is for page turning icons
		// 2nd to 8th is for tabs in current page
		int icons = session.getTargets().size();
		int maxPage = 1 + icons / 7;
		int currentPage = session.getTabPage();
		for (int i = 0; i < 9; i++) inventory.setItem(i, FILL_BLACK);

		if (currentPage > 0) inventory.setItem(0, new StackBuilder(new ItemStack(Material.ARROW))
			.name("&7<- &fPrevious Tabs Page &8| &f" + (currentPage + 1) + "&7/" + maxPage)
			.appendLore("&eClick &7to turn to previous page")
			.getStack());
		if (currentPage < maxPage - 1) inventory.setItem(8, new StackBuilder(new ItemStack(Material.ARROW))
			.name("&fNext Tabs Page &8| &f" + (currentPage + 1) + "&7/" + maxPage + " ->")
			.appendLore("&eClick &7to turn to next page")
			.getStack());

		for (int i = 0; i < 7; i++) {
			int idx = currentPage * 7 + i;
			int slot = 1 + i;
			if (idx >= icons) {
				inventory.setItem(slot, NEW_EDITOR_TAB);
				break;
			} else
				inventory.setItem(slot, new StackBuilder(session.getTargets().get(idx).getTargetDisplay().clone())
					.appendLore(
						"",
						"&eLeft click &7to open",
						"&eRight click &7to close")
					.getStack());
		}
	}

	@Override
	public void onInventoryClick(InventoryClickEvent event) {
		if (event.getClickedInventory() != inventory) return;
		int clickedSlot = event.getSlot();

		if (clickedSlot >= 0 && clickedSlot < 9) {
			int icons = session.getTargets().size();
			int maxPage = 1 + icons / 7;
			int currentPage = session.getTabPage();

			if (clickedSlot == 0 && currentPage > 0) {
				session.setTabPage(session.getTabPage() - 1);
				refresh();
				return;
			}

			if (clickedSlot == 8 && currentPage < maxPage - 1) {
				session.setTabPage(session.getTabPage() + 1);
				refresh();
				return;
			}

			int clickedIdx = currentPage * 7 + clickedSlot - 1;
			if (clickedIdx == icons) {
				EditorBrowseItemsTarget target = new EditorBrowseItemsTarget(session.getPlugin().getItems(), 0);
				session.getTargets().add(target);
				EditorGUI gui = target.createGUI(session);
				event.getWhoClicked().openInventory(gui.inventory);
			} else if (clickedIdx >= 0 && clickedIdx < icons) {
				EditorTarget tabTarget = session.getTargets().get(clickedIdx);

				if (event.isRightClick()) {
					int idx = session.getTargets().indexOf(tabTarget);
					session.getTargets().remove(idx);

					if (session.getTargets().size() == 0) {
						tabTarget = new EditorBrowseItemsTarget(session.getPlugin().getItems(), 0);
						session.getTargets().add(tabTarget);
					} else {
						tabTarget = session.getTargets().get(session.getTargets().size() - 1);
					}
				}

				EditorGUI gui = tabTarget.createGUI(session);
				event.getWhoClicked().openInventory(gui.inventory);
			}
		}
	}

	@Override
	public void onInventoryClose(InventoryCloseEvent event) {}
}

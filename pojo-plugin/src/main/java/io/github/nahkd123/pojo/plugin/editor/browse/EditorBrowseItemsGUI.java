package io.github.nahkd123.pojo.plugin.editor.browse;

import java.io.File;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import io.github.nahkd123.pojo.api.item.PojoItem;
import io.github.nahkd123.pojo.api.item.standard.StandardPojoItem;
import io.github.nahkd123.pojo.api.registry.UserDefinedId;
import io.github.nahkd123.pojo.api.utils.TextUtils;
import io.github.nahkd123.pojo.plugin.PojoPlugin;
import io.github.nahkd123.pojo.plugin.editor.EditorGUI;
import io.github.nahkd123.pojo.plugin.editor.EditorSession;
import io.github.nahkd123.pojo.plugin.editor.item.EditorEditItemTarget;
import io.github.nahkd123.pojo.plugin.gui.StackBuilder;

public class EditorBrowseItemsGUI extends EditorGUI {
	private List<PojoItem> pageItems = null;
	private EditorBrowseItemsTarget current;

	public EditorBrowseItemsGUI(EditorSession session, EditorBrowseItemsTarget current) {
		super(session, current, 6);
		this.current = current;
		placePage();
		placeBottomBar();
	}

	protected void placePage() {
		if (pageItems == null) pageItems = current.getIDsWithPagination(current.getPage(), 9 * 4);

		for (int i = 0; i < 9 * 4; i++) {
			int slot = i + 9;

			if (i < pageItems.size()) {
				PojoItem pojoItem = pageItems.get(i);
				String type = pojoItem instanceof StandardPojoItem ? "Standard"
					: "&o" + pojoItem.getClass().getCanonicalName() + " &7&o(Unsupported in editors)";
				boolean canEdit = pojoItem instanceof StandardPojoItem;

				ItemStack icon = new StackBuilder(pojoItem.createNew(true))
					.appendLore(
						"&8----------------",
						"&7Item ID: &f" + pojoItem.getId(),
						"&7Type: &f" + type,
						"",
						canEdit ? "&eClick &7to edit this item" : "&cCan't be edited with in-game editor")
					.getStack();
				getInventory().setItem(slot, icon);
			} else {
				getInventory().setItem(slot, null);
			}
		}
	}

	protected void placeBottomBar() {
		for (int i = 0; i < 9; i++) getInventory().setItem(9 * 5 + i, FILL_BLACK);
		getInventory().setItem(9 * 5 + 1, new StackBuilder(new ItemStack(Material.ARROW))
			.name("&7<- &fPrevious Page &8| &7Page &f" + (current.getPage() + 1))
			.appendLore("&eClick &7to turn to previous page")
			.getStack());
		getInventory().setItem(9 * 5 + 7, new StackBuilder(new ItemStack(Material.ARROW))
			.name("&fNext Page &8| &7Page &f" + (current.getPage() + 1) + " &7->")
			.appendLore("&eClick &7to turn to next page")
			.getStack());
		getInventory().setItem(9 * 5 + 4, new StackBuilder(new ItemStack(Material.CHEST))
			.name("&a+ &eCreate new item")
			.appendLore(
				"&eClick &7to create a new item",
				"&7You'll then enter the ID of the item in",
				"&7the chat.")
			.getStack());
	}

	@Override
	public void refresh() {
		super.refresh();
		placePage();
		placeBottomBar();
	}

	@Override
	public void onInventoryClick(InventoryClickEvent event) {
		super.onInventoryClick(event);
		if (event.getClickedInventory() != getInventory()) return;

		int clickedSlot = event.getSlot();
		if (clickedSlot >= 9 && clickedSlot < 9 * 5) {
			int idx = clickedSlot - 9;

			if (idx < pageItems.size()) {
				PojoItem item = pageItems.get(idx);
				EditorEditItemTarget target = new EditorEditItemTarget(getSession().getPlugin().getItems(), item);
				EditorGUI gui = target.createGUI(getSession());
				if (gui == null) return;

				getSession().replaceTarget(current, target);
				event.getWhoClicked().openInventory(gui.getInventory());
			}
		}

		if (clickedSlot == 9 * 5 + 1) {
			previousPage();
			return;
		}

		if (clickedSlot == 9 * 5 + 7) {
			nextPage();
			return;
		}

		if (clickedSlot == 9 * 5 + 4) {
			event.getWhoClicked().sendMessage(TextUtils.colorize("&8&l================"));
			event.getWhoClicked().sendMessage(TextUtils.colorize("&7You are creating a new Pojo Item "
				+ "(Standard variant)."));
			event.getWhoClicked().sendMessage(TextUtils.colorize("&7The ID of your item must follows this format: "
				+ "&6[&e<namespace>&6:]&e<id> &7, where [<namespace>:] is the optional part."));
			event.getWhoClicked().sendMessage(TextUtils.colorize("&7Type '&fpojo:cancel&7' to cancel item creation."));
			event.getWhoClicked().sendMessage(TextUtils.colorize("&7Enter the ID of the item in the chat:"));

			getSession().setHeldInput((session, input) -> {
				try {
					UserDefinedId id = UserDefinedId.fromString(input);
					session.setHeldInput(null);
					event.getWhoClicked().sendMessage(TextUtils.colorize("&8&l================"));

					if (id.equals(new UserDefinedId("pojo", "cancel"))) {
						event.getWhoClicked().openInventory(getInventory());
						return;
					}

					PojoPlugin plugin = getSession().getPlugin();
					File file = plugin.getItems().getItemFile(id);
					plugin.copyResourceTo(file, "sample-item-standard.yml");
					plugin.getItems().loadFromFile(id, file);

					PojoItem item = plugin.getItems().get(id);
					EditorEditItemTarget target = new EditorEditItemTarget(plugin.getItems(), item);
					session.replaceTarget(current, target);
					EditorGUI gui = target.createGUI(session);
					event.getWhoClicked().openInventory(gui.getInventory());
				} catch (IllegalArgumentException e) {
					event.getWhoClicked().sendMessage(TextUtils.colorize("&cInvalid input! &fPlease re-enter your item"
						+ "ID following the &6[&e<namespace>&6:]&e<id> &fformat."));
				}
			});

			event.getWhoClicked().closeInventory();
			return;
		}
	}

	public void previousPage() {
		if (current.getPage() <= 0) return;
		current.setPage(current.getPage() - 1);
		pageItems = null;
		refresh();
	}

	public void nextPage() {
		if (pageItems.size() != 9 * 4) return;
		current.setPage(current.getPage() + 1);
		pageItems = null;
		refresh();
	}
}

package io.github.nahkd123.pojo.plugin.editor.item;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import io.github.nahkd123.pojo.api.editor.EditableObject;
import io.github.nahkd123.pojo.api.item.standard.component.Component;
import io.github.nahkd123.pojo.api.item.standard.component.ComponentsFactory;
import io.github.nahkd123.pojo.api.item.standard.component.DefaultedComponentsFactory;
import io.github.nahkd123.pojo.api.item.standard.component.EditorComponentsFactory;
import io.github.nahkd123.pojo.api.item.standard.component.EditorSupportedComponent;
import io.github.nahkd123.pojo.plugin.editor.EditorGUI;
import io.github.nahkd123.pojo.plugin.editor.EditorSession;
import io.github.nahkd123.pojo.plugin.editor.EditorTarget;
import io.github.nahkd123.pojo.plugin.editor.editable.EditableEditorTarget;
import io.github.nahkd123.pojo.plugin.gui.StackBuilder;

public class ComponentsBrowserGUI extends EditorGUI {
	private EditorEditStandardItemGUI previous;
	private List<ComponentsFactory<?>> factories;
	private int page = 0;

	public ComponentsBrowserGUI(EditorEditStandardItemGUI previous, EditorSession session, EditorTarget current) {
		super(session, current, 6);
		this.previous = previous;
		factories = new ArrayList<>(ComponentsFactory.getAllFactories().values());
		placeSidebar();
		placeComponents();
	}

	@Override
	public void refresh() {
		super.refresh();
		placeSidebar();
		placeComponents();
	}

	protected void placeSidebar() {
		for (int i = 0; i < 5; i++) getInventory().setItem(9 + 9 * i, FILL_BLACK);
		if (previous != null) getInventory().setItem(9 * 1, new StackBuilder(new ItemStack(Material.ARROW))
			.name("&7<-- &fGo back: &e" + previous.getCurrent().getTargetName())
			.appendLore("&eClick &7to go back to previous menu")
			.getStack());
		getInventory().setItem(9 * 2, new StackBuilder(new ItemStack(Material.MINECART))
			.name("&eComponents Browser")
			.appendLore("&7&oFind your favorite component here!")
			.getStack());

		int maxPage = 1 + factories.size() / 8 * 5;
		if (page > 0) getInventory().setItem(9 * 4, new StackBuilder(new ItemStack(Material.ARROW))
			.name("&7<- &fPrevious Page &8| &f" + (page + 1) + "&7/" + maxPage)
			.getStack());
		if (page < maxPage - 1) getInventory().setItem(9 * 5, new StackBuilder(new ItemStack(Material.ARROW))
			.name("&fNext Page &8| &f" + (page + 1) + "&7/" + maxPage + " ->")
			.getStack());
	}

	protected void placeComponents() {
		for (int i = 0; i < 8 * 5; i++) {
			int line = i / 8;
			int col = i % 8;
			int slot = 9 * (line + 1) + col + 1;
			int idx = 8 * 5 * page + i;

			if (idx < factories.size()) {
				ComponentsFactory<?> factory = factories.get(idx);
				ItemStack icon;

				if (factory instanceof DefaultedComponentsFactory<?> defaulted) {
					String name = defaulted instanceof EditorComponentsFactory<?> ecf
						? ecf.getEditorDescription().name()
						: "&o" + defaulted.getClass().getCanonicalName();
					String[] desc = defaulted instanceof EditorComponentsFactory<?> ecf
						? Stream.of(ecf.getEditorDescription().description())
							.map(s -> "&7" + s)
							.toArray(String[]::new)
						: new String[] {
							"&7&oThis component might not support all",
							"&7&oeditor features!"
						};
					Material iconMat = defaulted instanceof EditorComponentsFactory<?> ecf
						? ecf.getEditorDescription().icon()
						: Material.CHEST_MINECART;

					icon = new StackBuilder(new ItemStack(iconMat))
						.name("&6Component: &e" + name)
						.appendLore(desc)
						.appendLore(
							"",
							"&eClick &7to add to current item")
						.getStack();
				} else {
					icon = new StackBuilder(new ItemStack(Material.MINECART))
						.name("&6Component: &e&o" + factory.getClass().getCanonicalName())
						.appendLore(
							"&7&oThis component can't be added through",
							"&7&oin-game editor!")
						.getStack();
				}

				getInventory().setItem(slot, icon);
			} else {
				getInventory().setItem(slot, null);
			}
		}
	}

	@Override
	public void onInventoryClick(InventoryClickEvent event) {
		super.onInventoryClick(event);
		if (event.getClickedInventory() != getInventory()) return;

		int clickedSlot = event.getSlot();

		if (clickedSlot == 9 && previous != null) {
			event.getWhoClicked().openInventory(previous.getInventory());
			return;
		}

		if (clickedSlot == 9 * 4 && page > 0) {
			page -= 1;
			refresh();
			return;
		}

		if (clickedSlot == 9 * 5 && page < factories.size() / 8 * 5) {
			page += 1;
			refresh();
			return;
		}

		int cx = clickedSlot % 9;
		int cy = clickedSlot / 9;

		if (cx >= 1 && cy >= 1) {
			int idx = 8 * 5 * page + (cy - 1) * 8 + cx - 1;

			if (idx < factories.size()) {
				ComponentsFactory<?> factory = factories.get(idx);
				if (!(factory instanceof DefaultedComponentsFactory<?> defaulted)) return;
				Component<?> component = defaulted.createDefault();
				previous.getItem().getComponents().add(component);

				if (component instanceof EditorSupportedComponent<?> esc) {
					EditableObject proxy = new EditableObject(esc.getEditorDescription(), esc.getEditorNodes());
					EditableEditorTarget target = new EditableEditorTarget(previous, proxy);
					getSession().replaceTarget(getCurrent(), target);
					EditorGUI gui = target.createGUI(getSession());
					event.getWhoClicked().openInventory(gui.getInventory());
				} else {
					previous.refresh();
					event.getWhoClicked().openInventory(previous.getInventory());
				}
			}

			return;
		}
	}
}

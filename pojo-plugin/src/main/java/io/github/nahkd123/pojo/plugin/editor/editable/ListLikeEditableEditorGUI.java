package io.github.nahkd123.pojo.plugin.editor.editable;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import io.github.nahkd123.pojo.api.editor.Editable;
import io.github.nahkd123.pojo.api.editor.EditableObject;
import io.github.nahkd123.pojo.plugin.editor.EditorGUI;
import io.github.nahkd123.pojo.plugin.editor.EditorSession;
import io.github.nahkd123.pojo.plugin.gui.StackBuilder;

public class ListLikeEditableEditorGUI extends EditableEditorGUI {
	protected List<Editable> allEditables;
	private EditableEditorTarget current;

	public ListLikeEditableEditorGUI(EditorGUI previous, EditorSession session, EditableEditorTarget current, List<Editable> allEditables) {
		super(previous, session, current, 6);
		this.current = current;
		this.allEditables = allEditables;
		updateEditablesList();
		placePageButtonsToSidebar();
		placePage();
	}

	protected void updateEditablesList() {
		if (current.getEditable() instanceof EditableObject obj) allEditables = obj.getFields();
	}

	public int getPage() { return current.getAuxPage(); }

	public void setPage(int page) {
		current.setAuxPage(page);
	}

	public List<Editable> getAllEditables() { return allEditables; }

	@Override
	public void refresh() {
		updateEditablesList();
		super.refresh();
		placePageButtonsToSidebar();
		placePage();
	}

	protected void placePageButtonsToSidebar() {
		super.placeSidebar();
		int page = current.getAuxPage();
		int maxPage = 1 + allEditables.size() / (8 * 5);

		if (page > 0) getInventory().setItem(9 * 4, new StackBuilder(new ItemStack(Material.ARROW))
			.name("&7<- &fPrevious Page &8| &f" + (page + 1) + "&7/" + maxPage)
			.getStack());
		if (page < maxPage - 1) getInventory().setItem(9 * 5, new StackBuilder(new ItemStack(Material.ARROW))
			.name("&fNext Page &8| &f" + (page + 1) + "&7/" + maxPage + " ->")
			.getStack());
	}

	protected void placePage() {
		int page = current.getAuxPage();

		for (int i = 0; i < 8 * 5; i++) {
			int idx = 8 * 5 * page + i;
			int slot = ((i / 8 + 1) * 9) + (i % 8) + 1;

			if (idx < allEditables.size()) {
				ItemStack icon = createIconForElement(allEditables.get(i));
				getInventory().setItem(slot, icon);
			} else if (idx == allEditables.size()) {
				getInventory().setItem(slot, createAddIcon());
			} else {
				getInventory().setItem(slot, null);
			}
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected ItemStack createIconForElement(Editable editable) {
		EditableClickProcessor processor = PROCESSORS.get(editable.getClass());
		if (processor == null) return null;
		return processor.createIcon(editable);
	}

	protected ItemStack createAddIcon() {
		return null;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected void onClickEditable(Editable editable, InventoryClickEvent event) {
		EditableClickProcessor processor = PROCESSORS.get(editable.getClass());
		if (processor != null) processor.onClick(editable, event, this);
	}

	protected void onClickAdd(InventoryClickEvent event) {
		// Up to subclass
	}

	@Override
	public void onInventoryClick(InventoryClickEvent event) {
		super.onInventoryClick(event);
		if (event.getClickedInventory() != getInventory()) return;

		int clickedSlot = event.getSlot();
		int cx = clickedSlot % 9;
		int cy = clickedSlot / 9;

		if (cx >= 1 && cy >= 1) {
			int idx = 8 * 5 * current.getAuxPage() + (cy - 1) * 8 + cx - 1;

			if (idx == allEditables.size()) {
				onClickAdd(event);
				return;
			} else if (idx < allEditables.size()) {
				Editable editable = allEditables.get(idx);
				onClickEditable(editable, event);
				return;
			}
		}
	}
}

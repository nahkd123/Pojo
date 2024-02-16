package io.github.nahkd123.pojo.plugin.editor.editable;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import io.github.nahkd123.pojo.api.editor.Editable;
import io.github.nahkd123.pojo.api.editor.EditableBool;
import io.github.nahkd123.pojo.api.editor.EditableInteger;
import io.github.nahkd123.pojo.api.editor.EditableList;
import io.github.nahkd123.pojo.api.editor.EditableString;
import io.github.nahkd123.pojo.api.utils.TextUtils;
import io.github.nahkd123.pojo.plugin.editor.EditorGUI;
import io.github.nahkd123.pojo.plugin.editor.EditorSession;
import io.github.nahkd123.pojo.plugin.gui.StackBuilder;

/**
 * <p>
 * Editable GUIs are reserved for {@link Editable} that have complex actions,
 * like {@link EditableString} with "clear all" and "set new value", or
 * {@link EditableList} with "add new", "remove" and "clear list".
 * </p>
 */
public abstract class EditableEditorGUI extends EditorGUI {
	protected static final Map<Class<?>, EditableClickProcessor<?>> PROCESSORS = new HashMap<>();

	protected static <T extends Editable> void addProcessor(Class<T> clazz, EditableClickProcessor<T> processor) {
		PROCESSORS.put(clazz, processor);
	}

	private EditorGUI previous;
	private EditableEditorTarget current;
	private int rows;

	public EditableEditorGUI(EditorGUI previous, EditorSession session, EditableEditorTarget current, int rows) {
		super(session, current, rows);
		this.previous = previous;
		this.current = current;
		this.rows = rows;
		placeSidebar();
	}

	public EditorGUI getPrevious() { return previous; }

	@Override
	public void refresh() {
		super.refresh();
		placeSidebar();
	}

	protected void placeSidebar() {
		for (int i = 0; i < rows - 1; i++) getInventory().setItem(9 + 9 * i, FILL_BLACK);
		if (previous != null) getInventory().setItem(9 * 1, new StackBuilder(new ItemStack(Material.ARROW))
			.name("&7<-- &fGo back: &e" + previous.getCurrent().getTargetName())
			.appendLore("&eClick &7to go back to previous menu")
			.getStack());
		getInventory().setItem(9 * 2, current.getTargetDisplay());
	}

	@Override
	public void onInventoryClick(InventoryClickEvent event) {
		super.onInventoryClick(event);
		if (event.getClickedInventory() != getInventory()) return;

		int clickedSlot = event.getSlot();

		if (clickedSlot == 9 * 1 && previous != null) {
			getSession().replaceTarget(getCurrent(), previous.getCurrent());
			previous.refresh();
			event.getWhoClicked().openInventory(previous.getInventory());
			return;
		}
	}

	static {
		addProcessor(
			EditableString.class,
			editable -> new StackBuilder(new ItemStack(editable.getDescription().icon()))
				.name("&6Text: &e" + editable.getDescription().name())
				.appendLore(Stream.of(editable.getDescription().description())
					.map(s -> "&7" + TextUtils.colorize(s))
					.toArray(String[]::new))
				.appendLore(
					"",
					"&7Contents: &f&o" + (editable.getValue() != null ? editable.getValue() : "&7&o(empty)"))
				.appendLore(
					"",
					"&eLeft click &7to open")
				.getStack());

		addProcessor(
			EditableInteger.class,
			editable -> new StackBuilder(new ItemStack(editable.getDescription().icon()))
				.name("&6Integer: &e" + editable.getDescription().name())
				.appendLore(Stream.of(editable.getDescription().description())
					.map(s -> "&7" + TextUtils.colorize(s))
					.toArray(String[]::new))
				.appendLore(
					"",
					"&7Value: &b" + editable.getValue())
				.appendLore(
					"",
					"&eLeft click &7to open")
				.getStack());

		addProcessor(
			EditableBool.class,
			new EditableClickProcessor<>() {
				public void onClick(EditableBool editable, InventoryClickEvent event, EditableEditorGUI gui) {
					editable.setValue(!editable.getValue());
					gui.refresh();
				};

				@Override
				public ItemStack createIcon(EditableBool editable) {
					return new StackBuilder(new ItemStack(editable.getValue() ? Material.LIME_DYE : Material.GRAY_DYE))
						.name("&6Boolean: &e" + editable.getDescription().name())
						.appendLore(Stream.of(editable.getDescription().description())
							.map(s -> "&7" + TextUtils.colorize(s))
							.toArray(String[]::new))
						.appendLore(
							"",
							"&7Value: " + (editable.getValue() ? "&aYes" : "&cNo"))
						.appendLore(
							"",
							"&eLeft click &7to toggle")
						.getStack();
				}
			});

		addProcessor(
			EditableList.class,
			editable -> new StackBuilder(new ItemStack(editable.getDescription().icon()))
				.name("&6List: &e" + editable.getDescription().name())
				.appendLore(Stream.of(editable.getDescription().description())
					.map(s -> "&7" + TextUtils.colorize(s))
					.toArray(String[]::new))
				.appendLore(
					"",
					"&7Contents (" + editable.size() + " entries)")
				.appendLore(editable.getPreviewLines()
					.stream()
					.map(s -> "  &f" + s)
					.toArray(String[]::new))
				.appendLore(
					"",
					"&eLeft click &7to open")
				.getStack());
	}

}

package io.github.nahkd123.pojo.plugin.editor.browse;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import io.github.nahkd123.pojo.api.item.ItemsRegistry;
import io.github.nahkd123.pojo.api.item.PojoItem;
import io.github.nahkd123.pojo.plugin.editor.EditorGUI;
import io.github.nahkd123.pojo.plugin.editor.EditorSession;
import io.github.nahkd123.pojo.plugin.editor.EditorTarget;
import io.github.nahkd123.pojo.plugin.gui.StackBuilder;

public class EditorBrowseItemsTarget implements EditorTarget {
	public static final ItemStack DISPLAY = new StackBuilder(new ItemStack(Material.BOOK))
		.name("&eBrowse Items")
		.appendLore("&7&oBrowse all items in this tab.")
		.getStack();
	private ItemsRegistry registry;
	private int page;

	public EditorBrowseItemsTarget(ItemsRegistry registry, int page) {
		this.registry = registry;
		this.page = page;
	}

	public ItemsRegistry getRegistry() { return registry; }

	public int getPage() { return page; }

	public void setPage(int page) { this.page = page; }

	/**
	 * <p>
	 * Get all items with pagination. The returned list have a size no larger than
	 * {@code pageSize}.
	 * </p>
	 * 
	 * @param page     The current page number, starts at {@code 0}.
	 * @param pageSize The page size, which is a maximum number of IDs in the
	 *                 returned list.
	 * @return All items in the specified page.
	 */
	public List<PojoItem> getIDsWithPagination(int page, int pageSize) {
		int skip = page * pageSize;
		return registry.getAllIDs().stream()
			.skip(skip).limit(pageSize)
			.map(registry::get)
			.toList();
	}

	@Override
	public String getTargetName() { return "Browse Items"; }

	@Override
	public ItemStack getTargetDisplay() { return DISPLAY; }

	@Override
	public boolean save() {
		// Always return successful for browsing menu
		return true;
	}

	@Override
	public EditorGUI createGUI(EditorSession session) {
		return new EditorBrowseItemsGUI(session, this);
	}
}

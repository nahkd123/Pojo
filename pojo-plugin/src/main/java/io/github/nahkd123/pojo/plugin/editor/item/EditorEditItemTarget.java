package io.github.nahkd123.pojo.plugin.editor.item;

import org.bukkit.inventory.ItemStack;

import io.github.nahkd123.pojo.api.item.ItemsRegistry;
import io.github.nahkd123.pojo.api.item.PojoItem;
import io.github.nahkd123.pojo.api.item.standard.StandardPojoItem;
import io.github.nahkd123.pojo.api.utils.ItemUtils;
import io.github.nahkd123.pojo.plugin.editor.EditorGUI;
import io.github.nahkd123.pojo.plugin.editor.EditorSession;
import io.github.nahkd123.pojo.plugin.editor.EditorTarget;
import io.github.nahkd123.pojo.plugin.gui.StackBuilder;

public class EditorEditItemTarget implements EditorTarget {
	private ItemsRegistry registry;
	private PojoItem item;
	private int auxPage0;

	public EditorEditItemTarget(ItemsRegistry registry, PojoItem item) {
		this.registry = registry;
		this.item = item;
	}

	public ItemsRegistry getRegistry() { return registry; }

	public PojoItem getItem() { return item; }

	public int getAuxPage0() { return auxPage0; }

	public void setAuxPage0(int auxPage0) { this.auxPage0 = auxPage0; }

	@Override
	public String getTargetName() { return item.getId().toString(); }

	@Override
	public ItemStack getTargetDisplay() {
		ItemStack stack = item.createNew(true);
		String name = ItemUtils.toFriendlyName(stack);

		return new StackBuilder(stack)
			.name("&eItem: &f" + name)
			.appendLore(
				"&8----------------",
				"&7Item ID: &f" + item.getId())
			.getStack();
	}

	@Override
	public boolean save() {
		try {
			registry.registerNew(item);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public EditorGUI createGUI(EditorSession session) {
		if (item instanceof StandardPojoItem std) return new EditorEditStandardItemGUI(session, this, std);
		return null;
	}
}

package io.github.nahkd123.pojo.plugin.gui;

import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

public interface PluginGUI extends InventoryHolder {
	public void onInventoryClick(InventoryClickEvent event);

	public void onInventoryClose(InventoryCloseEvent event);

	public static final ItemStack FILL_BLACK = new StackBuilder(new ItemStack(Material.BLACK_STAINED_GLASS_PANE))
		.name("&0")
		.getStack();
}

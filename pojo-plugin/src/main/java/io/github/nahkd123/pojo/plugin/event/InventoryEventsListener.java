package io.github.nahkd123.pojo.plugin.event;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;

import io.github.nahkd123.pojo.plugin.gui.PluginGUI;

public class InventoryEventsListener implements Listener {
	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		Inventory primary = event.getInventory();

		if (primary.getHolder() instanceof PluginGUI gui) {
			gui.onInventoryClick(event);
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onInventoryClose(InventoryCloseEvent event) {
		Inventory primary = event.getInventory();
		if (primary.getHolder() instanceof PluginGUI gui) gui.onInventoryClose(event);
	}
}

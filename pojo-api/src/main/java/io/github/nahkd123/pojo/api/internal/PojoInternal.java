package io.github.nahkd123.pojo.api.internal;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.NamespacedKey;
import org.bukkit.plugin.Plugin;

import io.github.nahkd123.pojo.api.item.ItemsRegistry;
import io.github.nahkd123.pojo.api.item.standard.component.ComponentsFactory;

public class PojoInternal {
	private static PojoInternal instance;
	private Plugin plugin;
	private PojoKeys keys;
	private Map<NamespacedKey, ComponentsFactory<?>> components = new HashMap<>();
	private ItemsRegistry items;

	public PojoInternal(Plugin plugin, PojoKeys keys, ItemsRegistry items) {
		this.plugin = plugin;
		this.keys = keys;
		this.items = items;
		PojoInternal.instance = this;
	}

	public Plugin getPlugin() { return plugin; }

	public PojoKeys getKeys() { return keys; }

	public ItemsRegistry getItems() { return items; }

	public Map<NamespacedKey, ComponentsFactory<?>> getComponents() { return components; }

	public static PojoKeys keys() {
		return instance.getKeys();
	}

	public static PojoInternal instance() {
		if (instance == null)
			throw new IllegalStateException("PojoInternal is not initialized (getInstance() before onLoad()?)");
		return instance;
	}
}

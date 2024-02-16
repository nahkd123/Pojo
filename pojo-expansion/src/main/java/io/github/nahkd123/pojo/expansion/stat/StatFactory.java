package io.github.nahkd123.pojo.expansion.stat;

import java.util.Collections;
import java.util.Map;

import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

import io.github.nahkd123.pojo.expansion.PojoExpansionPlugin;
import io.github.nahkd123.pojo.expansion.stat.value.StatValue;

public interface StatFactory {
	public Stat createDefault(StatOperation initialOperation, StatValue initialValue);

	public Stat createFromConfig(StatOperation operation, StatValue value, ConfigurationSection config);

	default void register(NamespacedKey typeId) {
		Map<NamespacedKey, StatFactory> map = JavaPlugin.getPlugin(PojoExpansionPlugin.class).getStats();
		if (map.containsKey(typeId))
			throw new IllegalStateException("Stat with type ID '" + typeId + "' is already registered");
		map.put(typeId, this);
	}

	public static Map<NamespacedKey, StatFactory> getAllFactories() {
		return Collections.unmodifiableMap(JavaPlugin.getPlugin(PojoExpansionPlugin.class).getStats());
	}
}

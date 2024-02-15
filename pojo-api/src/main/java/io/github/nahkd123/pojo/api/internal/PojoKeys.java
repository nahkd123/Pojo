package io.github.nahkd123.pojo.api.internal;

import org.bukkit.NamespacedKey;
import org.bukkit.plugin.Plugin;

/**
 * <p>
 * A collection of Pojo namespace keys.
 * </p>
 */
public class PojoKeys {
	public final NamespacedKey id;
	public final NamespacedKey displayMode;

	public PojoKeys(Plugin plugin) {
		id = new NamespacedKey(plugin, "id");
		displayMode = new NamespacedKey(plugin, "display_mode");
	}
}

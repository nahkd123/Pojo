package io.github.nahkd123.pojo.api.editor;

import org.bukkit.Material;

public record NodeDescription(Material icon, String name, String... description) {
	public NodeDescription(String name, String... description) {
		this(Material.RED_STAINED_GLASS_PANE, name, description);
	}
}

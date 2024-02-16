package io.github.nahkd123.pojo.expansion.utils;

import org.bukkit.Material;
import org.bukkit.inventory.EquipmentSlot;

import io.github.nahkd123.pojo.api.editor.NodeDescription;

public enum PojoEquipmentSlot {
	// @formatter:off
	ALL(null, new NodeDescription(Material.DIAMOND_BLOCK, "All slots", "Applies to all equipment slots")),
	MAIN_HAND(EquipmentSlot.HAND, new NodeDescription(Material.DIAMOND_SWORD, "Main hand", "Applies to main hand")),
	OFF_HAND(EquipmentSlot.OFF_HAND, new NodeDescription(Material.GOLDEN_SWORD, "Off hand", "Applies to off hand")),
	HEAD(EquipmentSlot.HEAD, new NodeDescription(Material.DIAMOND_HELMET, "Head", "Applies to helmet slot")),
	CHEST(EquipmentSlot.CHEST, new NodeDescription(Material.DIAMOND_HELMET, "Chest", "Applies to chestplate slot")),
	LEGS(EquipmentSlot.LEGS, new NodeDescription(Material.DIAMOND_HELMET, "Legs", "Applies to leggings slot")),
	FEET(EquipmentSlot.FEET, new NodeDescription(Material.DIAMOND_HELMET, "Feet", "Applies to boots slot"));
	// @formatter:on

	private EquipmentSlot bukkit;
	private NodeDescription description;

	private PojoEquipmentSlot(EquipmentSlot bukkit, NodeDescription description) {
		this.bukkit = bukkit;
		this.description = description;
	}

	public EquipmentSlot getBukkit() { return bukkit; }

	public NodeDescription getDescription() { return description; }
}

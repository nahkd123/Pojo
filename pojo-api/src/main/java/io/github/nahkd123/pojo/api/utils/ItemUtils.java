package io.github.nahkd123.pojo.api.utils;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class ItemUtils {
	public static String toFriendlyName(Material mat) {
		return EnumUtils.toFriendlyName(mat);
	}

	public static String toFriendlyName(ItemStack stack) {
		if (!stack.hasItemMeta() || !stack.getItemMeta().hasDisplayName()) return toFriendlyName(stack.getType());
		return stack.getItemMeta().getDisplayName();
	}
}

package io.github.nahkd123.pojo.api.utils;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class ItemUtils {
	public static String toFriendlyName(Material mat) {
		return Stream.of(mat.toString().split("_"))
			.map(v -> v.substring(0, 1).toUpperCase() + v.substring(1).toLowerCase())
			.collect(Collectors.joining(" "));
	}

	public static String toFriendlyName(ItemStack stack) {
		if (!stack.hasItemMeta() || !stack.getItemMeta().hasDisplayName()) return toFriendlyName(stack.getType());
		return stack.getItemMeta().getDisplayName();
	}
}

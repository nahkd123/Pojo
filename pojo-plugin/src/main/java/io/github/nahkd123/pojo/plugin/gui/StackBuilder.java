package io.github.nahkd123.pojo.plugin.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import io.github.nahkd123.pojo.api.utils.TextUtils;

public class StackBuilder {
	private ItemStack stack;

	public StackBuilder(ItemStack stack) {
		this.stack = stack;

		// Secretly remove all vanilla lore
		// TODO Make this an actual method instead
		ItemMeta meta = stack.getItemMeta();
		meta.addItemFlags(ItemFlag.values());
		stack.setItemMeta(meta);
	}

	public StackBuilder name(String name) {
		ItemMeta meta = stack.getItemMeta();
		meta.setDisplayName(TextUtils.colorize(name));
		stack.setItemMeta(meta);
		return this;
	}

	public StackBuilder appendLore(String... content) {
		ItemMeta meta = stack.getItemMeta();
		List<String> lore = meta.hasLore() ? meta.getLore() : new ArrayList<>();
		lore.addAll(Stream.of(content).map(TextUtils::colorize).toList());
		meta.setLore(lore);
		stack.setItemMeta(meta);
		return this;
	}

	public StackBuilder setLore(String... content) {
		ItemMeta meta = stack.getItemMeta();
		meta.setLore(Stream.of(content).map(TextUtils::colorize).toList());
		stack.setItemMeta(meta);
		return this;
	}

	public ItemStack getStack() { return stack; }
}

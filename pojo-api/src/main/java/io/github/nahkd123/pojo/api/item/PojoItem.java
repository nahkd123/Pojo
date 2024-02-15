package io.github.nahkd123.pojo.api.item;

import static io.github.nahkd123.pojo.api.internal.PojoInternal.keys;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import io.github.nahkd123.pojo.api.internal.PojoInternal;
import io.github.nahkd123.pojo.api.registry.RegistryEntry;
import io.github.nahkd123.pojo.api.registry.UserDefinedId;

public interface PojoItem extends RegistryEntry {
	/**
	 * <p>
	 * Create a new {@link ItemStack}.
	 * </p>
	 * 
	 * @param displayMode Whether the result item should be created for displaying.
	 *                    For example, in display mode, stats component shows the
	 *                    stat ranges of the item.
	 * @return A new {@link ItemStack}.
	 */
	default ItemStack createNew(boolean displayMode) {
		ItemStack stack = new ItemStack(Material.STONE);
		stack.setItemMeta(updateMeta(stack.getItemMeta(), displayMode));
		return stack;
	}

	default ItemMeta updateMeta(ItemMeta source, boolean displayMode) {
		PersistentDataContainer poc = source.getPersistentDataContainer();
		poc.set(keys().id, PersistentDataType.STRING, getId().toString());
		poc.set(keys().displayMode, PersistentDataType.BOOLEAN, displayMode);
		return source;
	}

	default ItemStack updateItem(ItemStack stack) {
		if (stack == null || !stack.hasItemMeta()) return stack;
		stack.setItemMeta(updateMeta(stack.getItemMeta()));
		return stack;
	}

	/**
	 * <p>
	 * Force update the {@link ItemMeta}.
	 * </p>
	 * 
	 * @param source The source {@link ItemMeta}.
	 * @return The new {@link ItemMeta}, usually the same as source but it can be
	 *         different, depending on {@link PojoItem} implementation.
	 */
	default ItemMeta updateMeta(ItemMeta source) {
		PersistentDataContainer poc = source.getPersistentDataContainer();
		boolean isDisp = poc.getOrDefault(keys().displayMode, PersistentDataType.BOOLEAN, false);
		return updateMeta(source, isDisp);
	}

	/**
	 * <p>
	 * Get the item ID from given {@link ItemStack}. This method returns
	 * {@code null} if the {@link ItemStack} does not contains the ID (a.k.a the
	 * item is not Pojo custom item.
	 * </p>
	 * 
	 * @param stack The stack to extract ID.
	 * @return The ID or {@code null}.
	 */
	public static UserDefinedId getId(ItemStack stack) {
		if (stack == null || !stack.hasItemMeta()) return null;
		return getId(stack.getItemMeta());
	}

	/**
	 * <p>
	 * Get the item ID from given {@link ItemMeta}. The ID is stored inside
	 * {@link ItemMeta#getPersistentDataContainer()}.
	 * </p>
	 * 
	 * @param meta The item meta to extract ID.
	 * @return The ID or {@code null}.
	 */
	public static UserDefinedId getId(ItemMeta meta) {
		if (meta == null) return null;
		PersistentDataContainer poc = meta.getPersistentDataContainer();
		return poc.has(PojoInternal.instance().getKeys().id)
			? UserDefinedId.fromString(poc.get(keys().id, PersistentDataType.STRING))
			: null;
	}
}

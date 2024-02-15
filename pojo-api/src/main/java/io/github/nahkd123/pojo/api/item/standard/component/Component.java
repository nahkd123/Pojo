package io.github.nahkd123.pojo.api.item.standard.component;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;

import io.github.nahkd123.pojo.api.utils.lore.LoreSorter;

/**
 * <p>
 * An interface for all kinds of item component. Use {@link DatalessComponent}
 * if your component doesn't need to store anything into the item.
 * </p>
 * 
 * @param <T> The type of component data.
 * @see DatalessComponent
 */
public interface Component<T> {
	public NamespacedKey getTypeId();

	/**
	 * <p>
	 * Create a brand new component data. The created data can be initialized with
	 * random values.
	 * </p>
	 * 
	 * @return Brand new component data.
	 */
	public T createNewData();

	public void storeDataTo(PersistentDataContainer container, T data);

	public T loadDataFrom(PersistentDataContainer container);

	default Material applyMaterial(T data, Material current, boolean displayMode) {
		return current;
	}

	default String applyName(T data, String current, boolean displayMode) {
		return current;
	}

	default void applyLore(T data, LoreSorter lore, boolean displayMode) {}

	default void applyPostDisplay(T data, ItemMeta meta, boolean displayMode) {}
}

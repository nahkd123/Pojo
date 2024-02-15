package io.github.nahkd123.pojo.api.item;

import java.util.Set;

import io.github.nahkd123.pojo.api.internal.PojoInternal;
import io.github.nahkd123.pojo.api.registry.UserDefinedId;

/**
 * <p>
 * A persistent registry for storing {@link PojoItem}. This registry is
 * persistent because it registers the item and store it into server's storage,
 * so the next time you enable the plugin, the item is already there.
 * </p>
 * 
 * @see #getRegistry()
 */
public interface ItemsRegistry {
	/**
	 * <p>
	 * Get a registered {@link PojoItem} from this registry.
	 * </p>
	 * 
	 * @param id ID of the item to get.
	 * @return The item, or {@code null} if there is no item with specified ID.
	 */
	public PojoItem get(UserDefinedId id);

	/**
	 * <p>
	 * Register a new {@link PojoItem} to this registry. This method will also
	 * stores the item definition into the storage (usually server's storage).
	 * </p>
	 * 
	 * @param item The item to register.
	 */
	public void registerNew(PojoItem item);

	/**
	 * <p>
	 * Remove an item with specified ID from this registry and delete the item from
	 * storage.
	 * </p>
	 * 
	 * @param id ID of the item to unregister.
	 * @return {@code true} if the item is unregistered successfully.
	 */
	public boolean unregister(UserDefinedId id);

	/**
	 * <p>
	 * Reload this registry from storage.
	 * </p>
	 */
	public void reloadRegistry();

	public Set<UserDefinedId> getAllIDs();

	/**
	 * <p>
	 * Get the main items registry.
	 * </p>
	 * 
	 * @return The registry.
	 */
	public static ItemsRegistry getRegistry() { return PojoInternal.instance().getItems(); }
}

package io.github.nahkd123.pojo.api.item.standard.component;

import java.util.Collections;
import java.util.Map;

import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.Plugin;

import io.github.nahkd123.pojo.api.internal.PojoInternal;

/**
 * <p>
 * Components factories are used to create a new component from configuration.
 * If you want to add support for in-game editor, consider use
 * {@link EditorComponentsFactory} and {@link EditorSupportedComponent}. Don't
 * forget to register your component by using {@link #register(NamespacedKey)}
 * during {@link Plugin#onLoad()} phase.
 * </p>
 * <p>
 * <b>In-game editor</b>: In-game editor can be added to your component by using
 * {@link EditorComponentsFactory} for your components factory and
 * {@link EditorSupportedComponent} for the component.
 * </p>
 * 
 * @param <T> The type for component data.
 * @see EditorComponentsFactory
 * @see Component
 * @see DatalessComponent
 * @see EditorSupportedComponent
 */
@FunctionalInterface
public interface ComponentsFactory<T> {
	/**
	 * <p>
	 * Create a new item component from configuration.
	 * </p>
	 * 
	 * @param config The component configuration.
	 * @return The component.
	 */
	public Component<T> createFromConfig(ConfigurationSection config);

	default void register(NamespacedKey id) {
		Map<NamespacedKey, ComponentsFactory<?>> map = PojoInternal.instance().getComponents();
		if (map.containsKey(id)) throw new IllegalStateException(id + " is already registered!");
		map.put(id, this);
	}

	/**
	 * <p>
	 * Get all registered components factories. The map returned from this method is
	 * <i>unmodifiable</i>, which means you can't put or remove elements from the
	 * map. Use {@link #register(NamespacedKey)} if you need to register.
	 * </p>
	 * 
	 * @return All registered components factories.
	 */
	public static Map<NamespacedKey, ComponentsFactory<?>> getAllFactories() {
		return Collections.unmodifiableMap(PojoInternal.instance().getComponents());
	}
}

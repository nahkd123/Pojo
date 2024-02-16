package io.github.nahkd123.pojo.api.item.standard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import io.github.nahkd123.pojo.api.internal.PojoInternal;
import io.github.nahkd123.pojo.api.item.PojoItem;
import io.github.nahkd123.pojo.api.item.standard.component.Component;
import io.github.nahkd123.pojo.api.item.standard.component.ComponentsFactory;
import io.github.nahkd123.pojo.api.item.standard.component.SaveableComponent;
import io.github.nahkd123.pojo.api.registry.UserDefinedId;
import io.github.nahkd123.pojo.api.utils.lore.LoreSorter;

public class StandardPojoItem implements PojoItem {
	private List<UserDefinedId> loreSections;
	private UserDefinedId id;
	private List<Component<?>> components;

	public StandardPojoItem(List<UserDefinedId> loreSections, UserDefinedId id, List<Component<?>> components) {
		this.loreSections = loreSections;
		this.id = id;
		this.components = components;
	}

	public static StandardPojoItem loadFromConfig(List<UserDefinedId> loreSections, UserDefinedId id, ConfigurationSection config) {
		ConfigurationSection componentsConfig = config.getConfigurationSection("components");
		List<Component<?>> components = new ArrayList<>();

		if (componentsConfig != null) {
			for (String name : componentsConfig.getKeys(false)) {
				ConfigurationSection component = componentsConfig.getConfigurationSection(name);
				if (!component.contains("type")) {
					PojoInternal.instance().getPlugin().getLogger()
						.warning("Items: " + id + ": Component '" + name + "' does not have 'type' field");
					continue;
				}

				NamespacedKey type = NamespacedKey.fromString(
					component.getString("type"),
					PojoInternal.instance().getPlugin());

				ComponentsFactory<?> factory = ComponentsFactory.getAllFactories().get(type);
				if (factory == null) {
					PojoInternal.instance().getPlugin().getLogger()
						.warning("Items: " + id + ": Component with type '" + type + "' is not registered!");
					continue;
				}

				Component<?> c = factory.createFromConfig(component);
				components.add(c);
			}
		}

		return new StandardPojoItem(loreSections, id, components);
	}

	public boolean saveToConfig(ConfigurationSection config) {
		ConfigurationSection componentsConfig = config.createSection("components");
		int currentIndex = 0;

		for (Component<?> component : components) {
			if (!(component instanceof SaveableComponent<?> saveable)) return false;
			ConfigurationSection componentConfig = componentsConfig.createSection("Autogenerated Name " + currentIndex);
			componentConfig.set("type", component.getTypeId().toString());
			saveable.saveComponentTo(componentConfig);
			currentIndex++;
		}

		return true;
	}

	@Override
	public UserDefinedId getId() { return id; }

	public List<Component<?>> getComponents() { return components; }

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public ItemStack createNew(boolean displayMode) {
		Material mat = Material.STONE;
		String name = null;
		LoreSorter lore = new LoreSorter(loreSections);
		Map<Component<?>, Object> data = new HashMap<>();

		for (Component component : components) {
			Object obj = data.put(component, component.createNewData());
			mat = component.applyMaterial(obj, mat, displayMode);
			name = component.applyName(obj, name, displayMode);
			component.applyLore(obj, lore, displayMode);
		}

		List<String> loreList = lore.build();
		ItemStack stack = new ItemStack(mat);
		ItemMeta meta = stack.getItemMeta();
		PojoItem.super.updateMeta(meta, displayMode);

		if (name != null) meta.setDisplayName(name);
		if (loreList.size() > 0) meta.setLore(loreList);
		for (Component component : components) component.applyPostDisplay(data.get(component), meta, displayMode);

		for (Component component : components) {
			Object obj = data.get(component);
			component.storeDataTo(meta.getPersistentDataContainer(), obj);
		}

		stack.setItemMeta(meta);
		return stack;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public ItemMeta updateMeta(ItemMeta source, boolean displayMode) {
		source = PojoItem.super.updateMeta(source, displayMode);
		String name = null;
		LoreSorter lore = new LoreSorter(loreSections);
		Map<Component<?>, Object> data = new HashMap<>();

		for (Component component : components) {
			Object obj = component.loadDataFrom(source.getPersistentDataContainer());
			data.put(component, obj);
			name = component.applyName(obj, name, displayMode);
			component.applyLore(obj, lore, displayMode);
		}

		List<String> loreList = lore.build();
		if (name != null) source.setLocalizedName(name);
		if (loreList.size() > 0) source.setLore(loreList);
		for (Component component : components) component.applyPostDisplay(data.get(component), source, displayMode);
		return source;
	}
}

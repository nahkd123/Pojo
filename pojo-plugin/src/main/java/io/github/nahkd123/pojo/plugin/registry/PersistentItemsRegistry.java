package io.github.nahkd123.pojo.plugin.registry;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.configuration.file.YamlConfiguration;

import io.github.nahkd123.pojo.api.item.ItemsRegistry;
import io.github.nahkd123.pojo.api.item.PojoItem;
import io.github.nahkd123.pojo.api.item.standard.StandardPojoItem;
import io.github.nahkd123.pojo.api.registry.UserDefinedId;
import io.github.nahkd123.pojo.plugin.PojoPlugin;

public class PersistentItemsRegistry implements ItemsRegistry {
	private PojoPlugin plugin;
	private File itemsFolder;
	private Map<UserDefinedId, PojoItem> items = new HashMap<>();

	// Configurations
	private List<UserDefinedId> loreSections;

	public PersistentItemsRegistry(PojoPlugin plugin, File itemsFolder) {
		this.plugin = plugin;
		this.itemsFolder = itemsFolder;

		YamlConfiguration config = plugin.getOrSaveConfig("items-config.yml");
		loreSections = config.getStringList("loreOrdering").stream().map(UserDefinedId::fromString).toList();
	}

	public File getItemFile(UserDefinedId id) {
		if (id.namespace().equals("pojo")) return new File(itemsFolder, id.id() + ".yml");
		return new File(new File(itemsFolder, id.namespace()), id.id() + ".yml");
	}

	public void loadRegistry() {
		plugin.getLogger().info("Loading user defined items...");
		if (!itemsFolder.exists()) itemsFolder.mkdirs();

		for (File file : itemsFolder.listFiles()) {
			if (file.isDirectory()) {
				String namespace = file.getName();

				for (File child : file.listFiles()) {
					String name = child.getName();
					if (!name.endsWith(".yml")) continue; // TODO allows loading other formats
					UserDefinedId id = new UserDefinedId(namespace, name.substring(0, name.length() - 4));
					loadFromFile(id, child);
				}
			} else if (file.getName().endsWith(".yml")) {
				UserDefinedId id = UserDefinedId.fromString(file.getName().substring(0, file.getName().length() - 4));
				loadFromFile(id, file);
			}
		}

		plugin.getLogger().info("Loaded " + items.size() + " items!");
	}

	public void loadFromFile(UserDefinedId id, File file) {
		StandardPojoItem item = StandardPojoItem.loadFromConfig(
			loreSections, id, YamlConfiguration.loadConfiguration(file));
		items.put(id, item);
	}

	@Override
	public PojoItem get(UserDefinedId id) {
		return items.get(id);
	}

	@Override
	public void registerNew(PojoItem item) {
		items.put(item.getId(), item);
		File file = getItemFile(item.getId());
		YamlConfiguration config = new YamlConfiguration();
		config.options().setHeader(Arrays.asList(
			"This file was automatically generated by Pojo plugin",
			"Modifications can be made in this file if you wanted to."));

		if (item instanceof StandardPojoItem std) {
			if (!std.saveToConfig(config)) throw new RuntimeException(item.getId()
				+ " can't be saved because at least 1 component is not saveable");
		} else {
			throw new RuntimeException(item.getClass().getCanonicalName() + " is not supported!");
		}

		try {
			config.save(file);
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	@Override
	public boolean unregister(UserDefinedId id) {
		PojoItem result = items.remove(id);
		if (result == null) return false;
		getItemFile(id).delete();
		return true;
	}

	@Override
	public void reloadRegistry() {
		plugin.getLogger().info("Reloading items registry...");
		items.clear();
		loadRegistry();
	}

	@Override
	public Set<UserDefinedId> getAllIDs() { return items.keySet(); }
}

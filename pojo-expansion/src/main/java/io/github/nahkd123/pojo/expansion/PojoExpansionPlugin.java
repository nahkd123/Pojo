package io.github.nahkd123.pojo.expansion;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.random.RandomGenerator;

import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import io.github.nahkd123.pojo.expansion.item.standard.StatsComponent;
import io.github.nahkd123.pojo.expansion.stat.StatFactory;
import io.github.nahkd123.pojo.expansion.stat.StatLocalizer;
import io.github.nahkd123.pojo.expansion.stat.StatsLocalizer;
import io.github.nahkd123.pojo.expansion.stat.provided.AttributeStat;

public class PojoExpansionPlugin extends JavaPlugin {
	// Shared
	private RandomGenerator randomGenerator = new Random();
	private StatsLocalizer statsLocalizer;

	// TODO Weird ass registry; replace this later
	private Map<NamespacedKey, StatFactory> stats = new HashMap<>();

	@Override
	public void onLoad() {
		// Stats
		AttributeStat.registerFactory(new NamespacedKey(this, "attribute"));

		// Item components
		StatsComponent.registerFactory(new NamespacedKey(this, "stats"), () -> statsLocalizer,
			randomGenerator::nextLong);
	}

	@Override
	public void onEnable() {
		// Configurations
		// @formatter:off
		YamlConfiguration statsConfig = getOrSaveConfig("stats.yml");
		statsLocalizer = new StatsLocalizer(StatLocalizer.DEFAULT).fromConfig(statsConfig.getConfigurationSection("localization"));
		// @formatter:on
	}

	public YamlConfiguration getOrSaveConfig(String path) {
		File file = new File(getDataFolder(), path);
		if (!file.exists()) saveResource(path, false);
		return YamlConfiguration.loadConfiguration(file);
	}

	public Map<NamespacedKey, StatFactory> getStats() { return stats; }
}

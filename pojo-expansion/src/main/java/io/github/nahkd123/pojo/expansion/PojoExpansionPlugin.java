package io.github.nahkd123.pojo.expansion;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.function.LongSupplier;
import java.util.function.Supplier;
import java.util.random.RandomGenerator;

import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import io.github.nahkd123.pojo.expansion.event.InventoryEventsListener;
import io.github.nahkd123.pojo.expansion.item.standard.GemstoneComponent;
import io.github.nahkd123.pojo.expansion.item.standard.GemstoneSlotsComponent;
import io.github.nahkd123.pojo.expansion.item.standard.StatsComponent;
import io.github.nahkd123.pojo.expansion.stat.StatFactory;
import io.github.nahkd123.pojo.expansion.stat.localize.GemstonesLocalizer;
import io.github.nahkd123.pojo.expansion.stat.localize.StatsLocalizer;
import io.github.nahkd123.pojo.expansion.stat.provided.AttributeStat;

public class PojoExpansionPlugin extends JavaPlugin {
	// Shared
	private RandomGenerator randomGenerator = new Random();
	private StatsLocalizer statsLocalizer;
	private GemstonesLocalizer gemstonesLocalizer;

	// TODO Weird ass registry; replace this later
	private Map<NamespacedKey, StatFactory> stats = new HashMap<>();

	@Override
	public void onLoad() {
		// Stats
		AttributeStat.registerFactory(new NamespacedKey(this, "attribute"));

		// Getters (lazy/late)
		Supplier<StatsLocalizer> statsLocalizer = () -> this.statsLocalizer;
		Supplier<GemstonesLocalizer> gemstonesLocalizer = () -> this.gemstonesLocalizer;
		LongSupplier seedGenerator = randomGenerator::nextLong;

		// Item components
		StatsComponent.registerFactory(new NamespacedKey(this, "stats"), statsLocalizer, seedGenerator);
		GemstoneSlotsComponent.registerFactory(new NamespacedKey(this, "gemstone_slots"), gemstonesLocalizer);
		GemstoneComponent.registerFactory(
			new NamespacedKey(this, "gemstone"),
			gemstonesLocalizer,
			statsLocalizer,
			seedGenerator);
	}

	@Override
	public void onEnable() {
		// Events
		getServer().getPluginManager().registerEvents(new InventoryEventsListener(), this);

		// Configurations
		// @formatter:off
		YamlConfiguration statsConfig = getOrSaveConfig("stats.yml");
		statsLocalizer = new StatsLocalizer().fromConfig(statsConfig.getConfigurationSection("localization"));

		YamlConfiguration gemstonesConfig = getOrSaveConfig("gemstones.yml");
		gemstonesLocalizer = new GemstonesLocalizer().fromConfig(gemstonesConfig.getConfigurationSection("localization"));
		// @formatter:on
	}

	public YamlConfiguration getOrSaveConfig(String path) {
		File file = new File(getDataFolder(), path);
		if (!file.exists()) saveResource(path, false);
		return YamlConfiguration.loadConfiguration(file);
	}

	public Map<NamespacedKey, StatFactory> getStats() { return stats; }
}

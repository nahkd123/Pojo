package io.github.nahkd123.pojo.expansion.stat.localize;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.configuration.ConfigurationSection;

import io.github.nahkd123.pojo.api.registry.UserDefinedId;

public class GemstonesLocalizer {
	private Map<UserDefinedId, GemstoneLocalizer> localizers = new HashMap<>();
	private GemstoneLocalizer defaultLocalizer;
	private List<String> gemstoneLore;

	public GemstonesLocalizer(GemstoneLocalizer defaultLocalizer, List<String> gemstoneLore) {
		this.defaultLocalizer = defaultLocalizer;
		this.gemstoneLore = gemstoneLore;
	}

	public GemstonesLocalizer() {
		this(GemstoneLocalizer.DEFAULT, Collections.emptyList());
	}

	public GemstonesLocalizer fromConfig(ConfigurationSection config) {
		if (config.contains("defaults")) defaultLocalizer = GemstoneLocalizer.fromConfig(
			GemstoneLocalizer.DEFAULT,
			config.getConfigurationSection("defaults"));

		for (String namespace : config.getKeys(false)) {
			if (namespace.equals("defaults")) continue;
			ConfigurationSection namespaceSection = config.getConfigurationSection(namespace);

			for (String id : namespaceSection.getKeys(false)) {
				UserDefinedId udid = new UserDefinedId(namespace, id);
				Object obj = namespaceSection.get(id);
				GemstoneLocalizer localizer;

				if (obj instanceof String str)
					localizer = new GemstoneLocalizer(defaultLocalizer, str, null, null, null, null, null, null, null, null, null);
				else if (obj instanceof ConfigurationSection s)
					localizer = GemstoneLocalizer.fromConfig(defaultLocalizer, s);
				else continue;

				localizers.put(udid, localizer);
			}
		}

		gemstoneLore = config.getStringList("defaults.gemstoneLore");
		return this;
	}

	public List<String> getGemstoneLore() { return gemstoneLore; }

	public GemstoneLocalizer getDefaultLocalizer() { return defaultLocalizer; }

	public void setDefaultLocalizer(GemstoneLocalizer defaultLocalizer) { this.defaultLocalizer = defaultLocalizer; }

	public GemstoneLocalizer getLocalizer(UserDefinedId id) {
		return localizers.getOrDefault(id, defaultLocalizer);
	}
}

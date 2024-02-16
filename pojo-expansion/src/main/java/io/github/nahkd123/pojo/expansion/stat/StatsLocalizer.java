package io.github.nahkd123.pojo.expansion.stat;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.configuration.ConfigurationSection;

import io.github.nahkd123.pojo.api.registry.UserDefinedId;

public class StatsLocalizer {
	private Map<UserDefinedId, Map<String, StatLocalizer>> localizers = new HashMap<>();
	private StatLocalizer defaultLocalizer;

	public StatsLocalizer(StatLocalizer defaultLocalizer) {
		this.defaultLocalizer = defaultLocalizer;
	}

	public StatsLocalizer fromConfig(ConfigurationSection config) {
		if (config.contains("defaults"))
			defaultLocalizer = StatLocalizer.fromConfig(config.getConfigurationSection("defaults"));

		for (String namespace : config.getKeys(false)) {
			if (namespace.equals("defaults")) continue;
			ConfigurationSection namespaceSection = config.getConfigurationSection(namespace);

			for (String id : namespaceSection.getKeys(false)) {
				UserDefinedId udid = new UserDefinedId(namespace, id);
				ConfigurationSection section = namespaceSection.getConfigurationSection(id);

				for (String key : section.getKeys(false)) {
					Object obj = section.get(key);
					StatLocalizer localizer;

					if (obj instanceof String str)
						localizer = new StatLocalizer(defaultLocalizer, str, null, null, null, null, null, null);
					else if (obj instanceof ConfigurationSection s)
						localizer = StatLocalizer.fromConfig(s);
					else continue;

					define(udid, key, localizer);
				}
			}
		}

		return this;
	}

	public void define(UserDefinedId id, String key, StatLocalizer localizer) {
		Map<String, StatLocalizer> map = localizers.get(id);
		if (map == null) localizers.put(id, map = new HashMap<>());
		map.put(key, localizer);
	}

	public StatLocalizer getDefaultLocalizer() { return defaultLocalizer; }

	public void setDefaultLocalizer(StatLocalizer defaultLocalizer) { this.defaultLocalizer = defaultLocalizer; }

	public StatLocalizer getLocalizer(UserDefinedId id, String key) {
		Map<String, StatLocalizer> map = localizers.get(id);
		if (map == null) return defaultLocalizer;
		return map.getOrDefault(key, defaultLocalizer);
	}

	public StatLocalizer getLocalizer(Stat stat) {
		return getLocalizer(new UserDefinedId(stat.getTypeId()), stat.getTranslationKey());
	}
}

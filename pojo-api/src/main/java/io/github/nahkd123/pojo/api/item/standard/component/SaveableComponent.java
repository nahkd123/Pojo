package io.github.nahkd123.pojo.api.item.standard.component;

import org.bukkit.configuration.ConfigurationSection;

public interface SaveableComponent<T> extends Component<T> {
	public void saveComponentTo(ConfigurationSection config);
}

package io.github.nahkd123.pojo.plugin.item.standard.component;

import java.util.EnumMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.meta.ItemMeta;

import io.github.nahkd123.pojo.api.editor.Editable;
import io.github.nahkd123.pojo.api.editor.EditableBool;
import io.github.nahkd123.pojo.api.editor.NodeDescription;
import io.github.nahkd123.pojo.api.item.standard.component.Component;
import io.github.nahkd123.pojo.api.item.standard.component.ComponentsFactory;
import io.github.nahkd123.pojo.api.item.standard.component.DatalessComponent;
import io.github.nahkd123.pojo.api.item.standard.component.EditorComponentsFactory;
import io.github.nahkd123.pojo.api.item.standard.component.EditorSupportedComponent;
import io.github.nahkd123.pojo.api.utils.EnumUtils;

public class ItemFlagsComponent implements DatalessComponent, EditorSupportedComponent<Void> {
	private static final NodeDescription DESCRIPTION = new NodeDescription(Material.ORANGE_BANNER, "Item Flags", new String[] {
		"Configure which part of the vanilla",
		"lore to hide."
	});
	private NamespacedKey typeId;
	private EnumMap<ItemFlag, Boolean> flags;

	public ItemFlagsComponent(NamespacedKey typeId, EnumMap<ItemFlag, Boolean> flags) {
		this.typeId = typeId;
		this.flags = flags;
	}

	@Override
	public NamespacedKey getTypeId() { return typeId; }

	public EnumMap<ItemFlag, Boolean> getFlags() { return flags; }

	public static void registerFactory(NamespacedKey typeId) {
		ComponentsFactory<Void> factory = new EditorComponentsFactory<>() {
			@Override
			public Component<Void> createDefault() {
				return new ItemFlagsComponent(typeId, new EnumMap<>(ItemFlag.class));
			}

			@Override
			public Component<Void> createFromConfig(ConfigurationSection config) {
				EnumMap<ItemFlag, Boolean> flags = new EnumMap<>(ItemFlag.class);

				for (ItemFlag flag : ItemFlag.values()) {
					String configName = EnumUtils.toFriendlyName(flag).replaceAll(" ", "");
					configName = configName.substring(0, 1).toLowerCase() + configName.substring(1);
					flags.put(flag, config.getBoolean(configName, false));
				}

				return new ItemFlagsComponent(typeId, flags);
			}

			@Override
			public NodeDescription getEditorDescription() { return DESCRIPTION; }
		};
		factory.register(typeId);
	}

	@Override
	public void saveComponentTo(ConfigurationSection config) {
		for (ItemFlag flag : ItemFlag.values()) {
			String configName = EnumUtils.toFriendlyName(flag).replaceAll(" ", "");
			configName = configName.substring(0, 1).toLowerCase() + configName.substring(1);
			config.set(configName, flags.getOrDefault(flag, false));
		}
	}

	@Override
	public NodeDescription getEditorDescription() { return DESCRIPTION; }

	@Override
	public List<Editable> getEditorNodes() {
		// @formatter:off
		return Stream.of(ItemFlag.values())
			.map(flag -> (Editable) new EditableBool(
				new NodeDescription(EnumUtils.toFriendlyName(flag)),
				() -> flags.getOrDefault(flag, false),
				s -> flags.put(flag, s)))
			.toList();
		// @formatter:on
	}

	@Override
	public void applyPostDisplay(ItemMeta meta, boolean displayMode) {
		Set<ItemFlag> existing = meta.getItemFlags();

		for (ItemFlag flag : ItemFlag.values()) {
			boolean existed = existing.contains(flag);
			boolean state = flags.getOrDefault(flag, false);
			boolean needUpdate = existed ^ state;

			if (needUpdate) {
				if (state) meta.addItemFlags(flag);
				else meta.removeItemFlags(flag);
			}
		}
	}
}

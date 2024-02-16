package io.github.nahkd123.pojo.plugin.item.standard.component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.meta.ItemMeta;

import io.github.nahkd123.pojo.api.editor.Editable;
import io.github.nahkd123.pojo.api.editor.EditableBool;
import io.github.nahkd123.pojo.api.editor.EditableInteger;
import io.github.nahkd123.pojo.api.editor.EditableList;
import io.github.nahkd123.pojo.api.editor.EditableString;
import io.github.nahkd123.pojo.api.editor.NodeDescription;
import io.github.nahkd123.pojo.api.item.standard.component.Component;
import io.github.nahkd123.pojo.api.item.standard.component.ComponentsFactory;
import io.github.nahkd123.pojo.api.item.standard.component.DatalessComponent;
import io.github.nahkd123.pojo.api.item.standard.component.EditorComponentsFactory;
import io.github.nahkd123.pojo.api.item.standard.component.EditorSupportedComponent;
import io.github.nahkd123.pojo.api.registry.UserDefinedId;
import io.github.nahkd123.pojo.api.utils.TextUtils;
import io.github.nahkd123.pojo.api.utils.lore.LoreSorter;

public class DisplayComponent implements DatalessComponent, EditorSupportedComponent<Void> {
	public static final UserDefinedId CUSTOM_LORE_SECTION = new UserDefinedId("pojo", "custom");
	private static final NodeDescription DESCRIPTION = new NodeDescription(Material.DIAMOND, "Display", new String[] {
		"Configure the display of the item.",
		"This component usually placed before other components, but",
		"you can also use this to enforce material/name at the end of",
		"components chain."
	});

	private NamespacedKey typeId;
	private Material material;
	private String name;
	private List<String> lore;
	private int modelId;
	private boolean hideAllFlags;

	public DisplayComponent(NamespacedKey typeId, Material material, String name, List<String> lore, int modelId, boolean hideAllFlags) {
		this.typeId = typeId;
		this.material = material;
		this.name = name;
		this.lore = lore != null ? lore : new ArrayList<>();
		this.modelId = modelId;
		this.hideAllFlags = hideAllFlags;
	}

	@Override
	public NamespacedKey getTypeId() { return typeId; }

	public Material getMaterial() { return material; }

	public String getName() { return name; }

	public List<String> getLore() { return lore; }

	public static void regisertFactory(NamespacedKey typeId) {
		ComponentsFactory<Void> factory = new EditorComponentsFactory<Void>() {
			@Override
			public Component<Void> createFromConfig(ConfigurationSection config) {
				Material material = config.contains("material") ? Material.valueOf(config.getString("material")) : null;
				String name = config.contains("name") ? config.getString("name") : null;
				List<String> lore = config.getStringList("lore");
				int modelId = config.getInt("modelId", 0);
				boolean hideAllFlags = config.getBoolean("hideAllFlags", false);
				return new DisplayComponent(typeId, material, name, lore, modelId, hideAllFlags);
			}

			@Override
			public Component<Void> createDefault() {
				return new DisplayComponent(typeId, null, null, null, 0, false);
			}

			@Override
			public NodeDescription getEditorDescription() { return DESCRIPTION; }
		};
		factory.register(typeId);
	}

	@Override
	public void saveComponentTo(ConfigurationSection config) {
		if (material != null) config.set("material", material.toString());
		if (name != null) config.set("name", name);
		if (lore.size() > 0) config.set("lore", lore);
	}

	@Override
	public Material applyMaterial(Material current, boolean displayMode) {
		return material != null ? material : current;
	}

	@Override
	public String applyName(String current, boolean displayMode) {
		return name != null ? TextUtils.colorize(name) : current;
	}

	@Override
	public void applyLore(LoreSorter lore, boolean displayMode) {
		if (this.lore.size() > 0)
			lore.getOrCreate(CUSTOM_LORE_SECTION).getLines().addAll(this.lore.stream()
				.map(TextUtils::colorize)
				.toList());
	}

	@Override
	public void applyPostDisplay(ItemMeta meta, boolean displayMode) {
		if (modelId != 0) meta.setCustomModelData(modelId);
		if (hideAllFlags) meta.addItemFlags(ItemFlag.values());
	}

	@Override
	public NodeDescription getEditorDescription() { return DESCRIPTION; }

	@Override
	public List<Editable> getEditorNodes() {
		// @formatter:off
		return Arrays.asList(
			new EditableString(
				new NodeDescription(Material.STONE, "Material", new String[] {
					"The material of this item."
				}),
				() -> material != null ? material.toString() : null,
				s -> material = s != null ? Material.valueOf(s) : null,
				s -> {
					try {
						Material.valueOf(s);
						return true;
					} catch (IllegalArgumentException e) {
						return false;
					}
				}),
			new EditableString(
				new NodeDescription(Material.PAPER, "Name", new String[] {
					"The display name of this item."
				}),
				() -> name, newName -> name = newName),
			EditableList.fromStringList(
				new NodeDescription(Material.BOOK, "Lore", new String[] {
					"The main lore of this item.",
					"This will be set at custom lore section."
				}),
				lore),
			new EditableInteger(
				new NodeDescription(Material.GOLDEN_PICKAXE, "Model ID", new String[] {
					"The numerical model ID for this item.",
					"This value follows vanilla's CustomModelData NBT tag,",
					"where 0 being \"no model\"."
				}),
				() -> modelId, newId -> modelId = Math.max(newId, 0)),
			new EditableBool(
				new NodeDescription(Material.PAPER, "Hide vanilla lore", new String[] {
					"Hide all vanilla lore, such as",
					"'When in main hand'."
				}),
				() -> hideAllFlags, newState -> hideAllFlags = newState));
		// @formatter:on
	}
}

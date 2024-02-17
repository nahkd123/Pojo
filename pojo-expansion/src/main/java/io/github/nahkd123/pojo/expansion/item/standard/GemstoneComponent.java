package io.github.nahkd123.pojo.expansion.item.standard;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.LongSupplier;
import java.util.function.Supplier;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import io.github.nahkd123.pojo.api.editor.Editable;
import io.github.nahkd123.pojo.api.editor.EditableObject;
import io.github.nahkd123.pojo.api.editor.EditableString;
import io.github.nahkd123.pojo.api.editor.NodeDescription;
import io.github.nahkd123.pojo.api.item.standard.component.ComponentsFactory;
import io.github.nahkd123.pojo.api.item.standard.component.EditorComponentsFactory;
import io.github.nahkd123.pojo.api.item.standard.component.EditorSupportedComponent;
import io.github.nahkd123.pojo.api.registry.UserDefinedId;
import io.github.nahkd123.pojo.api.utils.TextUtils;
import io.github.nahkd123.pojo.api.utils.lore.LoreSection;
import io.github.nahkd123.pojo.api.utils.lore.LoreSorter;
import io.github.nahkd123.pojo.expansion.stat.Stat;
import io.github.nahkd123.pojo.expansion.stat.compute.ComputedStats;
import io.github.nahkd123.pojo.expansion.stat.localize.GemstoneLocalizer;
import io.github.nahkd123.pojo.expansion.stat.localize.GemstonesLocalizer;
import io.github.nahkd123.pojo.expansion.stat.localize.StatLocalizer;
import io.github.nahkd123.pojo.expansion.stat.localize.StatsLocalizer;
import io.github.nahkd123.pojo.expansion.utils.PlaceholderUtils;
import io.github.nahkd123.pojo.expansion.utils.PojoEquipmentSlot;

public class GemstoneComponent implements EditorSupportedComponent<ComputedStats> {
	private static final NodeDescription DESCRIPTION = new NodeDescription(Material.EMERALD, "Pojo Expansion: Gemstone", "Turn this item into gemstone.");

	private NamespacedKey typeId;
	private Supplier<GemstonesLocalizer> gemstonesLocalizer;
	private Supplier<StatsLocalizer> statsLocalizer;
	private LongSupplier seedGenerator;
	private UserDefinedId slotId;
	private PojoEquipmentSlot equipmentSlot;
	private List<Stat> modifiers;

	public GemstoneComponent(NamespacedKey typeId, Supplier<GemstonesLocalizer> gemstonesLocalizer, Supplier<StatsLocalizer> statsLocalizer, LongSupplier seedGenerator, UserDefinedId slotId, PojoEquipmentSlot equipmentSlot, List<Stat> modifiers) {
		this.typeId = typeId;
		this.gemstonesLocalizer = gemstonesLocalizer;
		this.statsLocalizer = statsLocalizer;
		this.seedGenerator = seedGenerator;
		this.slotId = slotId;
		this.equipmentSlot = equipmentSlot;
		this.modifiers = modifiers;
	}

	public UserDefinedId getSlotId() { return slotId; }

	public void setSlotId(UserDefinedId slotId) { this.slotId = slotId; }

	public PojoEquipmentSlot getEquipmentSlot() { return equipmentSlot; }

	public void setEquipmentSlot(PojoEquipmentSlot equipmentSlot) { this.equipmentSlot = equipmentSlot; }

	public List<Stat> getModifiers() { return modifiers; }

	public static void registerFactory(NamespacedKey typeId, Supplier<GemstonesLocalizer> gemstonesLocalizer, Supplier<StatsLocalizer> statsLocalizer, LongSupplier seedGenerator) {
		ComponentsFactory<ComputedStats> factory = new EditorComponentsFactory<>() {
			@Override
			public GemstoneComponent createDefault() {
				return new GemstoneComponent(typeId, gemstonesLocalizer, statsLocalizer, seedGenerator, new UserDefinedId("mynamespace", "sample_slot"), PojoEquipmentSlot.ALL, new ArrayList<>());
			}

			@Override
			public GemstoneComponent createFromConfig(ConfigurationSection config) {
				UserDefinedId slotId = config.contains("gemstoneSlot")
					? UserDefinedId.fromString(config.getString("gemstoneSlot"))
					: new UserDefinedId("mynamespace", "sample_slot");
				PojoEquipmentSlot equipmentSlot = config.contains("equipmentSlot")
					? PojoEquipmentSlot.valueOf(config.getString("equipmentSlot"))
					: PojoEquipmentSlot.ALL;
				List<Stat> modifiers = config.contains("modifiers")
					? Stat.loadStatsListFrom(config.getConfigurationSection("modifiers"))
					: new ArrayList<>();
				return new GemstoneComponent(typeId, gemstonesLocalizer, statsLocalizer, seedGenerator, slotId, equipmentSlot, modifiers);
			}

			@Override
			public NodeDescription getEditorDescription() { return DESCRIPTION; }
		};
		factory.register(typeId);
	}

	@Override
	public void saveComponentTo(ConfigurationSection config) {
		if (slotId != null) config.set("gemstoneSlot", slotId.toString());
		if (equipmentSlot != null) config.set("equipmentSlot", equipmentSlot.toString());

		ConfigurationSection allModifiersConfig = config.createSection("modifiers");
		Stat.saveStatsListTo(allModifiersConfig, modifiers);
	}

	@Override
	public NamespacedKey getTypeId() { return typeId; }

	@Override
	public ComputedStats createNewData() {
		return new ComputedStats(seedGenerator.getAsLong(), equipmentSlot, modifiers);
	}

	@Override
	public void storeDataTo(PersistentDataContainer container, ComputedStats data) {
		container.set(typeId, PersistentDataType.LONG, data.getSeed());
	}

	@Override
	public ComputedStats loadDataFrom(PersistentDataContainer container) {
		return new ComputedStats(container.get(typeId, PersistentDataType.LONG), equipmentSlot, modifiers);
	}

	@Override
	public NodeDescription getEditorDescription() { return DESCRIPTION; }

	@Override
	public List<Editable> getEditorNodes() {
		// @formatter:off
		return Arrays.asList(
			new EditableString(
				new NodeDescription(
					Material.EMERALD,
					"Slot ID",
					"The gemstone slot ID that this gemstone",
					"can be placed."
				),
				() -> slotId.toString(),
				s -> slotId = s != null? UserDefinedId.fromString(s) : slotId
			),
			EditableObject.enumToBooleans(
				new NodeDescription(
					Material.GOLDEN_CHESTPLATE,
					"Equipment Slot",
					"Specify an equipment slot or leave it to",
					"'All' to apply to all stats components."
				),
				Arrays.asList(PojoEquipmentSlot.values()),
				() -> equipmentSlot,
				newSlot -> equipmentSlot = newSlot,
				slot -> slot.getDescription()
			),
			Stat.createStatsEditableList(
				new NodeDescription(Material.GOLDEN_SWORD, "Modifiers", "Configure a list of stat modifiers"),
				modifiers
			)
		);
		// @formatter:on
	}

	@Override
	public void applyLore(ComputedStats data, LoreSorter lore, boolean displayMode) {
		LoreSection selfSection = lore.getOrCreate(new UserDefinedId(typeId));
		GemstonesLocalizer gemstoneLocalizer = this.gemstonesLocalizer.get();
		GemstoneLocalizer slotLocalizer = gemstoneLocalizer.getLocalizer(slotId);
		String slotName = slotLocalizer.getName() != null ? slotLocalizer.getName() : slotId.toString();

		selfSection.getLines().addAll(gemstoneLocalizer.getGemstoneLore().stream()
			.map(template -> PlaceholderUtils.apply(s -> switch (s) {
			case "name" -> slotName;
			default -> null;
			}, TextUtils.colorize(template)))
			.toList());

		LoreSection statsSection = lore.getOrCreate(new UserDefinedId("pojoexpansion", "stats"));

		if (displayMode) {
			for (Stat modifier : modifiers) {
				if (modifier == null || !modifier.canBeDisplayed()) return;
				StatLocalizer localizer = this.statsLocalizer.get().getLocalizer(modifier);
				String statName = localizer.getName() != null ? localizer.getName() : modifier.getTranslationKey();
				String lineContent = localizer.localizeBase(statName, modifier.getOperation(), modifier.getStatValue());
				statsSection.getLines().add(lineContent);
			}
		} else {
			for (Stat modifiers : modifiers) {
				if (modifiers == null || !modifiers.canBeDisplayed()) return;
				double value = data.get(modifiers).getValue();
				StatLocalizer localizer = this.statsLocalizer.get().getLocalizer(modifiers);
				String statName = localizer.getName() != null ? localizer.getName() : modifiers.getTranslationKey();
				statsSection.getLines().add(localizer.localizeBase(statName, modifiers.getOperation(), value));
			}
		}
	}
}

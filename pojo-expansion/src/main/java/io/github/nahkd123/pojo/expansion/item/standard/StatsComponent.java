package io.github.nahkd123.pojo.expansion.item.standard;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.LongSupplier;
import java.util.function.Supplier;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import io.github.nahkd123.pojo.api.editor.Editable;
import io.github.nahkd123.pojo.api.editor.EditableObject;
import io.github.nahkd123.pojo.api.editor.NodeDescription;
import io.github.nahkd123.pojo.api.item.PojoItem;
import io.github.nahkd123.pojo.api.item.standard.StandardPojoItem;
import io.github.nahkd123.pojo.api.item.standard.component.ComponentDataHolder;
import io.github.nahkd123.pojo.api.item.standard.component.ComponentsFactory;
import io.github.nahkd123.pojo.api.item.standard.component.EditorComponentsFactory;
import io.github.nahkd123.pojo.api.item.standard.component.EditorSupportedComponent;
import io.github.nahkd123.pojo.api.registry.UserDefinedId;
import io.github.nahkd123.pojo.api.utils.lore.LoreSection;
import io.github.nahkd123.pojo.api.utils.lore.LoreSorter;
import io.github.nahkd123.pojo.expansion.stat.Stat;
import io.github.nahkd123.pojo.expansion.stat.compute.ComputedStats;
import io.github.nahkd123.pojo.expansion.stat.localize.StatLocalizer;
import io.github.nahkd123.pojo.expansion.stat.localize.StatsLocalizer;
import io.github.nahkd123.pojo.expansion.utils.PojoEquipmentSlot;

public class StatsComponent implements EditorSupportedComponent<ComputedStats> {
	private static final NodeDescription DESCRIPTION = new NodeDescription(Material.DIAMOND_SWORD, "Pojo Expansion: Stats", new String[] {
		"Add stats to this item."
	});

	private NamespacedKey typeId;
	private Supplier<StatsLocalizer> localizer;
	private LongSupplier seedGenerator;
	private PojoEquipmentSlot slot;
	private List<Stat> stats;

	public StatsComponent(NamespacedKey typeId, Supplier<StatsLocalizer> localizer, LongSupplier seedGenerator, PojoEquipmentSlot slot, List<Stat> stats) {
		this.typeId = typeId;
		this.localizer = localizer;
		this.seedGenerator = seedGenerator;
		this.slot = slot;
		this.stats = stats;
	}

	public PojoEquipmentSlot getEquipmentSlot() { return slot; }

	public void setEquipmentSlot(PojoEquipmentSlot slot) { this.slot = slot; }

	public List<Stat> getStats() { return stats; }

	public static void registerFactory(NamespacedKey typeId, Supplier<StatsLocalizer> localizer, LongSupplier seedGenerator) {
		ComponentsFactory<ComputedStats> factory = new EditorComponentsFactory<>() {
			@Override
			public StatsComponent createDefault() {
				return new StatsComponent(typeId, localizer, seedGenerator, PojoEquipmentSlot.ALL, new ArrayList<>());
			}

			@Override
			public StatsComponent createFromConfig(ConfigurationSection config) {
				StatsComponent component = createDefault();
				if (config.contains("equipmentSlot"))
					component.slot = PojoEquipmentSlot.valueOf(config.getString("equipmentSlot"));
				if (config.contains("stats"))
					component.stats = Stat.loadStatsListFrom(config.getConfigurationSection("stats"));
				return component;
			}

			@Override
			public NodeDescription getEditorDescription() { return DESCRIPTION; }
		};
		factory.register(typeId);
	}

	@Override
	public void saveComponentTo(ConfigurationSection config) {
		if (slot != null) config.set("equipmentSlot", slot.toString());

		ConfigurationSection allStatsConfig = config.createSection("stats");
		Stat.saveStatsListTo(allStatsConfig, stats);
	}

	@Override
	public NodeDescription getEditorDescription() { return DESCRIPTION; }

	@Override
	public List<Editable> getEditorNodes() {
		// @formatter:off
		return Arrays.asList(
			EditableObject.enumToBooleans(
				new NodeDescription(
					Material.GOLDEN_CHESTPLATE,
					"Equipment Slot",
					"Specify an equipment slot or leave",
					"it to 'All' to apply to all slots."
				),
				Arrays.asList(PojoEquipmentSlot.values()),
				() -> slot,
				newSlot -> slot = newSlot,
				slot -> slot.getDescription()
			),
			Stat.createStatsEditableList(
				new NodeDescription(
					Material.DIAMOND_SWORD,
					"Stat slots",
					"Add and remove stat slots."
				),
				stats
			)
		);
		// @formatter:on
	}

	@Override
	public NamespacedKey getTypeId() { return typeId; }

	@Override
	public ComputedStats createNewData() {
		return new ComputedStats(seedGenerator.getAsLong(), slot, stats);
	}

	@Override
	public void storeDataTo(PersistentDataContainer container, ComputedStats data) {
		container.set(typeId, PersistentDataType.LONG, data.getSeed());
	}

	@Override
	public ComputedStats loadDataFrom(PersistentDataContainer container) {
		long seed = container.getOrDefault(typeId, PersistentDataType.LONG, 0L);
		return new ComputedStats(seed, slot, stats);
	}

	@Override
	public void applyLore(ComputedStats data, LoreSorter lore, boolean displayMode) {
		LoreSection section = lore.getOrCreate(new UserDefinedId(typeId));

		if (displayMode) {
			for (Stat stat : stats) {
				if (stat == null || !stat.canBeDisplayed()) return;
				StatLocalizer localizer = this.localizer.get().getLocalizer(stat);
				String name = localizer.getName() != null ? localizer.getName() : stat.getTranslationKey();
				String lineContent = localizer.localizeBase(name, stat.getOperation(), stat.getStatValue());
				section.getLines().add(lineContent);
			}
		} else {
			for (Stat stat : data.getStats()) {
				if (stat == null || !stat.canBeDisplayed()) return;
				StatLocalizer localizer = this.localizer.get().getLocalizer(stat);
				section.getLines().add(localizer.localize(data.get(stat)));
			}
		}
	}

	@Override
	public void applyPostDisplay(ComputedStats data, ItemMeta meta, boolean displayMode) {
		if (displayMode) return;

		// Clear all modifiers so that AttributeStat can add new modifiers
		meta.setAttributeModifiers(null);
		for (Stat stat : stats) stat.applyToItemMeta(meta, data.get(stat).getValue(), slot);
	}

	/**
	 * <p>
	 * Compute item stats from given {@link ItemMeta}. This also takes gemstones and
	 * other modifiers into account (modifiers are components that modifies the
	 * computed stats).
	 * </p>
	 * 
	 * @param meta The meta.
	 * @return An unmodifiable collection of computed stats lists, each associated
	 *         with {@link StatsComponent}.
	 */
	public static List<ComputedStats> computeItemStats(ItemMeta meta) {
		PojoItem item = PojoItem.getFrom(meta);
		if (!(item instanceof StandardPojoItem std)) return Collections.emptyList();

		ComponentDataHolder dataHolder = std.loadDataFrom(meta, true);
		return Collections.unmodifiableList(dataHolder.getList(StatsComponent.class));
	}

	/**
	 * <p>
	 * Compute item stats from {@link ItemStack}. See
	 * {@link #computeItemStats(ItemMeta)} for more information.
	 * </p>
	 * 
	 * @param stack The item.
	 * @return An unmodifiable collection of computed stats.
	 */
	public static List<ComputedStats> computeItemStats(ItemStack stack) {
		return stack != null && stack.hasItemMeta() ? computeItemStats(stack.getItemMeta()) : null;
	}
}

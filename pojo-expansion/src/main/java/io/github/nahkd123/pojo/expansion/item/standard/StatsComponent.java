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
import org.bukkit.plugin.java.JavaPlugin;

import io.github.nahkd123.pojo.api.editor.Editable;
import io.github.nahkd123.pojo.api.editor.EditableList;
import io.github.nahkd123.pojo.api.editor.EditableObject;
import io.github.nahkd123.pojo.api.editor.NodeDescription;
import io.github.nahkd123.pojo.api.item.PojoItem;
import io.github.nahkd123.pojo.api.item.standard.StandardPojoItem;
import io.github.nahkd123.pojo.api.item.standard.component.Component;
import io.github.nahkd123.pojo.api.item.standard.component.ComponentDataHolder;
import io.github.nahkd123.pojo.api.item.standard.component.ComponentsFactory;
import io.github.nahkd123.pojo.api.item.standard.component.EditorComponentsFactory;
import io.github.nahkd123.pojo.api.item.standard.component.EditorSupportedComponent;
import io.github.nahkd123.pojo.api.registry.UserDefinedId;
import io.github.nahkd123.pojo.api.utils.EnumUtils;
import io.github.nahkd123.pojo.api.utils.lore.LoreSection;
import io.github.nahkd123.pojo.api.utils.lore.LoreSorter;
import io.github.nahkd123.pojo.expansion.PojoExpansionPlugin;
import io.github.nahkd123.pojo.expansion.stat.Stat;
import io.github.nahkd123.pojo.expansion.stat.StatFactory;
import io.github.nahkd123.pojo.expansion.stat.StatLocalizer;
import io.github.nahkd123.pojo.expansion.stat.StatOperation;
import io.github.nahkd123.pojo.expansion.stat.StatsLocalizer;
import io.github.nahkd123.pojo.expansion.stat.compute.ComputedStats;
import io.github.nahkd123.pojo.expansion.stat.value.StatConstantValue;
import io.github.nahkd123.pojo.expansion.stat.value.StatValue;
import io.github.nahkd123.pojo.expansion.utils.PojoEquipmentSlot;

public class StatsComponent implements Component<ComputedStats>, EditorSupportedComponent<ComputedStats> {
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

				if (config.contains("stats")) {
					ConfigurationSection allStatsConfig = config.getConfigurationSection("stats");

					for (String name : allStatsConfig.getKeys(false)) {
						ConfigurationSection statConfig = allStatsConfig.getConfigurationSection(name);

						if (!statConfig.contains("type")) {
							component.stats.add(null);
							continue;
						}

						NamespacedKey type = NamespacedKey.fromString(statConfig.getString("type"),
							JavaPlugin.getPlugin(PojoExpansionPlugin.class));
						StatFactory statFactory = StatFactory.getAllFactories().get(type);

						if (statFactory == null) {
							// TODO warn
							component.stats.add(null);
							continue;
						}

						StatOperation operation = statConfig.contains("operation")
							? StatOperation.valueOf(statConfig.getString("operation"))
							: StatOperation.ADD_VALUE;
						StatValue value = StatValue.fromConfig(statConfig, "value");
						Stat stat = statFactory.createFromConfig(operation, value, statConfig);
						component.stats.add(stat);
					}
				}

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
		int counter = 0;

		for (Stat stat : stats) {
			ConfigurationSection statConfig = allStatsConfig.createSection("Autogenerated Name " + counter);
			counter++;
			if (stat == null) continue;

			statConfig.set("type", stat.getTypeId().toString());
			statConfig.set("operation", stat.getOperation().toString());
			StatValue.toConfig(statConfig, "value", stat.getStatValue());
			stat.saveToConfig(statConfig);
		}
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
				slot -> slot.getDescription()),
			new EditableList(
				new NodeDescription(
					Material.DIAMOND_SWORD,
					"Stat slots",
					"Add and remove stat slots."
				),
				() -> stats.size(),
				index -> new EditableObject(
					new NodeDescription(Material.GOLDEN_SWORD, "Stat slot #" + (index + 1), "Edit the stat in this slot"),
					() -> {
						List<Editable> list = new ArrayList<>();
						List<NamespacedKey> types = new ArrayList<>();
						types.add(null);
						types.addAll(StatFactory.getAllFactories().keySet());

						list.add(EditableObject.enumToBooleans(
							new NodeDescription(Material.PAPER, "Type", "The stat type for this slot"),
							types,
							() -> stats.get(index) != null? stats.get(index).getTypeId() : null,
							newType -> {
								Stat oldStat = stats.get(index);
								if (newType == null) {
									stats.set(index, null);
									return;
								}

								if (oldStat != null && oldStat.getTypeId().equals(newType)) return;
								StatFactory factory = StatFactory.getAllFactories().get(newType);
								if (factory == null) return;
								Stat newStat = factory.createDefault(
									oldStat != null ? oldStat.getOperation() : StatOperation.ADD_VALUE,
									oldStat != null ? oldStat.getStatValue() : new StatConstantValue(0d));
								stats.set(index, newStat);
							},
							type -> type != null
								? new NodeDescription(type.toString())
								: new NodeDescription("None", "Default type.")));
						if (stats.get(index) == null) return list;

						Stat current = stats.get(index);
						list.add(EditableObject.enumToBooleans(
							new NodeDescription(Material.REDSTONE, "Operation", "Change how this slot modify the stat"),
							Arrays.asList(StatOperation.values()),
							current::getOperation,
							current::setOperation,
							op -> new NodeDescription(EnumUtils.toFriendlyName(op), switch (op) {
							case ADD_VALUE -> new String[] { "Add value to player's stat" };
							case ADD_PERCENTAGE -> new String[] { "Add value to player's stat multiplier" };
							case MULTIPLIER -> new String[] { "Multply player's stat by a value" };
							default -> new String[0];
							})));
						list.addAll(Stat.createValueTypeEditables(current::getStatValue, current::setStatValue));
						list.addAll(current.getEditableParameters());
						return list;
					}
				)
				.setCustomPreviewLines(() -> Collections.singletonList(stats.get(index) != null ? stats.get(index).getEditorText() : "&7&o(Empty slot)")),
				index -> stats.add(index, null),
				index -> stats.remove(index),
				(from, to) -> {
					Stat stat = stats.remove((int) from);
					stats.add(to, stat);
				}));
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
				String lineContent = localizer.localize(name, stat.getOperation(), stat.getStatValue());
				section.getLines().add(lineContent);
			}
		} else {
			for (Stat stat : stats) {
				if (stat == null || !stat.canBeDisplayed()) return;
				double value = data.get(stat).getValue();
				StatLocalizer localizer = this.localizer.get().getLocalizer(stat);
				String name = localizer.getName() != null ? localizer.getName() : stat.getTranslationKey();
				section.getLines().add(localizer.localize(name, stat.getOperation(), value));
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

		ComponentDataHolder dataHolder = std.loadDataFrom(meta);
		return Collections.unmodifiableList(dataHolder.get(StatsComponent.class));
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

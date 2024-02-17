package io.github.nahkd123.pojo.expansion.stat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import io.github.nahkd123.pojo.api.editor.Editable;
import io.github.nahkd123.pojo.api.editor.EditableDouble;
import io.github.nahkd123.pojo.api.editor.EditableList;
import io.github.nahkd123.pojo.api.editor.EditableObject;
import io.github.nahkd123.pojo.api.editor.EditableString;
import io.github.nahkd123.pojo.api.editor.NodeDescription;
import io.github.nahkd123.pojo.api.utils.EnumUtils;
import io.github.nahkd123.pojo.expansion.PojoExpansionPlugin;
import io.github.nahkd123.pojo.expansion.stat.provided.AttributeStat;
import io.github.nahkd123.pojo.expansion.stat.value.StatConstantValue;
import io.github.nahkd123.pojo.expansion.stat.value.StatFormulaValue;
import io.github.nahkd123.pojo.expansion.stat.value.StatRangeValue;
import io.github.nahkd123.pojo.expansion.stat.value.StatValue;
import io.github.nahkd123.pojo.expansion.utils.PojoEquipmentSlot;

public interface Stat {
	public NamespacedKey getTypeId();

	public StatValue getStatValue();

	public void setStatValue(StatValue value);

	public StatOperation getOperation();

	public void setOperation(StatOperation operation);

	public void saveToConfig(ConfigurationSection config);

	/**
	 * <p>
	 * Some stats, like attributes, have editable parameters like attribute ID.
	 * </p>
	 * 
	 * @return List of editables.
	 */
	public List<Editable> getEditableParameters();

	public boolean canBeDisplayed();

	public String getTranslationKey();

	/**
	 * <p>
	 * Matching key is a special object that will be used for comparing 2
	 * {@link Stat} to see if they are referring to the same stat type.
	 * </p>
	 * <p>
	 * For example, 2 {@link AttributeStat} with the same {@link Attribute} will
	 * have the same matching key.
	 * </p>
	 * <p>
	 * This matching key is usually used by gemstones to modify the final item
	 * stats.
	 * </p>
	 * 
	 * @return The stat matching key.
	 */
	public Object getMatchingKey();

	default String getEditorText() { return toString(); }

	default void applyToItemMeta(ItemMeta meta, double value, PojoEquipmentSlot slot) {}

	public static enum EditableStatType {
		CONSTANT(new NodeDescription("Constant value", "The value is always the same")) {
			@Override
			public StatValue getDefault() { return new StatConstantValue(0d); }
		},
		RANGE(new NodeDescription("Range", "The value varies in a range", "Exponent curve is supported")) {
			@Override
			public StatValue getDefault() {
				return new StatRangeValue(new StatConstantValue(0d), new StatConstantValue(0d), 1);
			}
		},
		FORMULA(new NodeDescription("Formula", "Enter your own stat formula")) {
			@Override
			public StatValue getDefault() { return new StatFormulaValue("0f"); }
		};

		private NodeDescription description;

		private EditableStatType(NodeDescription description) {
			this.description = description;
		}

		public NodeDescription getDescription() { return description; }

		public abstract StatValue getDefault();

		public static EditableStatType getTypeFrom(StatValue value) {
			if (value instanceof StatConstantValue) return CONSTANT;
			if (value instanceof StatRangeValue) return RANGE;
			if (value instanceof StatFormulaValue) return FORMULA;
			return CONSTANT;
		}
	}

	public static EditableObject createValueTypeSwitcher(Supplier<StatValue> getter, Consumer<StatValue> setter) {
		return EditableObject.enumToBooleans(
			new NodeDescription(Material.PAPER, "Value type", "The type of value. Can be a constant", "a formula or a range"),
			Arrays.asList(EditableStatType.values()),
			() -> EditableStatType.getTypeFrom(getter.get()),
			newType -> {
				EditableStatType oldType = EditableStatType.getTypeFrom(getter.get());
				if (newType == oldType) return;
				setter.accept(newType.getDefault());
			},
			type -> type.getDescription());
	}

	public static List<Editable> createValueTypeEditables(Supplier<StatValue> getter, Consumer<StatValue> setter) {
		List<Editable> list = new ArrayList<>();
		list.add(createValueTypeSwitcher(getter, setter));

		StatValue current = getter.get();

		// @formatter:off
		if (current instanceof StatConstantValue)
			list.add(new EditableDouble(
				new NodeDescription(Material.DIAMOND, "Value"),
				() -> ((StatConstantValue) getter.get()).value(),
				v -> setter.accept(new StatConstantValue(v))
			));
		if (current instanceof StatRangeValue range) {
			if (!(range.min() instanceof StatConstantValue) || !(range.max() instanceof StatConstantValue)) return list;

			list.add(new EditableDouble(
				new NodeDescription(Material.GOLD_NUGGET, "Minimum"),
				() -> ((StatRangeValue) getter.get()).getMin(),
				v -> {
					StatRangeValue oldRange = (StatRangeValue) getter.get();
					setter.accept(new StatRangeValue(new StatConstantValue(v), oldRange.max(), 1d));
				}));
			list.add(new EditableDouble(
				new NodeDescription(Material.GOLD_BLOCK, "Maximum"),
				() -> ((StatRangeValue) getter.get()).getMax(),
				v -> {
					StatRangeValue oldRange = (StatRangeValue) getter.get();
					setter.accept(new StatRangeValue(oldRange.min(), new StatConstantValue(v), 1d));
				}));
			list.add(new EditableDouble(
				new NodeDescription(Material.GOLD_INGOT, "Exponent"),
				() -> ((StatRangeValue) getter.get()).exponent(),
				v -> {
					StatRangeValue oldRange = (StatRangeValue) getter.get();
					setter.accept(new StatRangeValue(oldRange.min(), oldRange.max(), v));
				}));
		}
		if (current instanceof StatFormulaValue)
			list.add(new EditableString(
				new NodeDescription(Material.REDSTONE, "Formula"),
				() -> ((StatFormulaValue) getter.get()).getFormula(),
				v -> setter.accept(new StatFormulaValue(v))
			));
		// @formatter:on
		return list;
	}

	public static void saveStatsListTo(ConfigurationSection allStatsConfig, Iterable<Stat> stats) {
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

	public static List<Stat> loadStatsListFrom(ConfigurationSection allStatsConfig) {
		List<Stat> stats = new ArrayList<>();

		for (String name : allStatsConfig.getKeys(false)) {
			ConfigurationSection statConfig = allStatsConfig.getConfigurationSection(name);

			if (!statConfig.contains("type")) {
				stats.add(null);
				continue;
			}

			NamespacedKey type = NamespacedKey.fromString(statConfig.getString("type"),
				JavaPlugin.getPlugin(PojoExpansionPlugin.class));
			StatFactory statFactory = StatFactory.getAllFactories().get(type);

			if (statFactory == null) {
				// TODO warn
				stats.add(null);
				continue;
			}

			StatOperation operation = statConfig.contains("operation")
				? StatOperation.valueOf(statConfig.getString("operation"))
				: StatOperation.ADD_VALUE;
			StatValue value = StatValue.fromConfig(statConfig, "value");
			Stat stat = statFactory.createFromConfig(operation, value, statConfig);
			stats.add(stat);
		}

		return stats;
	}

	public static EditableList createStatsEditableList(NodeDescription description, List<Stat> stats) {
		// @formatter:off
		return new EditableList(
			description,
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
			}
		);
		// @formatter:on
	}
}

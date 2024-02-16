package io.github.nahkd123.pojo.expansion.stat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.meta.ItemMeta;

import io.github.nahkd123.pojo.api.editor.Editable;
import io.github.nahkd123.pojo.api.editor.EditableDouble;
import io.github.nahkd123.pojo.api.editor.EditableObject;
import io.github.nahkd123.pojo.api.editor.EditableString;
import io.github.nahkd123.pojo.api.editor.NodeDescription;
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
}

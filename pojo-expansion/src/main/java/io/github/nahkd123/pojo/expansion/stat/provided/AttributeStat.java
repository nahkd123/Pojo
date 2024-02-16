package io.github.nahkd123.pojo.expansion.stat.provided;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.attribute.AttributeModifier.Operation;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.meta.ItemMeta;

import io.github.nahkd123.pojo.api.editor.Editable;
import io.github.nahkd123.pojo.api.editor.EditableObject;
import io.github.nahkd123.pojo.api.editor.NodeDescription;
import io.github.nahkd123.pojo.api.utils.EnumUtils;
import io.github.nahkd123.pojo.expansion.stat.Stat;
import io.github.nahkd123.pojo.expansion.stat.StatFactory;
import io.github.nahkd123.pojo.expansion.stat.StatOperation;
import io.github.nahkd123.pojo.expansion.stat.value.StatValue;
import io.github.nahkd123.pojo.expansion.utils.PojoEquipmentSlot;

public class AttributeStat implements Stat {
	private NamespacedKey typeId;
	private Attribute attribute;
	private StatOperation operation;
	private StatValue value;

	public AttributeStat(NamespacedKey typeId, Attribute attribute, StatOperation operation, StatValue value) {
		this.typeId = typeId;
		this.attribute = attribute;
		this.operation = operation;
		this.value = value;
	}

	public Attribute getAttribute() { return attribute; }

	@Override
	public NamespacedKey getTypeId() { return typeId; }

	@Override
	public StatValue getStatValue() { return value; }

	@Override
	public void setStatValue(StatValue value) { this.value = value; }

	@Override
	public StatOperation getOperation() { return operation; }

	@Override
	public void setOperation(StatOperation operation) { this.operation = operation; }

	@Override
	public Object getMatchingKey() { return attribute; }

	public static void registerFactory(NamespacedKey typeId) {
		StatFactory factory = new StatFactory() {
			@Override
			public Stat createFromConfig(StatOperation operation, StatValue value, ConfigurationSection config) {
				Attribute attr = config.contains("attribute") ? Attribute.valueOf(config.getString("attribute")) : null;
				return new AttributeStat(typeId, attr, operation, value);
			}

			@Override
			public Stat createDefault(StatOperation initialOperation, StatValue initialValue) {
				return new AttributeStat(typeId, null, initialOperation, initialValue);
			}
		};
		factory.register(typeId);
	}

	@Override
	public void saveToConfig(ConfigurationSection config) {
		config.set("attribute", attribute != null ? attribute.toString() : null);
	}

	@Override
	public List<Editable> getEditableParameters() {
		return Arrays.asList(
			EditableObject.enumToBooleans(
				new NodeDescription(Material.DIAMOND_SWORD, "Attribute Type", "Select a vanilla attribute"),
				Stream.concat(Stream.of(new Attribute[] { null }), Stream.of(Attribute.values())).toList(),
				() -> attribute,
				value -> attribute = value,
				value -> value != null
					? new NodeDescription(EnumUtils.toFriendlyName(value))
					: new NodeDescription("None", "Default attribute.")));
	}

	@Override
	public boolean canBeDisplayed() {
		return attribute != null;
	}

	@Override
	public void applyToItemMeta(ItemMeta meta, double value, PojoEquipmentSlot slot) {
		if (attribute == null) return;

		UUID uuid = UUID.randomUUID();
		meta.addAttributeModifier(attribute, new AttributeModifier(uuid, uuid.toString(), switch (operation) {
		case ADD_VALUE -> value;
		case ADD_PERCENTAGE -> value;
		case MULTIPLIER -> value - 1;
		default -> value;
		}, switch (operation) {
		case ADD_VALUE -> Operation.ADD_NUMBER;
		case ADD_PERCENTAGE -> Operation.ADD_SCALAR;
		case MULTIPLIER -> Operation.MULTIPLY_SCALAR_1;
		default -> Operation.ADD_NUMBER;
		}, slot.getBukkit()));
	}

	@Override
	public String getTranslationKey() {
		if (attribute == null) return "empty";
		String s = EnumUtils.toFriendlyName(attribute).replaceAll(" ", "");
		s = s.substring(0, 1).toLowerCase() + s.substring(1);
		return s;
	}

	@Override
	public String getEditorText() {
		return "&7Attribute/&f"
			+ (attribute != null ? EnumUtils.toFriendlyName(attribute) : "&oEmpty")
			+ "&7: " + EnumUtils.toFriendlyName(operation) + ": &f" + value.getDisplayText();
	}

	@Override
	public String toString() {
		return "attribute: " + attribute + ", " + operation + ", " + value;
	}
}

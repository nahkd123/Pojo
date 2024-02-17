package io.github.nahkd123.pojo.expansion.stat.localize;

import java.util.Random;

import org.bukkit.configuration.ConfigurationSection;

import io.github.nahkd123.pojo.api.utils.TextUtils;
import io.github.nahkd123.pojo.expansion.stat.StatOperation;
import io.github.nahkd123.pojo.expansion.stat.compute.ComputedStat;
import io.github.nahkd123.pojo.expansion.stat.value.StatConstantValue;
import io.github.nahkd123.pojo.expansion.stat.value.StatValue;
import io.github.nahkd123.pojo.expansion.stat.value.StatValueWithMaxMin;
import io.github.nahkd123.pojo.expansion.utils.PlaceholderUtils;

public class StatLocalizer {
	// @formatter:off
	public static final StatLocalizer DEFAULT = new StatLocalizer(
		null, null,
		"{base}{modifiers}",
		"&7{name}: &a+{value}",
		"&7{name}: &a+{value;percentage}%",
		"&7{name}: &ex{value}",
		"&7{name}: &a+{min} &7-> &a{max}",
		"&7{name}: &a+{min;percentage} &7-> &a{max;percentage}",
		"&7{name}: &ex{min} &7-> &e{max}");
	// @formatter:on

	private String name;
	private String format;
	private String addValue;
	private String addPercentage;
	private String multiplier;
	private String addValueRange;
	private String addPercentageRange;
	private String multiplierRange;

	public StatLocalizer(StatLocalizer parent, String name, String format, String addValue, String addPercentage, String multiplier, String addValueRange, String addPercentageRange, String multiplierRange) {
		this.name = name;
		this.format = format != null ? format : parent.format;
		this.addValue = addValue != null ? addValue : parent.addValue;
		this.addPercentage = addPercentage != null ? addPercentage : parent.addPercentage;
		this.multiplier = multiplier != null ? multiplier : parent.multiplier;
		this.addValueRange = addValueRange != null ? addValueRange : parent.addValueRange;
		this.addPercentageRange = addPercentageRange != null ? addPercentageRange : parent.addPercentageRange;
		this.multiplierRange = multiplierRange != null ? multiplierRange : parent.multiplierRange;
	}

	public static StatLocalizer fromConfig(StatLocalizer parent, ConfigurationSection config) {
		String name = config.getString("name");
		String format = config.getString("format");
		String addValue = config.getString("addValue");
		String addPercentage = config.getString("addPercentage");
		String multiplier = config.getString("multiplier");
		String addValueRange = config.getString("addValueRange");
		String addPercentageRange = config.getString("addPercentageRange");
		String multiplierRange = config.getString("multiplierRange");
		return new StatLocalizer(parent, name, format, addValue, addPercentage, multiplier, addValueRange, addPercentageRange, multiplierRange);
	}

	public String getName() { return name; }

	public String getFormat() { return format; }

	public String localizeBase(String name, StatOperation operation, double value) {
		String s = switch (operation) {
		case ADD_VALUE -> addValue;
		case ADD_PERCENTAGE -> addPercentage;
		case MULTIPLIER -> multiplier;
		};

		s = PlaceholderUtils.apply(varName -> switch (varName) {
		case "name" -> name;
		case "value", "max", "min" -> Double.toString(value);
		default -> null;
		}, s);
		return TextUtils.colorize(s);
	}

	public String localizeBase(String name, StatOperation operation, double min, double max) {
		String s = switch (operation) {
		case ADD_VALUE -> addValueRange;
		case ADD_PERCENTAGE -> addPercentageRange;
		case MULTIPLIER -> multiplierRange;
		};

		s = PlaceholderUtils.apply(varName -> switch (varName) {
		case "name" -> name;
		case "value" -> Double.toString((min + max) / 2);
		case "min" -> Double.toString(min);
		case "max" -> Double.toString(max);
		default -> null;
		}, s);
		return TextUtils.colorize(s);
	}

	public String localizeBase(String name, StatOperation operation, StatValue value) {
		if (value instanceof StatValueWithMaxMin mm) return localizeBase(name, operation, mm.getMin(), mm.getMax());
		if (value instanceof StatConstantValue cons) return localizeBase(name, operation, cons.value());
		return localizeBase(name, operation, value.get(new Random()));
	}

	public String localize(ComputedStat computed) {
		String base = localizeBase(
			name != null ? name : computed.getStat().getTranslationKey(),
			computed.getStat().getOperation(),
			computed.getValue());

		return PlaceholderUtils.apply(s -> switch (s) {
		case "base" -> base;
		case "modifiers" -> computed.getModifiers();
		default -> null;
		}, TextUtils.colorize(format));
	}
}

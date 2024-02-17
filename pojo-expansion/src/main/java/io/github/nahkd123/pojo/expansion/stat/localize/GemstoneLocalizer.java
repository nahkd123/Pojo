package io.github.nahkd123.pojo.expansion.stat.localize;

import org.bukkit.configuration.ConfigurationSection;

import io.github.nahkd123.pojo.api.utils.TextUtils;
import io.github.nahkd123.pojo.expansion.utils.PlaceholderUtils;

public class GemstoneLocalizer extends StatLocalizer {
	// @formatter:off
	public static final GemstoneLocalizer DEFAULT = new GemstoneLocalizer(
		null, null,
		" &9{base}",
		"+{value;number}",
		"+(value;percentage}",
		"x{value;number}",
		"+{min;number} -> {max;number}",
		"+(min;percentage} -> {max;percentage}",
		"x{min;number} -> {max;number}",
		"&7[&o{name}&7]",
		"&7[&f{gemstoneName}&7]");
	// @formatter:on

	private String emptySlot;
	private String filledSlot;

	public GemstoneLocalizer(GemstoneLocalizer parent, String name, String format, String addValue, String addPercentage, String multiplier, String addValueRange, String addPercentageRange, String multiplierRange, String emptySlot, String filledSlot) {
		super(parent, name, format, addValue, addPercentage, multiplier, addValueRange, addPercentageRange, multiplierRange);
		this.emptySlot = emptySlot != null ? emptySlot : parent.emptySlot;
		this.filledSlot = filledSlot != null ? filledSlot : parent.filledSlot;
	}

	public static GemstoneLocalizer fromConfig(GemstoneLocalizer parent, ConfigurationSection config) {
		String name = config.getString("name");
		String format = config.getString("format");
		String addValue = config.getString("addValue");
		String addPercentage = config.getString("addPercentage");
		String multiplier = config.getString("multiplier");
		String addValueRange = config.getString("addValueRange");
		String addPercentageRange = config.getString("addPercentageRange");
		String multiplierRange = config.getString("multiplierRange");
		String emptySlot = config.getString("emptySlot");
		String filledSlot = config.getString("filledSlot");
		return new GemstoneLocalizer(parent, name, format, addValue, addPercentage, multiplier, addValueRange, addPercentageRange, multiplierRange, emptySlot, filledSlot);
	}

	public String localizeEmptySlot(String slotName) {
		return TextUtils.colorize(PlaceholderUtils.apply(s -> switch (s) {
		case "name" -> slotName;
		default -> null;
		}, emptySlot));
	}

	public String localizeFilledSlot(String slotName, String gemstoneName) {
		return TextUtils.colorize(PlaceholderUtils.apply(s -> switch (s) {
		case "name" -> slotName;
		case "gemstoneName" -> gemstoneName;
		default -> null;
		}, filledSlot));
	}
}

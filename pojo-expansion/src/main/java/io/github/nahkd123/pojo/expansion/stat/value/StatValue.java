package io.github.nahkd123.pojo.expansion.stat.value;

import java.util.Arrays;
import java.util.List;
import java.util.random.RandomGenerator;

import org.bukkit.configuration.ConfigurationSection;

import xyz.mangostudio.mangoscript.runtime.execution.Evaluator;

/**
 * <p>
 * Stat value generates a random number based on given parameters, such as
 * number range, exponent parameter or generating from custom formula (which is
 * powered by MangoScript's {@link Evaluator}).
 * </p>
 */
public interface StatValue {
	/**
	 * <p>
	 * Generate next value procedurally. Pojo Expansion does not stores the
	 * generated stat values in item's NBT; it instead stores the seed so that all
	 * values can be populated procedurally.
	 * </p>
	 * 
	 * @param random Random number generator.
	 * @return The generated value.
	 */
	public double get(RandomGenerator random);

	default String getDisplayText() { return toString(); }

	public static StatValue fromConfig(ConfigurationSection root, String key) {
		if (!root.contains(key)) return new StatConstantValue(0d);

		Object obj = root.get(key);
		if (obj instanceof Number num) return new StatConstantValue(num.doubleValue());
		if (obj instanceof String str) return new StatFormulaValue(str);
		if (obj instanceof List<?> lst)
		// @formatter:off
			return new StatRangeValue(
				new StatConstantValue(((Number) lst.get(0)).doubleValue()),
				new StatConstantValue(((Number) lst.get(1)).doubleValue()),
				1d
			);
		// @formatter:on

		if (obj instanceof ConfigurationSection section) {
			StatValue min = fromConfig(section, "min");
			StatValue max = fromConfig(section, "max");
			double exp = section.getDouble("exponent", 1d);
			return new StatRangeValue(min, max, exp);
		}

		return new StatConstantValue(0d);
	}

	public static void toConfig(ConfigurationSection root, String key, StatValue val) {
		if (val instanceof StatConstantValue cons) {
			root.set(key, cons.value());
			return;
		}

		if (val instanceof StatFormulaValue formula) {
			root.set(key, formula.getFormula());
			return;
		}

		if (val instanceof StatRangeValue range) {
			StatValue min = range.min();
			StatValue max = range.max();
			double exp = range.exponent();

			if (min instanceof StatConstantValue minConst && max instanceof StatConstantValue maxConst && exp == 1d) {
				root.set("value", Arrays.asList(minConst.value(), maxConst.value()));
				return;
			}

			ConfigurationSection section = root.createSection(key);
			toConfig(section, "min", min);
			toConfig(section, "max", max);
			section.set("exponent", exp);
			return;
		}

		root.set("value", 0d);
		return;
	}
}

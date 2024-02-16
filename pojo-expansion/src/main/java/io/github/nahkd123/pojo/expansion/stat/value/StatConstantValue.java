package io.github.nahkd123.pojo.expansion.stat.value;

import java.text.DecimalFormat;
import java.util.random.RandomGenerator;

public record StatConstantValue(double value) implements StatValue {
	private static final DecimalFormat FORMATTER = new DecimalFormat("#,##0.##");

	@Override
	public double get(RandomGenerator random) {
		return value;
	}

	@Override
	public String getDisplayText() { return "&a" + FORMATTER.format(value); }
}

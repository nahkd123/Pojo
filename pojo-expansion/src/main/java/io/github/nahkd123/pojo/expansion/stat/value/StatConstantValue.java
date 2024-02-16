package io.github.nahkd123.pojo.expansion.stat.value;

import java.util.random.RandomGenerator;

public record StatConstantValue(double value) implements StatValue {
	@Override
	public double get(RandomGenerator random) {
		return value;
	}
}

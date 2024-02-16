package io.github.nahkd123.pojo.expansion.stat.value;

import java.util.Random;
import java.util.random.RandomGenerator;

public record StatRangeValue(StatValue min, StatValue max, double exponent) implements StatValueWithMaxMin {
	@Override
	public double get(RandomGenerator random) {
		double min = this.min.get(random);
		double max = this.max.get(random);
		double range = max - min;
		return min + Math.pow(random.nextDouble(0, 1), exponent) * range;
	}

	@Override
	public double getMax() {
		if (max instanceof StatValueWithMaxMin range) return range.getMax();
		if (max instanceof StatConstantValue cons) return cons.value();
		return max.get(new Random());
	}

	@Override
	public double getMin() {
		if (min instanceof StatValueWithMaxMin range) return range.getMin();
		if (min instanceof StatConstantValue cons) return cons.value();
		return min.get(new Random());
	}
}

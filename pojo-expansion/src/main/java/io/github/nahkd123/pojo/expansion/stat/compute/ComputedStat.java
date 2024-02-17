package io.github.nahkd123.pojo.expansion.stat.compute;

import io.github.nahkd123.pojo.expansion.stat.Stat;
import io.github.nahkd123.pojo.expansion.stat.StatOperation;

public class ComputedStat {
	private Stat stat;
	private double value;
	private double percentage = 1d;
	private String modifiers = "";
	// TODO modifiers chain: "Stat Name: +X% (+5)"

	public ComputedStat(Stat stat, double value) {
		this.stat = stat;
		this.value = value;
	}

	public double getPercentage() { return percentage; }

	public double getValue() { return value * Math.max(percentage, 0d); }

	public String getModifiers() { return modifiers; }

	public void modify(StatOperation operation, double modifier, String modifierText) {
		if (stat.getOperation() == StatOperation.ADD_VALUE) modifyWithAddValueSource(operation, modifier);
		if (stat.getOperation() == StatOperation.ADD_PERCENTAGE) modifyWithAddPercentageSource(operation, modifier);
		if (stat.getOperation() == StatOperation.MULTIPLIER) modifyWithMultiplierSource(operation, modifier);
		modifiers += modifierText;
	}

	private void modifyWithAddValueSource(StatOperation operation, double modifier) {
		switch (operation) {
		case ADD_VALUE:
			value += modifier;
			break;
		case ADD_PERCENTAGE:
			percentage += modifier;
			break;
		case MULTIPLIER:
			value *= modifier;
			break;
		default:
			break;
		}
	}

	private void modifyWithAddPercentageSource(StatOperation operation, double modifier) {
		switch (operation) {
		case ADD_PERCENTAGE:
			value += modifier;
			break;
		case MULTIPLIER:
			value *= modifier;
			break;
		default:
			break;
		}
	}

	private void modifyWithMultiplierSource(StatOperation operation, double modifier) {
		switch (operation) {
		case MULTIPLIER:
			value *= modifier;
			break;
		default:
			break;
		}
	}

	public Stat getStat() { return stat; }
}

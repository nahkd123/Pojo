package io.github.nahkd123.pojo.expansion.stat.compute;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import io.github.nahkd123.pojo.expansion.stat.Stat;
import io.github.nahkd123.pojo.expansion.stat.StatOperation;
import io.github.nahkd123.pojo.expansion.utils.PojoEquipmentSlot;

public class ComputedStats {
	private Long seed;
	private PojoEquipmentSlot equipmentSlot;
	private List<Stat> stats = new ArrayList<>();
	private Random random;
	private Map<Object, ComputedStat> map = new HashMap<>();
	private Set<Object> matchingKeys = new HashSet<>();

	public ComputedStats(Long seed, PojoEquipmentSlot equipmentSlot, List<Stat> stats) {
		this.seed = seed;
		this.equipmentSlot = equipmentSlot;
		this.random = new Random(seed != null ? seed : 0L);

		// Compute in sequential order
		for (Stat stat : stats) {
			matchingKeys.add(stat.getMatchingKey());
			this.stats.add(stat);
			map.put(stat.getMatchingKey(), new ComputedStat(stat, stat.getStatValue().get(random)));
		}
	}

	public Long getSeed() { return seed; }

	public PojoEquipmentSlot getEquipmentSlot() { return equipmentSlot; }

	public List<Stat> getStats() { return stats; }

	public ComputedStat get(Stat stat) {
		ComputedStat computed = map.get(stat.getMatchingKey());

		if (computed == null) {
			// If the matching key haven't seen before, add a new stat
			if (!matchingKeys.contains(stat.getMatchingKey())) {
				matchingKeys.add(stat.getMatchingKey());
				stats.add(stat);
			}

			// The 0d value is used instead of generating new value
			// This is because such stat does not exists yet
			map.put(stat.getMatchingKey(),
				computed = new ComputedStat(stat, stat.getOperation() == StatOperation.MULTIPLIER ? 1d : 0d));
		}

		return computed;
	}

	public ComputedStat modify(Stat modifier, String modifierText) {
		ComputedStat computed = get(modifier);
		computed.modify(modifier.getOperation(), modifier.getStatValue().get(random), modifierText);
		return computed;
	}
}

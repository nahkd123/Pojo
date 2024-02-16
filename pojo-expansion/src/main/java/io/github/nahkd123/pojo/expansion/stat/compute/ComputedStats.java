package io.github.nahkd123.pojo.expansion.stat.compute;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import io.github.nahkd123.pojo.expansion.stat.Stat;
import io.github.nahkd123.pojo.expansion.utils.PojoEquipmentSlot;

public class ComputedStats {
	private Long seed;
	private PojoEquipmentSlot equipmentSlot;
	private List<Stat> stats;
	private Random random;
	private Map<Object, ComputedStat> map = new HashMap<>();
	private Set<Object> matchingKeys = new HashSet<>();

	public ComputedStats(Long seed, PojoEquipmentSlot equipmentSlot, List<Stat> stats) {
		this.seed = seed;
		this.equipmentSlot = equipmentSlot;
		this.stats = stats;
		stats.stream().map(s -> s.getMatchingKey()).forEach(matchingKeys::add);
		this.random = new Random(seed != null ? seed : 0L);

		// Compute in sequential order
		for (Stat stat : stats) get(stat);
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

			map.put(stat.getMatchingKey(), computed = new ComputedStat(stat, stat.getStatValue().get(random)));
		}

		return computed;
	}

	public ComputedStat modify(Stat modifier) {
		ComputedStat computed = get(modifier);
		computed.modify(modifier.getOperation(), modifier.getStatValue().get(random));
		return computed;
	}
}

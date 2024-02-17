package io.github.nahkd123.pojo.expansion.stat.gemstone;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataContainer;

import io.github.nahkd123.pojo.api.registry.UserDefinedId;

public class GemstonesHolder {
	private List<UserDefinedId> layout;
	private List<Gemstone> gemstones;

	public GemstonesHolder(List<UserDefinedId> layout) {
		this.layout = layout;
		this.gemstones = new ArrayList<>();
		for (int i = 0; i < layout.size(); i++) gemstones.add(null);
	}

	public List<UserDefinedId> getLayout() { return layout; }

	public List<Gemstone> getGemstones() { return gemstones; }

	public boolean tryAttachGemstone(UserDefinedId slotId, Gemstone gemstone) {
		for (int i = 0; i < layout.size(); i++) {
			UserDefinedId destSlotId = layout.get(i);
			if (!destSlotId.equals(slotId)) continue;
			if (gemstones.get(i) != null) continue;

			gemstones.set(i, gemstone);
			return true;
		}

		return false;
	}

	public GemstonesHolder load(List<PersistentDataContainer> slots) {
		if (slots == null) return this;

		for (int i = 0; i < layout.size(); i++) {
			if (i >= slots.size()) return this;

			PersistentDataContainer slot = slots.get(i);
			Gemstone gemstone = Gemstone.TYPE.fromPrimitive(slot, slot.getAdapterContext());
			gemstones.set(i, gemstone);
		}

		return this;
	}

	public List<PersistentDataContainer> toContainers(PersistentDataAdapterContext context) {
		List<PersistentDataContainer> slots = new ArrayList<>();

		for (int i = 0; i < layout.size(); i++) {
			if (i >= gemstones.size()) {
				slots.add(context.newPersistentDataContainer());
				continue;
			}

			Gemstone gemstone = gemstones.get(i);

			if (gemstone == null) {
				slots.add(context.newPersistentDataContainer());
				continue;
			}

			slots.add(Gemstone.TYPE.toPrimitive(gemstone, context));
		}

		return slots;
	}
}

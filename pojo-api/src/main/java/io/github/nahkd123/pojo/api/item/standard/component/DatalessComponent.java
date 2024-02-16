package io.github.nahkd123.pojo.api.item.standard.component;

import org.bukkit.Material;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;

import io.github.nahkd123.pojo.api.utils.lore.LoreSorter;

public interface DatalessComponent extends Component<Void> {
	@Override
	default Void createNewData() {
		return null;
	}

	@Override
	default Void loadDataFrom(PersistentDataContainer container) {
		return null;
	}

	@Override
	default void storeDataTo(PersistentDataContainer container, Void data) {}

	@Override
	default Material applyMaterial(Void data, Material current, boolean displayMode) {
		return applyMaterial(current, displayMode);
	}

	default Material applyMaterial(Material current, boolean displayMode) {
		return current;
	}

	@Override
	default String applyName(Void data, String current, boolean displayMode) {
		return applyName(current, displayMode);
	}

	default String applyName(String current, boolean displayMode) {
		return current;
	}

	@Override
	default void applyLore(Void data, LoreSorter lore, boolean displayMode) {
		applyLore(lore, displayMode);
	}

	default void applyLore(LoreSorter lore, boolean displayMode) {}

	@Override
	default void applyPostDisplay(Void data, ItemMeta meta, boolean displayMode) {
		applyPostDisplay(meta, displayMode);
	}

	default void applyPostDisplay(ItemMeta meta, boolean displayMode) {}
}

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

	public Material applyMaterial(Material current, boolean displayMode);

	@Override
	default String applyName(Void data, String current, boolean displayMode) {
		return applyName(current, displayMode);
	}

	public String applyName(String current, boolean displayMode);

	@Override
	default void applyLore(Void data, LoreSorter lore, boolean displayMode) {
		applyLore(lore, displayMode);
	}

	public void applyLore(LoreSorter lore, boolean displayMode);

	@Override
	default void applyPostDisplay(Void data, ItemMeta meta, boolean displayMode) {
		applyPostDisplay(meta, displayMode);
	}

	public void applyPostDisplay(ItemMeta meta, boolean displayMode);
}

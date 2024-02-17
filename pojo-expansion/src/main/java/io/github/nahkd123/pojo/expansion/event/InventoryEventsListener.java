package io.github.nahkd123.pojo.expansion.event;

import java.util.Optional;

import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import io.github.nahkd123.pojo.api.item.PojoItem;
import io.github.nahkd123.pojo.api.item.standard.StandardPojoItem;
import io.github.nahkd123.pojo.api.item.standard.component.ComponentDataHolder;
import io.github.nahkd123.pojo.api.utils.TextUtils;
import io.github.nahkd123.pojo.expansion.item.standard.GemstoneComponent;
import io.github.nahkd123.pojo.expansion.item.standard.GemstoneSlotsComponent;
import io.github.nahkd123.pojo.expansion.stat.gemstone.Gemstone;
import io.github.nahkd123.pojo.expansion.stat.gemstone.GemstonesHolder;

public class InventoryEventsListener implements Listener {
	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		if (event.isCancelled()) return;
		if (event.getInventory().getType() != InventoryType.CRAFTING) return;
		if (checkGemstoneApply(event)) return;
	}

	private boolean checkGemstoneApply(InventoryClickEvent event) {
		ItemStack cursorStack = event.getCursor();
		if (!(PojoItem.getFrom(cursorStack) instanceof StandardPojoItem cursorStd)) return false;

		ItemStack clickedStack = event.getCurrentItem();
		if (!(PojoItem.getFrom(clickedStack) instanceof StandardPojoItem clickedStd)) return false;

		Optional<GemstoneComponent> gemstoneComponent = cursorStd.getComponents().stream()
			.filter(s -> s instanceof GemstoneComponent)
			.findAny().map(s -> (GemstoneComponent) s);
		if (gemstoneComponent.isEmpty()) return false;

		Optional<GemstoneSlotsComponent> gemstoneSlotComponent = clickedStd.getComponents().stream()
			.filter(s -> s instanceof GemstoneSlotsComponent)
			.findAny().map(s -> (GemstoneSlotsComponent) s);
		if (gemstoneSlotComponent.isEmpty()) return false;

		// Test
		ComponentDataHolder clickedDataHolder = clickedStd.loadDataFrom(clickedStack.getItemMeta(), true);
		GemstonesHolder slots = clickedDataHolder.get(gemstoneSlotComponent.get());

		ComponentDataHolder cursorDataHolder = cursorStd.loadDataFrom(cursorStack.getItemMeta(), true);
		Gemstone gemstone = new Gemstone(PojoItem.getId(cursorStack), cursorDataHolder);

		if (event.getWhoClicked().getGameMode() == GameMode.CREATIVE) {
			event.getWhoClicked().sendMessage(TextUtils
				.colorize("&6&lNOTE: &eGemstones can only be applied in any gamemode that isn't Creative mode! "
					+ "I still couldn't figure out a way to prevent unwanted creative mode item duplication..."));
			return false;
		}

		if (!slots.tryAttachGemstone(gemstoneComponent.get().getSlotId(), gemstone)) return false;

		// Apply
		clickedStack = clickedStack.clone();
		ItemMeta meta = clickedStack.getItemMeta();
		clickedStd.saveDataTo(meta.getPersistentDataContainer(), clickedDataHolder);
		clickedStd.updateMeta(meta);
		clickedStack.setItemMeta(meta);
		event.setCancelled(true);
		event.setCurrentItem(clickedStack);
		event.getWhoClicked().setItemOnCursor(null);
		if (event.getWhoClicked() instanceof Player p)
			p.playSound(p.getEyeLocation(), Sound.BLOCK_SMITHING_TABLE_USE, 1f, 1f);
		return true;
	}
}

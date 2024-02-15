package io.github.nahkd123.pojo.plugin.event;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.scheduler.BukkitRunnable;

import io.github.nahkd123.pojo.plugin.PojoPlugin;
import io.github.nahkd123.pojo.plugin.editor.ChatInput;
import io.github.nahkd123.pojo.plugin.editor.EditorSession;

public class PlayerChatEventsListener implements Listener {
	private PojoPlugin plugin;

	public PlayerChatEventsListener(PojoPlugin plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onChatAsync(AsyncPlayerChatEvent event) {
		EditorSession editor = plugin.getEditor(event.getPlayer().getUniqueId());
		ChatInput input = editor.getHeldInput();

		if (input != null) {
			final String message = event.getMessage();
			event.setCancelled(true);

			new BukkitRunnable() {
				@Override
				public void run() {
					input.onInput(editor, message);
				}
			}.runTask(plugin);
		}
	}
}

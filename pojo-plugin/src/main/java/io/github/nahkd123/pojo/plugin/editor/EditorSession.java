package io.github.nahkd123.pojo.plugin.editor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import io.github.nahkd123.pojo.plugin.PojoPlugin;

/**
 * <p>
 * Editing Pojo stuffs, like custom items or custom blocks (we'll call these
 * "targets"), can be done using in-game editor. Editor session links the player
 * and the current editing target.
 * </p>
 */
public class EditorSession {
	private PojoPlugin plugin;
	private UUID owner;
	private List<EditorTarget> targets = new ArrayList<>();
	private int tabPage = 0;
	private ChatInput heldInput;

	public EditorSession(PojoPlugin plugin, UUID owner) {
		this.plugin = plugin;
		this.owner = owner;
	}

	public PojoPlugin getPlugin() { return plugin; }

	public UUID getOwnerUUID() { return owner; }

	public Player getOwnerOrNull() { return Bukkit.getPlayer(owner); }

	public List<EditorTarget> getTargets() { return targets; }

	public void replaceTarget(EditorTarget target, EditorTarget with) {
		int idx = targets.indexOf(target);
		if (idx == -1) targets.add(with);
		else targets.set(idx, with);
	}

	public int getTabPage() { return tabPage; }

	public void setTabPage(int tabPage) { this.tabPage = tabPage; }

	public ChatInput getHeldInput() { return heldInput; }

	public void setHeldInput(ChatInput heldInput) { this.heldInput = heldInput; }
}

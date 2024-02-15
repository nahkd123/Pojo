package io.github.nahkd123.pojo.plugin.command.provided;

import java.io.File;

import org.bukkit.entity.Player;

import io.github.nahkd123.pojo.api.item.PojoItem;
import io.github.nahkd123.pojo.api.registry.UserDefinedId;
import io.github.nahkd123.pojo.plugin.PojoPlugin;
import io.github.nahkd123.pojo.plugin.command.CommandException;
import io.github.nahkd123.pojo.plugin.command.CommandExecutorWrapper;
import io.github.nahkd123.pojo.plugin.command.SimplePluginCommand;
import io.github.nahkd123.pojo.plugin.command.argument.PlayerArgumentType;
import io.github.nahkd123.pojo.plugin.command.argument.RegistryArgumentType;
import io.github.nahkd123.pojo.plugin.command.argument.UserDefinedIdArgumentType;
import io.github.nahkd123.pojo.plugin.editor.EditorGUI;
import io.github.nahkd123.pojo.plugin.editor.EditorSession;
import io.github.nahkd123.pojo.plugin.editor.EditorTarget;
import io.github.nahkd123.pojo.plugin.editor.browse.EditorBrowseItemsTarget;
import io.github.nahkd123.pojo.plugin.editor.item.EditorEditItemTarget;

public class PojoAdminCommand extends CommandExecutorWrapper {
	public PojoAdminCommand(PojoPlugin plugin) {
		super(new SimplePluginCommand()
			.withChildren("items", items(plugin))
			.withChildren("editor", editor(plugin)));
	}

	private static SimplePluginCommand items(PojoPlugin plugin) {
		return new SimplePluginCommand()
			.withChildren("reload", new SimplePluginCommand()
				.withCallback(ctx -> {
					plugin.getItems().reloadRegistry();
					ctx.feedback("&eReloaded all items");
				}))
			.withChildren("new", new SimplePluginCommand()
				.withArgument("id", UserDefinedIdArgumentType.TYPE)
				.withCallback(ctx -> {
					UserDefinedId id = ctx.getArgument("id", UserDefinedIdArgumentType.TYPE);
					File file = plugin.getItems().getItemFile(id);
					plugin.copyResourceTo(file, "sample-item-standard.yml");
					plugin.getItems().loadFromFile(id, file);
					ctx.feedback("&aCreated " + id + ", stored in " + file + "!");
					ctx.feedback("&7&o(Use &7/pojo items edit " + id + " &oto edit the item)");
				}))
			.withChildren("give", new SimplePluginCommand()
				.withArgument("player", PlayerArgumentType.TYPE)
				.withArgument("id", new RegistryArgumentType(plugin.getItems()::getAllIDs))
				.withCallback(ctx -> {
					Player player = ctx.getArgument("player", PlayerArgumentType.TYPE);
					UserDefinedId id = ctx.getArgument("id", UserDefinedIdArgumentType.TYPE);
					PojoItem item = plugin.getItems().get(id);
					if (item == null) throw new CommandException("Item with ID " + id + " does not exists");
					player.getInventory().addItem(item.createNew(false));
					ctx.feedback("&aGave " + player.getName() + " 1x " + id + "!");
				}))
			.withChildren("edit", new SimplePluginCommand()
				.withArgument("id", new RegistryArgumentType(plugin.getItems()::getAllIDs))
				.withCallback(ctx -> {
					Player player = ctx.getPlayerOrThrow();
					UserDefinedId id = ctx.getArgument("id", UserDefinedIdArgumentType.TYPE);
					PojoItem item = plugin.getItems().get(id);
					if (item == null) throw new CommandException("Item with ID " + id + " does not exists");

					EditorSession session = plugin.getEditor(player.getUniqueId());
					EditorTarget target = new EditorEditItemTarget(plugin.getItems(), item);
					session.getTargets().add(target);

					EditorGUI gui = target.createGUI(session);
					player.openInventory(gui.getInventory());
				}));
	}

	private static SimplePluginCommand editor(PojoPlugin plugin) {
		return new SimplePluginCommand()
			.withCallback(ctx -> {
				Player player = ctx.getPlayerOrThrow();
				EditorSession session = plugin.getEditor(player.getUniqueId());
				EditorTarget target;

				if (session.getTargets().size() == 0) {
					target = new EditorBrowseItemsTarget(plugin.getItems(), 0);
					session.getTargets().add(target);
				} else {
					target = session.getTargets().get(session.getTargets().size() - 1);
				}

				EditorGUI gui = target.createGUI(session);
				player.openInventory(gui.getInventory());
			});
	}
}

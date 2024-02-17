package io.github.nahkd123.pojo.plugin.command.provided;

import java.io.File;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import io.github.nahkd123.pojo.api.item.PojoItem;
import io.github.nahkd123.pojo.api.registry.UserDefinedId;
import io.github.nahkd123.pojo.plugin.PojoPlugin;
import io.github.nahkd123.pojo.plugin.command.CommandContext;
import io.github.nahkd123.pojo.plugin.command.CommandException;
import io.github.nahkd123.pojo.plugin.command.CommandExecutorWrapper;
import io.github.nahkd123.pojo.plugin.command.SimplePluginCommand;
import io.github.nahkd123.pojo.plugin.command.argument.NumberArgumentType;
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
				.withCallback(ctx -> giveItem(plugin, ctx, 1))
				.withChildren("withAmount", new SimplePluginCommand()
					.withArgument("amount", NumberArgumentType.TYPE)
					.withCallback(
						ctx -> giveItem(plugin, ctx, ctx.getArgument("amount", NumberArgumentType.TYPE).intValue()))))
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
				}))
			.withChildren("updateDisplay", new SimplePluginCommand()
				.withArgument("player", PlayerArgumentType.TYPE)
				.withCallback(ctx -> {
					Player player = ctx.getArgument("player", PlayerArgumentType.TYPE);
					ItemStack[] contents = player.getInventory().getContents();
					int updated = 0;

					for (int i = 0; i < contents.length; i++) {
						if (contents[i] == null) continue;
						PojoItem item = PojoItem.getFrom(contents[i]);
						if (item == null) continue;

						updated++;
						contents[i] = item.updateItem(contents[i]);
					}

					player.getInventory().setContents(contents);
					ctx.feedback("&eUpdated " + updated + " items in " + player.getDisplayName() + "'s inventory!");
				}));
	}

	private static void giveItem(PojoPlugin plugin, CommandContext ctx, int amount) {
		Player player = ctx.getArgument("player", PlayerArgumentType.TYPE);
		UserDefinedId id = ctx.getArgument("id", UserDefinedIdArgumentType.TYPE);
		PojoItem item = plugin.getItems().get(id);
		if (item == null) throw new CommandException("Item with ID " + id + " does not exists");

		ItemStack stack = item.createNew(false);
		stack.setAmount(amount);
		player.getInventory().addItem(stack);
		ctx.feedback("&aGave " + player.getName() + " " + amount + "x " + id + "!");
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

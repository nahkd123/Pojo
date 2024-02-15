package io.github.nahkd123.pojo.plugin.command;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import io.github.nahkd123.pojo.api.utils.TextUtils;
import io.github.nahkd123.pojo.plugin.command.argument.Argument;
import io.github.nahkd123.pojo.plugin.command.argument.WordsStream;

public class CommandExecutorWrapper implements CommandExecutor, TabCompleter {
	private PluginCommand command;

	public CommandExecutorWrapper(PluginCommand command) {
		this.command = command;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		return tabCompleteNode(command, new WordsStream(args, 0));
	}

	public List<String> tabCompleteNode(PluginCommand node, WordsStream args) {
		for (int i = 0; i < node.getArguments().size(); i++) {
			Argument<?> argument = node.getArguments().get(i);
			Optional<List<String>> result = argument.type().tryTabComplete(args);
			if (result.isPresent()) return result.get();
		}

		if (!args.isAvailable()) return Collections.emptyList();
		String subcommandName = args.next();

		if (!args.isAvailable()) {
			return node.getChildren().keySet().stream().filter(v -> v.startsWith(subcommandName)).toList();
		} else {
			LiteralCommand subcommand = node.getChildren().get(subcommandName);
			if (subcommand == null) return Collections.emptyList();
			return tabCompleteNode(subcommand.command(), args);
		}
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		try {
			SimpleCommandContext context = new SimpleCommandContext(sender);
			WordsStream stream = new WordsStream(args, 0);
			executeNode(command, context, stream);
		} catch (CommandException e) {
			sender.sendMessage(TextUtils.colorize("&cError: &f" + e.getMessage()));
			// TODO print the tree of exceptions
		}

		return true;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void executeNode(PluginCommand node, SimpleCommandContext context, WordsStream args) {
		for (int i = 0; i < node.getArguments().size(); i++) {
			Argument argument = node.getArguments().get(i);
			if (args.getPosition() >= args.getArray().length)
				throw new CommandException("Expected argument \"" + argument.name() + "\"");

			Optional result = argument.type().tryParse(args);
			if (result.isEmpty()) throw new CommandException("Invalid argument value for \"" + argument.name() + "\"");
			((Map) context.getArguments()).put(argument.name(), result.get());
		}

		if (args.isAvailable()) {
			String subcommandName = args.next();
			LiteralCommand child = node.getChildren().get(subcommandName);
			if (child == null) throw new CommandException("Unknown subcommand: " + subcommandName);
			executeNode(child.command(), context, args);
		} else {
			node.execute(context);
		}
	}
}

package io.github.nahkd123.pojo.plugin.command;

import java.util.HashMap;

import org.bukkit.command.CommandSender;

import io.github.nahkd123.pojo.plugin.command.argument.ArgumentType;

public class SimpleCommandContext implements CommandContext {
	private HashMap<String, ?> arguments = new HashMap<>();
	private CommandSender sender;

	public SimpleCommandContext(CommandSender sender) {
		this.sender = sender;
	}

	@Override
	public CommandSender getSender() { return sender; }

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getArgument(String name, ArgumentType<T> type) {
		return (T) arguments.get(name);
	}

	public HashMap<String, ?> getArguments() { return arguments; }
}

package io.github.nahkd123.pojo.plugin.command;

import java.util.List;
import java.util.Map;

import io.github.nahkd123.pojo.plugin.command.argument.Argument;

public interface PluginCommand {
	public List<Argument<?>> getArguments();

	public Map<String, LiteralCommand> getChildren();

	public void execute(CommandContext context) throws CommandException;
}

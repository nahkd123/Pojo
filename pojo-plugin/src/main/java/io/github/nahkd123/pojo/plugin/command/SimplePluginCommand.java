package io.github.nahkd123.pojo.plugin.command;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import io.github.nahkd123.pojo.plugin.command.argument.Argument;
import io.github.nahkd123.pojo.plugin.command.argument.ArgumentType;

public class SimplePluginCommand implements PluginCommand {
	private List<Argument<?>> arguments = new ArrayList<>();
	private Map<String, LiteralCommand> children = new HashMap<>();
	private Consumer<CommandContext> callback = null;

	@Override
	public List<Argument<?>> getArguments() { return arguments; }

	@Override
	public Map<String, LiteralCommand> getChildren() { return children; }

	public SimplePluginCommand withArgument(String name, ArgumentType<?> type) {
		arguments.add(new Argument<>(name, type));
		return this;
	}

	public SimplePluginCommand withChildren(String name, PluginCommand child) {
		children.put(name, new LiteralCommand(name, child));
		return this;
	}

	public SimplePluginCommand withCallback(Consumer<CommandContext> callback) {
		this.callback = callback;
		return this;
	}

	@Override
	public void execute(CommandContext context) throws CommandException {
		if (callback == null) {
			String args = arguments.stream().map(v -> "&6<&e" + v.name() + "&6>").collect(Collectors.joining(" "));
			String childrenNames = children.keySet().stream().map(v -> "&f" + v).collect(Collectors.joining(" &7| "));
			context.feedback("&cUsage: &7..."
				+ (!args.isEmpty() ? args + " " : "")
				+ (!childrenNames.isEmpty() ? "<" + childrenNames + ">" : ""));
		} else {
			callback.accept(context);
		}
	}
}

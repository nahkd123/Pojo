package io.github.nahkd123.pojo.plugin.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import io.github.nahkd123.pojo.api.utils.TextUtils;
import io.github.nahkd123.pojo.plugin.command.argument.Argument;
import io.github.nahkd123.pojo.plugin.command.argument.ArgumentType;

public interface CommandContext {
	public CommandSender getSender();

	default Player getPlayerOrThrow() throws CommandException {
		if (!(getSender() instanceof Player p)) throw new CommandException("You must be a player to use this command");
		return p;
	}

	default void feedback(String message) {
		getSender().sendMessage(TextUtils.colorize(message));
	}

	public <T> T getArgument(String name, ArgumentType<T> type);

	default <T> T getArgument(Argument<T> argument) {
		return getArgument(argument.name(), argument.type());
	}
}

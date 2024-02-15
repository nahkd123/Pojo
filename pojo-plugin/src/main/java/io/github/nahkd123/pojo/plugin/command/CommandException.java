package io.github.nahkd123.pojo.plugin.command;

import java.io.Serial;

public class CommandException extends RuntimeException {
	@Serial
	private static final long serialVersionUID = 3624783183773615372L;

	public CommandException(String message) {
		super(message);
	}

	public CommandException(String message, CommandException child) {
		super(message, child);
	}
}

package io.github.nahkd123.pojo.plugin.command.argument;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class NumberArgumentType implements ArgumentType<Number> {
	public static final NumberArgumentType TYPE = new NumberArgumentType();

	@Override
	public Optional<List<String>> tryTabComplete(WordsStream stream) {
		if (!stream.isAvailable()) return Optional.of(Arrays.asList("0"));
		stream.next();
		return Optional.empty();
	}

	@Override
	public Optional<Number> tryParse(WordsStream stream) {
		try {
			double d = Double.parseDouble(stream.peek());
			stream.next();
			return Optional.of(d);
		} catch (NumberFormatException e) {
			return Optional.empty();
		}
	}
}

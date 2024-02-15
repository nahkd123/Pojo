package io.github.nahkd123.pojo.plugin.command.argument;

import java.util.List;
import java.util.Optional;

public interface ArgumentType<T> {
	public Optional<List<String>> tryTabComplete(WordsStream stream);

	public Optional<T> tryParse(WordsStream stream);
}

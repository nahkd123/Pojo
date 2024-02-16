package io.github.nahkd123.pojo.plugin.command.argument;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import io.github.nahkd123.pojo.api.registry.UserDefinedId;

public class UserDefinedIdArgumentType implements ArgumentType<UserDefinedId> {
	public static final UserDefinedIdArgumentType TYPE = new UserDefinedIdArgumentType();

	@Override
	public Optional<List<String>> tryTabComplete(WordsStream stream) {
		if (!stream.isAvailable()) return Optional.of(Collections.singletonList("pojo:"));
		String s = stream.next();
		if (stream.isAvailable()) return Optional.empty();
		if (!s.contains(":")) return Optional.of(Collections.singletonList(s + ":"));
		return Optional.of(Collections.emptyList());
	}

	@Override
	public Optional<UserDefinedId> tryParse(WordsStream stream) {
		try {
			return Optional.of(UserDefinedId.fromString(stream.next()));
		} catch (IllegalArgumentException e) {
			return Optional.empty();
		}
	}
}

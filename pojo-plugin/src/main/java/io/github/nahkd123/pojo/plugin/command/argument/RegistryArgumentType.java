package io.github.nahkd123.pojo.plugin.command.argument;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;

import io.github.nahkd123.pojo.api.registry.UserDefinedId;

public class RegistryArgumentType extends UserDefinedIdArgumentType {
	private Supplier<Set<UserDefinedId>> keys;

	public RegistryArgumentType(Supplier<Set<UserDefinedId>> keys) {
		this.keys = keys;
	}

	@Override
	public Optional<List<String>> tryTabComplete(WordsStream stream) {
		if (!stream.isAvailable()) return Optional.of(keys.get().stream().map(Object::toString).toList());
		String s = stream.next();
		if (stream.isAvailable()) return Optional.empty();
		return Optional.of(keys.get().stream().map(Object::toString).filter(v -> v.startsWith(s)).toList());
	}
}

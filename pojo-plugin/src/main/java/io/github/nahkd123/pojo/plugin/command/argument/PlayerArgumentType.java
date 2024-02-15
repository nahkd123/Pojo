package io.github.nahkd123.pojo.plugin.command.argument;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class PlayerArgumentType implements ArgumentType<Player> {
	public static final PlayerArgumentType TYPE = new PlayerArgumentType();

	@Override
	public Optional<List<String>> tryTabComplete(WordsStream stream) {
		Collection<? extends Player> players = Bukkit.getOnlinePlayers();

		if (!stream.isAvailable()) return Optional.of(players.stream().map(Player::getName).toList());
		String s = stream.next();
		if (stream.isAvailable()) return Optional.empty();
		return Optional.of(players.stream().map(Player::getName).filter(v -> v.startsWith(s)).toList());
	}

	@Override
	public Optional<Player> tryParse(WordsStream stream) {
		return Optional.ofNullable(Bukkit.getPlayer(stream.next()));
	}
}

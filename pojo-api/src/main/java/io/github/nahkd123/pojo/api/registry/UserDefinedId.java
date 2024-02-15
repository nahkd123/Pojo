package io.github.nahkd123.pojo.api.registry;

import org.bukkit.NamespacedKey;

/**
 * <p>
 * A record for all user-defined IDs. Each ID contains a namespace and the
 * actual ID itself.
 * </p>
 */
public record UserDefinedId(String namespace, String id) {
	public static UserDefinedId fromString(String id) {
		String[] split = id.split("\\:", 2);
		if (split.length == 1) return new UserDefinedId("pojo", id);
		else return new UserDefinedId(split[0], split[1]);
	}

	public UserDefinedId(NamespacedKey key) {
		this(key.getNamespace(), key.getKey());
	}

	@Override
	public String toString() {
		return namespace + ":" + id;
	}
}

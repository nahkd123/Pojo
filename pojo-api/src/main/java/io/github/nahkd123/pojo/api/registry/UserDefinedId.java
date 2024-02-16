package io.github.nahkd123.pojo.api.registry;

import org.bukkit.NamespacedKey;

/**
 * <p>
 * A record for all user-defined IDs. Each ID contains a namespace and the
 * actual ID itself.
 * </p>
 */
public record UserDefinedId(String namespace, String id) {
	public UserDefinedId {
		if (!validate(namespace, "abcdefghijklmnopqrstuvwxyz0123456789-_"))
			throw new IllegalArgumentException("Namespace '" + namespace + "' does not match [a-z0-9-_] pattern");
		if (!validate(id, "abcdefghijklmnopqrstuvwxyz0123456789-_/."))
			throw new IllegalArgumentException("ID '" + id + "' does not match [a-z0-9-_/.] pattern");
	}

	/**
	 * <p>
	 * Convert a string to {@link UserDefinedId}. The given string must follows the
	 * format {@code [<namespace>:]<id>}, where {@code namespace} matches the
	 * {@code [a-z0-9-_]} pattern and {@code id} matches the {@code [a-z0-9-_/]}.
	 * The input string will be converted to lowercase and the default namespace is
	 * {@code pojo}.
	 * </p>
	 * 
	 * @param id The ID as string, following the {@code [<namespace>:]<id>} format.
	 * @return The ID object.
	 */
	public static UserDefinedId fromString(String id) {
		id = id.toLowerCase();
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

	private static boolean validate(String input, String charsSet) {
		for (int i = 0; i < input.length(); i++)
			if (charsSet.indexOf(input.charAt(i)) == -1) return false;
		return true;
	}
}

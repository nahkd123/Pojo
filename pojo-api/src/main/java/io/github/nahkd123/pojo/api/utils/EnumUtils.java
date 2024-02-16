package io.github.nahkd123.pojo.api.utils;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public class EnumUtils {
	public static String toFriendlyName(Enum<?> enumObj) {
		return Stream.of(enumObj.toString().split("_"))
			.map(v -> v.substring(0, 1).toUpperCase() + v.substring(1).toLowerCase())
			.collect(Collectors.joining(" "));
	}
}

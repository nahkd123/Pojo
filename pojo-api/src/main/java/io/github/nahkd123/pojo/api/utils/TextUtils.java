package io.github.nahkd123.pojo.api.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.ChatColor;

public class TextUtils {
	private static final Pattern HEX = Pattern.compile("&#([0-9A-Fa-f]{6})");

	public static String colorize(String input) {
		return ChatColor.translateAlternateColorCodes('&',
			HEX.matcher(input).replaceAll(result -> Matcher.quoteReplacement(rgbToHex(input))));
	}

	public static String rgbToHex(String rgb) {
		char[] cs = new char[14];
		for (int i = 0; i < 14; i += 2) cs[i] = '\u00a7';
		cs[1] = 'x';
		for (int i = 0; i < 6; i++) cs[3 + i * 2] = rgb.charAt(i);
		return String.valueOf(cs);
	}
}

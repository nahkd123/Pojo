package io.github.nahkd123.pojo.expansion.utils;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PlaceholderUtils {
	public static final Pattern BRACKET = Pattern.compile("[{](.+?)[}]");
	private static final Map<String, BiFunction<String, Supplier<String>, String>> TRANSFORMERS = new HashMap<>();

	/**
	 * <p>
	 * Register a input transformer. Transformers basically transforms the input
	 * text, usually from placeholder variable, along with bonus arguments and
	 * returns a transformed text.
	 * </p>
	 * <p>
	 * To use the transformer, use
	 * {@code <bracket>...<name>;<...parameters>...<bracket>}
	 * </p>
	 * 
	 * @param type        The transformer type.
	 * @param transformer The transformer function. The first parameter is the
	 *                    transformer input, the second parameter is the
	 *                    transformer's parameters supplier.
	 */
	public static void registerTransformer(String type, BiFunction<String, Supplier<String>, String> transformer) {
		TRANSFORMERS.put(type, transformer);
	}

	public static String applyPlaceholder(UnaryOperator<String> getter, String innerContent) {
		String[] split = innerContent.split(";");
		AtomicInteger pos = new AtomicInteger(0);

		String input = getter.apply(split[pos.getAndAdd(1)]);
		if (input == null) return "{" + innerContent + "}";

		while (pos.get() < split.length) {
			BiFunction<String, Supplier<String>, String> tf = TRANSFORMERS.get(split[pos.getAndAdd(1)]);
			if (tf == null) return "{" + innerContent + "}";
			input = tf.apply(input, () -> split[pos.getAndAdd(1)]);
			if (input == null) return "{" + innerContent + "}";
		}

		return input;
	}

	public static String apply(UnaryOperator<String> getter, String content) {
		return BRACKET
			.matcher(content)
			.replaceAll(match -> Matcher.quoteReplacement(applyPlaceholder(getter, match.group(1))));
	}

	static {
		DecimalFormat percentageFormatter = new DecimalFormat("#,##0.##%");
		DecimalFormat numberFormatter = new DecimalFormat("#,##0.##");
		Map<String, DecimalFormat> cachedFormatters = new HashMap<>();

		registerTransformer("percentage", (input, params) -> percentageFormatter.format(Double.parseDouble(input)));
		registerTransformer("number", (input, params) -> numberFormatter.format(Double.parseDouble(input)));
		registerTransformer("formatted", (input, params) -> {
			String formatterName = params.get();
			DecimalFormat formatter = cachedFormatters.get(formatterName);
			if (formatter == null) cachedFormatters.put(formatterName, formatter = new DecimalFormat(formatterName));
			return formatter.format(Double.parseDouble(input));
		});
	}
}

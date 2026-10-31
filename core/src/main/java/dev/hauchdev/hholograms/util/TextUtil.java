package dev.hauchdev.hholograms.util;

import java.util.regex.Pattern;

public final class TextUtil {
    private static final Pattern DANGEROUS = Pattern.compile("(?i)</?(click|hover|insertion|keybind|font|translatable|selector|score)(:[^>]*)?>");

    private TextUtil() {}

    public static String sanitize(String input, boolean trusted) {
        if (input == null) return "";
        if (trusted) return input;
        return DANGEROUS.matcher(input).replaceAll("");
    }
}
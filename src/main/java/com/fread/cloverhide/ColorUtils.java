package com.fread.cloverhide;

import net.md_5.bungee.api.ChatColor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ColorUtils {

    // Шаблон для поиска &#RRGGBB
    private static final Pattern HEX_PATTERN = Pattern.compile("(&#)([A-Fa-f0-9]{6})");

    /**
     * Перевод &-кодов (включая hex) в цветовые коды §.
     */
    public static String translateColorCodes(String message) {
        if (message == null) return null;

        // Сначала обрабатываем hex-цвета вида &#RRGGBB
        Matcher matcher = HEX_PATTERN.matcher(message);
        while (matcher.find()) {
            String fullMatch = matcher.group(0); // "&#FF0000"
            String hex = matcher.group(2);       // "FF0000"

            ChatColor hexColor = ChatColor.of("#" + hex);
            message = message.replace(fullMatch, hexColor.toString());
        }

        // Затем стандартные &-коды (&c, &l, &6, etc.)
        message = ChatColor.translateAlternateColorCodes('&', message);
        return message;
    }
}
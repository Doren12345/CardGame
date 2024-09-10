package org.doren.cardgame;

import net.md_5.bungee.api.ChatColor;
import org.doren.cardgame.manager.LanguageManager;

import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {

    public static String getLangData(String key) {
        LanguageManager langManager = LanguageManager.getInstance();
        if (langManager == null) {
            throw new IllegalStateException("LanguageManager is not initialized.");
        }

        return langManager.getLang(key);
    }

    /**
     * Converts a string containing legacy color codes (e.g., &1) and hex color codes (e.g., &#FFFFFF) into a format
     * usable by Minecraft with ChatColor.
     *
     * @param text The input string with legacy and/or hex color codes.
     * @return The string with color codes replaced by Minecraft-compatible codes.
     */
    @SuppressWarnings("deprecation")
    public static String translateColors(String text) {
        // Convert legacy color codes (&1, &2, etc.) to Minecraft color codes (§1, §2, etc.)
        String translatedText = ChatColor.translateAlternateColorCodes('&', text);

        // Create a pattern to match hex color codes (&#000000 or #000000)
        Pattern hexPattern = Pattern.compile("(?i)(§)?#([A-Fa-f0-9]{6})");
        Matcher matcher = hexPattern.matcher(translatedText);

        // Create a StringBuffer to hold the final string
        StringBuffer sb = new StringBuffer();

        // Replace all hex color codes with the corresponding ChatColor.of() values
        while (matcher.find()) {
            String hexColor = matcher.group(2); // Get the hex color code
            matcher.appendReplacement(sb, ChatColor.of("#" + hexColor).toString());
        }
        matcher.appendTail(sb);

        return sb.toString();
    }


    // 靜態方法來獲取所有可用的語言列表
    public static Set<String> getAllAvailableLanguages() {
        LanguageManager langManager = LanguageManager.getInstance();
        if (langManager == null) {
            throw new IllegalStateException("LanguageManager is not initialized.");
        }
        return langManager.getAllAvailableLanguages();
    }
}

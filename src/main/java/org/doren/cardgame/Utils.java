package org.doren.cardgame;

import org.doren.cardgame.manager.LanguageManager;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Utils {

    public static String getLangData(String key) {
        LanguageManager langManager = LanguageManager.getInstance();
        if (langManager == null) {
            throw new IllegalStateException("LanguageManager is not initialized.");
        }

        String Lang = langManager.getLang(key);

        Map<String, String> placeholders = new HashMap<>();

        for (String placeholder : placeholders.values()) {
            Lang.replaceAll(placeholder, placeholders.get(placeholder));
        }

        return Lang;
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

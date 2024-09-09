package org.doren.cardgame.manager;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.Set;
import java.util.logging.Logger;

public class LanguageManager {
    private static final Logger logger = Bukkit.getLogger();
    private static LanguageManager instance;

    private ConfigurationSection languageConfig;
    private YamlConfiguration allLanguagesConfig;

    /**
     * Get the singleton instance of {@code LanguageManager}.
     *
     * @return The singleton instance of {@code LanguageManager}.
     */
    public static synchronized LanguageManager getInstance() {
        return instance != null ? instance : new LanguageManager();
    }

    /**
     * Retrieve a language string from the configuration based on the provided key.
     *
     * @param key The key used to look up the language string.
     * @return The language string associated with the given key.
     */
    public String getLang(String key) {
        return languageConfig != null ? languageConfig.getString(key) : null;
    }

    /**
     * Retrieve a set of all available language keys from the configuration.
     *
     * @return A set of strings representing all available language keys.
     */
    public Set<String> getAllAvailableLanguages() {
        return allLanguagesConfig != null ? allLanguagesConfig.getKeys(false) : null;
    }

    /**
     * Initialize the language manager by loading the language configuration from the specified data folder.
     *
     * @param dataFolder The folder where the language configuration file is located.
     * @param config The main configuration file used to determine the selected language.
     */
    public void initializeLanguageManager(File dataFolder, YamlConfiguration config) {
        YamlConfiguration languageConfig = loadLanguageConfig(dataFolder);

        if (languageConfig == null) {
            logger.warning("Cannot find the language config file.");
            return;
        }

        String selectedLanguage = getSelectedLanguage(config);
        setLanguageConfig(languageConfig, selectedLanguage);
    }

    /**
     * Load the language configuration file from the specified data folder.
     *
     * @param dataFolder The folder where the language configuration file is located.
     * @return The loaded YamlConfiguration or null if loading fails.
     */
    private YamlConfiguration loadLanguageConfig(File dataFolder) {
        File languageFile = new File(dataFolder, "languages.yml");
        if (!languageFile.exists()) {
            return null;
        }
        return YamlConfiguration.loadConfiguration(languageFile);
    }

    /**
     * Retrieve the selected language from the main configuration file.
     *
     * @param config The main configuration file.
     * @return The selected language as a string.
     */
    private String getSelectedLanguage(YamlConfiguration config) {
        return config.getString("language", "placeholder");
    }

    /**
     * Set the language configuration based on the selected language.
     *
     * @param languageConfig The full language configuration file.
     * @param selectedLanguage The selected language.
     */
    private void setLanguageConfig(YamlConfiguration languageConfig, String selectedLanguage) {
        try {
            this.languageConfig = languageConfig.getConfigurationSection(selectedLanguage);
            if (this.languageConfig == null || "placeholder".equals(selectedLanguage)) {
                this.languageConfig = languageConfig.getConfigurationSection("en_US");
                logger.warning("Invalid language: " + selectedLanguage + ". Using default language...");
            }
            instance = this;
        } catch (Exception e) {
            logger.severe("Failed to load language configuration: " + e.getMessage());
        }
    }
}
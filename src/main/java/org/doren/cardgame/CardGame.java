package org.doren.cardgame;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.doren.cardgame.manager.BattleManager;
import org.doren.cardgame.manager.LanguageManager;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Enumeration;
import java.util.Objects;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Logger;

public final class CardGame extends JavaPlugin {

    // Logger
    Logger logger = Bukkit.getLogger();
    YamlConfiguration config;

    @SuppressWarnings("FieldMayBeFinal")
    private LanguageManager lang = new LanguageManager();

    @Override
    public void onEnable() {
        // load config into config
        loadMainConfig();

        // language init
        lang.initializeLanguageManager(getDataFolder(), config);

        // command register
        if (this.getCommand("cardgame") == null) this.getLogger().warning("Can't Find Command to Register: cardgame");
        Objects.requireNonNull(this.getCommand("cardgame")).setExecutor(new Commands());

        // battle manager
        new BattleManager().init(config);

        // enable message
        logger.info(Utils.getLangData("log-onenable"));


    }

    public void reload() {
        // load config into config
        loadMainConfig();

        // language init
        lang.initializeLanguageManager(getDataFolder(), config);

        // battle manager
        new BattleManager().init(config);
    }

    public void saveDefaultConfig() {
        try {
            // 獲取 JAR 文件路徑
            File jarFile = new File(getClass().getProtectionDomain().getCodeSource().getLocation().toURI());

            if (jarFile.isFile()) {
                try (JarFile jar = new JarFile(jarFile)) {
                    jar.stream()
                            .filter(entry -> entry.getName().startsWith("configs/") && !entry.isDirectory())
                            .forEach(entry -> copyFileIfNotExists(jar, entry));
                }
            }
        } catch (Exception e) {
            getLogger().severe("Failed to load configs: " + e.getMessage());
        }
    }

    private void copyFileIfNotExists(JarFile jar, JarEntry entry) {
        try {
            String entryName = entry.getName();
            File targetFile = new File(getDataFolder(), entryName.substring("configs/".length()));

            if (!targetFile.exists()) {
                Files.createDirectories(targetFile.getParentFile().toPath());
                try (InputStream inputStream = jar.getInputStream(entry)) {
                    Files.copy(inputStream, targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    getLogger().info("Copied missing file: " + entryName);
                }
            }
        } catch (IOException e) {
            getLogger().severe("Failed to copy file: " + entry.getName() + " - " + e.getMessage());
        }
    }

    // 加載 config.yml
    public void loadMainConfig() {
        saveDefaultConfig();

        config = YamlConfiguration.loadConfiguration(new File(getDataFolder(), "config.yml"));
        if (config.getKeys(false).isEmpty()) {
            logger.warning("Cannot Find The Main Config. Disabling Plugin...");
            getServer().getPluginManager().disablePlugin(this);
        }
    }

    @Override
    public void onDisable() {
        logger.info("Plugin Disabled.");
    }
}

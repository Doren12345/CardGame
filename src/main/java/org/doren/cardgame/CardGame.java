package org.doren.cardgame;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.doren.cardgame.manager.BattleManager;
import org.doren.cardgame.manager.LanguageManager;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Logger;

public final class CardGame extends JavaPlugin {

    private Logger logger;
    private YamlConfiguration config;
    private LanguageManager lang;

    @Override
    public void onEnable() {
        logger = this.getLogger();
        lang = new LanguageManager();

        // 初始化配置和語言管理器
        initializeConfigAndLanguage();

        // 註冊命令
        registerCommands();

        // 初始化戰鬥管理器
        new BattleManager().init(config);

        // 啟用信息
        logger.info(Utils.getLangData("log-onenable"));
    }

    @Override
    public void onDisable() {
        logger.info("Plugin Disabled.");
    }

    public void reload() {
        initializeConfigAndLanguage();
        new BattleManager().init(config);
    }

    private void initializeConfigAndLanguage() {
        loadMainConfig();
        lang.initializeLanguageManager(getDataFolder(), config);
    }

    private void registerCommands() {
        if (this.getCommand("cardgame") == null) {
            logger.warning("Can't Find Command to Register: cardgame");
        } else {
            Objects.requireNonNull(this.getCommand("cardgame")).setExecutor(new Commands());
        }
    }

    public void saveDefaultConfig() {
        try {
            File jarFile = new File(getClass().getProtectionDomain().getCodeSource().getLocation().toURI());

            if (jarFile.isFile()) {
                try (JarFile jar = new JarFile(jarFile)) {
                    jar.stream()
                            .filter(entry -> entry.getName().startsWith("configs/") && !entry.isDirectory())
                            .forEach(entry -> copyFileIfNotExists(jar, entry));
                }
            }
        } catch (Exception e) {
            logger.severe("Failed to load configs: " + e.getMessage());
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
                    logger.info("Copied missing file: " + entryName);
                }
            }
        } catch (IOException e) {
            logger.severe("Failed to copy file: " + entry.getName() + " - " + e.getMessage());
        }
    }

    private void loadMainConfig() {
        saveDefaultConfig();
        config = YamlConfiguration.loadConfiguration(new File(getDataFolder(), "config.yml"));
        if (config.getKeys(false).isEmpty()) {
            logger.warning("Cannot Find The Main Config. Disabling Plugin...");
            getServer().getPluginManager().disablePlugin(this);
        }
    }
}
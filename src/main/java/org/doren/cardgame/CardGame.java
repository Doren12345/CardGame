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
        // 從 JAR 文件中讀取 configs 內所有檔案和資料夾
        try {
            File jarFile = new File(getClass().getProtectionDomain().getCodeSource().getLocation().toURI());
            if (jarFile.isFile()) {
                try (JarFile jar = new JarFile(jarFile)) {
                    Enumeration<JarEntry> entries = jar.entries();
                    while (entries.hasMoreElements()) {
                        JarEntry entry = entries.nextElement();
                        String entryName = entry.getName();

                        // 處理 configs 目錄中的檔案和資料夾
                        if (entryName.startsWith("configs/") && !entry.isDirectory()) {
                            // 修改目標檔案路徑，移除 configs/ 前綴
                            File targetFile = new File(getDataFolder(), entryName.substring("configs/".length()));
                            if (!targetFile.exists()) {
                                // 如果目標檔案不存在，則從資源複製
                                try (InputStream inputStream = jar.getInputStream(entry)) {
                                    Files.createDirectories(targetFile.getParentFile().toPath()); // 確保目標資料夾存在
                                    Files.copy(inputStream, targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                                    getLogger().info("Copied missing file: " + entryName);
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            getLogger().severe("Failed to load configs: " + e.getMessage());
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

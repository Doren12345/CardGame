package org.doren.cardgame.gui;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Item;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.doren.cardgame.CardGame;
import org.doren.cardgame.Utils;
import org.doren.cardgame.gui.battle.battleGUI;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.doren.cardgame.gui.ItemStackMarker.markItemStack;

public class GUI {

    private final CardGame plugin = CardGame.getPlugin(CardGame.class);

    public GUI() {}

    public void init() {
        new battleGUI().init(plugin);
    }

    /**
     * 根據配置生成 Inventory
     *
     * @param guiName GUI 名稱
     * @return 生成的 Inventory 或 null（在錯誤情況下）
     */
    public Inventory generateInventoryFromConfig(String guiName) {
        List<File> matchedFiles = findFilesWithName(guiName);

        if (!validateMatchedFiles(matchedFiles, guiName)) return null;

        YamlConfiguration config = YamlConfiguration.loadConfiguration(matchedFiles.get(0));
        List<String> inventoryGUIStrings = config.getStringList("InventoryGUI");

        if (!validateInventoryGUI(inventoryGUIStrings, config.getName())) return null;

        List<String> inventoryGUISlots = parseInventoryGUISlots(inventoryGUIStrings);

        ConfigurationSection ItemsSection = config.getConfigurationSection("Items");
        if (ItemsSection == null) {
            plugin.getLogger().warning("Failed to load ItemsSection.");
            return null;
        }
        Map<String, Object> itemsMap = ItemsSection.getValues(false);
        List<ItemStack> inventoryItems = buildInventoryItems(inventoryGUISlots, itemsMap);

        if (inventoryItems == null) {
            plugin.getLogger().warning("Failed to build inventory items.");
            return null;
        }

        Inventory inventory = createInventory(guiName, inventoryGUIStrings.size());
        populateInventory(inventory, inventoryItems);

        return inventory;
    }

    /**
     * 驗證文件匹配情況
     */
    private boolean validateMatchedFiles(List<File> matchedFiles, String guiName) {
        if (matchedFiles.size() != 1) {
            plugin.getLogger().warning(matchedFiles.isEmpty()
                    ? "Can't Find The GUI: " + guiName
                    : "Impossible: matchedFiles Size > 1");
            return false;
        }
        return true;
    }

    /**
     * 驗證 Inventory GUI 配置
     */
    private boolean validateInventoryGUI(List<String> inventoryGUIStrings, String configName) {
        if (inventoryGUIStrings.isEmpty()) {
            plugin.getLogger().warning("Missing or incorrect InventoryGUI Section in UIconfig: " + configName);
            return false;
        }
        if (inventoryGUIStrings.size() > 6) {
            plugin.getLogger().warning("The InventoryGUI size must lower than 7 lines.");
            return false;
        }
        return true;
    }

    /**
     * 解析 Inventory GUI Slots
     */
    private List<String> parseInventoryGUISlots(List<String> inventoryGUIStrings) {
        List<String> inventoryGUISlots = new ArrayList<>();
        for (String line : inventoryGUIStrings) {
            String[] slots = line.split(" ");
            if (slots.length != 9) {
                plugin.getLogger().warning("InventoryGUI not correctly configured.");
                return Collections.emptyList();
            }
            Collections.addAll(inventoryGUISlots, slots);
        }
        return inventoryGUISlots;
    }

    /**
     * 創建 Inventory
     */
    @SuppressWarnings("deprecation")
    private Inventory createInventory(String guiName, int rowCount) {
        int inventorySize = rowCount * 9;
        String title = Utils.getLangData("gui-title-" + guiName);
        return Bukkit.createInventory(null, inventorySize, title);
    }

    /**
     * 填充 Inventory
     */
    private void populateInventory(Inventory inventory, List<ItemStack> items) {
        inventory.clear();
        for (int i = 0; i < items.size(); i++) {
            inventory.setItem(i, items.get(i));
        }
    }

    /**
     * 根據配置構建 Inventory Items
     */
    private List<ItemStack> buildInventoryItems(List<String> inventoryGUISlots, Map<String, Object> itemsConfigMap) {
        List<ItemStack> inventoryItems = new ArrayList<>();
        for (String itemID : inventoryGUISlots) {
            ItemStack itemStack = buildItemStackFromConfig(itemID, itemsConfigMap);
            inventoryItems.add(itemStack != null ? itemStack : new ItemStack(Material.AIR));
        }
        return inventoryItems;
    }

    /**
     * 根據配置生成單個 ItemStack
     */
    private ItemStack buildItemStackFromConfig(String itemID, Map<String, Object> itemsConfigMap) {
        Object itemConfigObj = itemsConfigMap.get(itemID);
        if (!(itemConfigObj instanceof ConfigurationSection)) {
            plugin.getLogger().warning("Invalid or missing configuration for item ID: " + itemID);
            return null;
        }

        ConfigurationSection itemConfig = (ConfigurationSection) itemConfigObj;
        String materialStr = itemConfig.getString("material");

        if (materialStr == null) {
            plugin.getLogger().warning("Material is missing for item ID: " + itemID);
            return null;
        }

        Material material = parseMaterial(materialStr);
        if (material == null) return null;

        int amount = Math.max(itemConfig.getInt("amount", 1), 1);
        ItemStack itemStack = new ItemStack(material, amount);

        setItemMeta(itemStack, itemConfig);

        return itemStack;
    }

    /**
     * 解析 Material
     */
    private Material parseMaterial(String materialStr) {
        try {
            return Material.valueOf(materialStr);
        } catch (IllegalArgumentException e) {
            plugin.getLogger().warning("Invalid material: " + materialStr);
            return null;
        }
    }

    /**
     * 設置 ItemMeta
     */
    @SuppressWarnings("deprecation")
    private void setItemMeta(ItemStack itemStack, ConfigurationSection itemConfig) {
        ItemMeta meta = itemStack.getItemMeta();
        if (meta == null) return;

        meta.setCustomModelData(itemConfig.getInt("customModelData", 0));
        String DisplayName = itemConfig.getString("name", "");
        String ColoredDisplayName = Utils.translateColors(DisplayName);
        meta.setDisplayName(ColoredDisplayName);

        List<String> lore = itemConfig.getStringList("lore")
                .stream()
                .map(Utils::translateColors)
                .collect(Collectors.toList());

        meta.setLore(lore);

        itemStack.setItemMeta(meta);

        // Marker
        String GUIAction = itemConfig.getString("action", "cancel");
        markItemStack(itemStack, "GUIAction", GUIAction);
    }

    /**
     * 查找名稱匹配的文件
     */
    private List<File> findFilesWithName(String name) {
        File folder = new File(plugin.getDataFolder(), "gui");
        if (!folder.exists() || !folder.isDirectory()) {
            plugin.getLogger().warning("Directory does not exist: " + folder.getPath());
            return Collections.emptyList();
        }

        File[] files = folder.listFiles((dir, filename) -> filename.contains(name));
        List<File> matchedFiles = new ArrayList<>();
        if (files != null) {
            Collections.addAll(matchedFiles, files);
        }
        return matchedFiles.isEmpty() ? Collections.emptyList() : matchedFiles;
    }
}
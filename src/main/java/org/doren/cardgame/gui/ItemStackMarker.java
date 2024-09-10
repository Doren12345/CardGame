package org.doren.cardgame.gui;

import org.bukkit.persistence.PersistentDataType;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.NamespacedKey;
import org.doren.cardgame.CardGame;

public class ItemStackMarker {
    private static final CardGame plugin = CardGame.getPlugin(CardGame.class);

    // 私有化構造方法以防止實例化
    private ItemStackMarker() {}

    public static void markItemStack(ItemStack itemStack, String key, String value) {
        ItemMeta meta = itemStack.getItemMeta();
        if (meta == null) return;

        NamespacedKey namespacedKey = new NamespacedKey(plugin, key);
        PersistentDataContainer dataContainer = meta.getPersistentDataContainer();
        dataContainer.set(namespacedKey, PersistentDataType.STRING, value);

        itemStack.setItemMeta(meta);
    }

    /**
     * 獲取 ItemStack 上指定鍵的標記值。
     *
     * @param itemStack 要檢查的 ItemStack
     * @param key       標記的鍵
     * @return 標記的值，如果標記存在則返回，否則返回 null
     */
    public static String getItemMarker(ItemStack itemStack, String key) {
        if (itemStack == null || !itemStack.hasItemMeta()) return null;

        ItemMeta meta = itemStack.getItemMeta();
        NamespacedKey namespacedKey = new NamespacedKey(plugin, key);
        PersistentDataContainer dataContainer = meta.getPersistentDataContainer();

        return dataContainer.get(namespacedKey, PersistentDataType.STRING);
    }
}

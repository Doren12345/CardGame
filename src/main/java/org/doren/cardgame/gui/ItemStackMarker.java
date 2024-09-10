package org.doren.cardgame.gui;

import org.bukkit.persistence.PersistentDataType;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.NamespacedKey;
import org.doren.cardgame.CardGame;

public class ItemStackMarker {
    private final CardGame plugin = CardGame.getPlugin(CardGame.class);

    public ItemStackMarker() {}
    public void markItemStack(ItemStack itemStack, String key, String value) {
        ItemMeta meta = itemStack.getItemMeta();
        if (meta == null) return;

        NamespacedKey namespacedKey = new NamespacedKey(plugin, key);
        PersistentDataContainer dataContainer = meta.getPersistentDataContainer();
        dataContainer.set(namespacedKey, PersistentDataType.STRING, value);

        itemStack.setItemMeta(meta);
    }
}

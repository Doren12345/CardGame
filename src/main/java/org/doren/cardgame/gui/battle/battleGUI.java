package org.doren.cardgame.gui.battle;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.doren.cardgame.gui.GUI;

import java.util.Arrays;

import static org.doren.cardgame.gui.ItemStackMarker.getItemMarker;

public class battleGUI implements Listener {
    private Inventory inv;

    public battleGUI() {}

    public void init(Plugin plugin) {
        inv = new GUI().generateInventoryFromConfig("battleUI");

        Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public Inventory getInventory() {
        return this.inv;
    }

    // Check for clicks on items
    @EventHandler
    public void onInventoryClick(final InventoryClickEvent e) {
        if (!e.getInventory().equals(inv)) return;

        final ItemStack clickedItem = e.getCurrentItem();

        String[] action = getItemMarker(clickedItem, "GUIAction").split(";");

        if (Arrays.asList(action).contains("cancel")) e.setCancelled(true);

        // verify current item is not null
        if (clickedItem == null || clickedItem.getType().isAir()) return;

        final Player p = (Player) e.getWhoClicked();

        // ...
    }

//    // Cancel dragging in our inventory
//    @EventHandler
//    public void onInventoryClick(final InventoryDragEvent e) {
//        if (e.getInventory().equals(inv)) {
//            e.setCancelled(true);
//        }
//    }
}

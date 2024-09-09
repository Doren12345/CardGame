package org.doren.cardgame.manager;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;
import org.doren.cardgame.CardGame;
import org.doren.cardgame.manager.battleListeners.PlayerCollisionDetection;
import org.doren.cardgame.manager.battleListeners.PlayerDamageListener;
import org.doren.cardgame.manager.battleListeners.PlayerRightClickListener;

public class BattleManager {
    public void init(YamlConfiguration config) {

        final CardGame plugin = CardGame.getPlugin(CardGame.class);

        // Impossible
        if (config == null) {
            Bukkit.getLogger().warning("Configuration is not initialized. Disabling Plugin...");
            Bukkit.getServer().getPluginManager().disablePlugin(plugin);
            return;
        }

        // battle method
        String battleMethod = config.getString("battle-method");
        switch (battleMethod.toLowerCase()) {
            case "collide":
                new PlayerCollisionDetection();
                break;

            case "left-click":
            case "right-click":
            case "shift-left-click":
            case "shift-right-click":
                boolean requiresShift = battleMethod.contains("shift");
                Listener listener = createBattleMethodListener(battleMethod, requiresShift);
                if (listener != null) {
                    Bukkit.getServer().getPluginManager().registerEvents(listener, plugin);
                } else {
                    plugin.getLogger().warning("Invalid battle method in configuration: " + battleMethod);
                }
                break;

            default:
                plugin.getLogger().warning("Unknown battle-method in configuration: " + battleMethod);
                break;
        }


        // ...
    }

    private Listener createBattleMethodListener(String battleMethod, boolean requiresShift) {
        switch (battleMethod.replace("shift-", "")) {
            case "left-click":
                return new PlayerDamageListener(requiresShift);
            case "right-click":
                return new PlayerRightClickListener(requiresShift);
            default:
                return null;
        }
    }

    public BattleManager() {}
}

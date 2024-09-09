package org.doren.cardgame.manager.battleListeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.doren.cardgame.CardGame;

public class PlayerCollisionDetection {

    private final CardGame plugin = CardGame.getPlugin(CardGame.class);

    public PlayerCollisionDetection() {
        startCollisionCheckTask();
    }

    private void startCollisionCheckTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player1 : Bukkit.getOnlinePlayers()) {
                    for (Player player2 : Bukkit.getOnlinePlayers()) {
                        if (player1.equals(player2)) {
                            continue;
                        }

                        if (player1.getLocation().distance(player2.getLocation()) < 0.5) {
                            onPlayerCollide(player1, player2);
                        }
                    }
                }
            }
        }.runTaskTimer(plugin, 0, 10);
    }

    private void onPlayerCollide(Player player1, Player player2) {
        player1.performCommand("cardgame battle " + player2.getName() + " -plugin");
    }
}
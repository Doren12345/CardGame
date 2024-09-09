package org.doren.cardgame.manager.battleListeners;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;

public class PlayerRightClickListener implements Listener {
    @SuppressWarnings("FieldMayBeFinal")
    private boolean requireShift;

    public PlayerRightClickListener(boolean requireShift) {
        this.requireShift = requireShift;
    }

    @EventHandler
    public void onPlayerRightClickedAnotherPlayer(PlayerInteractEntityEvent e) {
        Player player = e.getPlayer();
        Entity clickedEntity = e.getRightClicked();

        if (clickedEntity instanceof Player) {
            Player clickedPlayer = (Player) clickedEntity;
            if (requireShift && !player.isSneaking()) return;
            player.performCommand("cardgame battle " + clickedPlayer.getName() + " -plugin");
        }
    }
}

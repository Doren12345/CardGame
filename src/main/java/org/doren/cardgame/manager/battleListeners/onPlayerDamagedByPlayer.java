package org.doren.cardgame.manager.battleListeners;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class onPlayerDamagedByPlayer implements Listener {
    private boolean requireShift;

    public onPlayerDamagedByPlayer(boolean requireShift) {
        this.requireShift = requireShift;
    }

    @EventHandler
    private void onPlayerDamagedByPlayer(EntityDamageByEntityEvent e) {
        Entity inviterEntity = e.getDamager();
        Entity inviteeEntity = e.getEntity();
        if (inviterEntity instanceof Player && inviteeEntity instanceof Player) {
            Player inviter = (Player) inviterEntity;
            Player invitee = (Player) inviteeEntity;
            if (requireShift && !inviter.isSneaking()) return;
            inviter.performCommand("cardgame battle " + invitee.getName() + " -plugin");
        }
    }
}

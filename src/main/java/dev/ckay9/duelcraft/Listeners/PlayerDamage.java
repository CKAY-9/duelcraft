package dev.ckay9.duelcraft.Listeners;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

import dev.ckay9.duelcraft.DuelCraft;
import dev.ckay9.duelcraft.Duels.DuelType;
import dev.ckay9.duelcraft.Duels.Match;

public class PlayerDamage implements Listener {
    DuelCraft duel_craft;

    public PlayerDamage(DuelCraft duel_craft) {
        this.duel_craft = duel_craft;
    }
    
    @EventHandler(priority = EventPriority.LOWEST)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        Entity damaged_entity = event.getEntity();
        Entity damager_entity = event.getDamager();

        if (!(damaged_entity instanceof Player) || !(damager_entity instanceof Player)) {
            return;
        }
        
        Player damaged = (Player)damaged_entity;
        Player damager = (Player)damager_entity;

        Match match = Match.getCurrentPlayerMatch(damaged, this.duel_craft);
        if (match == null) {
            return;
        }

        if (!match.hasAccepted() || !match.hasStarted()) {
            return;
        }

        if (match.getDuelType() == DuelType.CLASSIC) {
            double final_health = damaged.getHealth() - event.getFinalDamage();
            boolean is_too_low = final_health < 0.5; // half heart
            if (!is_too_low) {
                return;
            }
    
            event.setCancelled(true);
            match.endGameAndDeclareWinner(damager, damaged);
        }

        if (match.getDuelType() == DuelType.SPLEEF) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onEntityDamage(EntityDamageEvent event) {
        Entity entity = event.getEntity();
        if (!(entity instanceof Player)) {
            return;
        }

        Player player = (Player)entity;
        Match match = Match.getCurrentPlayerMatch(player, this.duel_craft);
        if (match == null) {
            return;
        }

        if (!match.hasAccepted() || !match.hasStarted()) {
            return;
        }

        if (match.getDuelType() == DuelType.SPLEEF) {
            event.setCancelled(true);
        }
    }
}

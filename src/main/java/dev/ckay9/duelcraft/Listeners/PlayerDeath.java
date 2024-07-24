package dev.ckay9.duelcraft.Listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import dev.ckay9.duelcraft.DuelCraft;
import dev.ckay9.duelcraft.Duels.Match;

public class PlayerDeath implements Listener {
    DuelCraft duel_craft;

    public PlayerDeath(DuelCraft duel_craft) {
        this.duel_craft = duel_craft;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        Match.handlePlayerAbandon(player, duel_craft);
    }
}

package dev.ckay9.duelcraft.Listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import dev.ckay9.duelcraft.DuelCraft;
import dev.ckay9.duelcraft.Duels.Match;

public class PlayerLeave implements Listener {
    DuelCraft duel_craft;

    public PlayerLeave(DuelCraft duel_craft) {
        this.duel_craft = duel_craft;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerLeave(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        Match.handlePlayerAbandon(player, duel_craft);
    }
}

package dev.ckay9.duelcraft.Listeners;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;

import dev.ckay9.duelcraft.DuelCraft;
import dev.ckay9.duelcraft.Duels.DuelType;
import dev.ckay9.duelcraft.Duels.Match;

public class BlockInteract implements Listener {
    DuelCraft duel_craft;

    public BlockInteract(DuelCraft duel_craft) {
        this.duel_craft = duel_craft;
    }

    private void handleClassic(BlockDamageEvent event, Player player, Match match) {
        if (!match.hasStarted() || match.hasEnded()) {
            return;
        }

        event.setCancelled(true);
    }

    private void handleSpleef(BlockDamageEvent event, Player player, Match match) {
        if (!match.hasStarted() || match.hasEnded()) {
            return;
        }

        Block block = event.getBlock();
        Material block_type = block.getType();
        if (block_type != Material.SNOW_BLOCK && block_type != Material.SNOW) {
            event.setCancelled(true);
        } else {
            event.setInstaBreak(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onBlockDamage(BlockDamageEvent event) {
        Player player = event.getPlayer();
        Match match = Match.getCurrentPlayerMatch(player, this.duel_craft);
        if (match == null) {
            return;
        }

        if (match.getDuelType() == DuelType.CLASSIC) {
            handleClassic(event, player, match);
            return;
        }
        
        if (match.getDuelType() == DuelType.SPLEEF) {
            handleSpleef(event, player, match);
            return;
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Match match = Match.getCurrentPlayerMatch(player, this.duel_craft);
        if (match == null) {
            return;
        }
        
        if (match.getDuelType() == DuelType.SPLEEF) {
            event.setDropItems(false);
            return;
        }
    }
}

package dev.ckay9.duelcraft.Listeners;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import dev.ckay9.duelcraft.DuelCraft;
import dev.ckay9.duelcraft.Duels.DuelType;
import dev.ckay9.duelcraft.Duels.DuelWorld;
import dev.ckay9.duelcraft.Duels.Match;

public class PlayerMove implements Listener {
    DuelCraft duel_craft;

    public PlayerMove(DuelCraft duel_craft) {
        this.duel_craft = duel_craft;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Match match = Match.getCurrentPlayerMatch(player, this.duel_craft);
        if (match == null) {
            return;
        }

        if (!match.hasBeenCreated() || !match.hasAccepted() || match.hasEnded()) {
            return;
        }

        if (player.getWorld() != match.getDuelWorld().getWorld()) {
            return;
        }

        Location player_location = player.getLocation();
        if (match.getDuelType() == DuelType.SPLEEF) {
            DuelWorld duel_world = match.getDuelWorld();
            double y_pos = player_location.getY();
            double spleef_bottom_y = duel_world.getCenterLocation().getY() - (duel_world.floor_count * duel_world.wall_height) + 1;
            boolean is_below_minimum = y_pos <= spleef_bottom_y;
            if (is_below_minimum) {
                boolean is_challenged = match.getChallenged().getUniqueId() == player.getUniqueId();
                if (is_challenged) {
                    match.endGameAndDeclareWinner(match.getChallenger(), player);
                    return;
                }

                match.endGameAndDeclareWinner(match.getChallenged(), player);
                return;
            }

            return;
        }
    
        if (match.getDuelType() == DuelType.BLOWBOW) {
            DuelWorld duel_world = match.getDuelWorld();
            double y_pos = player_location.getY();
            double spleef_bottom_y = duel_world.getCenterLocation().getY() - 10;
            boolean is_below_minimum = y_pos <= spleef_bottom_y;
            if (is_below_minimum) {
                boolean is_challenged = match.getChallenged().getUniqueId() == player.getUniqueId();
                if (is_challenged) {
                    match.endGameAndDeclareWinner(match.getChallenger(), player);
                    return;
                }

                match.endGameAndDeclareWinner(match.getChallenged(), player);
                return;
            }

            return;
        }
    }
}

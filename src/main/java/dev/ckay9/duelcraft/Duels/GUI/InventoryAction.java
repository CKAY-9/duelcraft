package dev.ckay9.duelcraft.Duels.GUI;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.InventoryView;

import dev.ckay9.duelcraft.DuelCraft;
import dev.ckay9.duelcraft.Duels.Match;

public class InventoryAction implements Listener {
    DuelCraft duel_craft;

    public InventoryAction(DuelCraft duel_craft) {
        this.duel_craft = duel_craft;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onInventoryAction(InventoryCloseEvent event) {
        Player player = (Player)event.getPlayer();
        Match match = Match.getCurrentPlayerMatch(player, this.duel_craft);
        if (match == null) {
            return;
        }

        InventoryView view = event.getView();
        String title = view.getTitle();
        if (!match.hasBeenCreated() && title.contains("Duel Type")) {
            match.deleteMatch();
        }
    }
}

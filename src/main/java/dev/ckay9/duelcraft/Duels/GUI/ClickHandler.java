package dev.ckay9.duelcraft.Duels.GUI;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

import dev.ckay9.duelcraft.DuelCraft;
import dev.ckay9.duelcraft.Duels.Match;

public class ClickHandler implements Listener {
    public DuelCraft duels;

    public ClickHandler(DuelCraft duels) {
        this.duels = duels;
    }

    private void handleNavigation(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        if (event.getSlot() == ClickTypes.BACK_CLOSE_SMALL_MENU) {
            player.closeInventory();
            return;
        }

        HashMap<Integer, String> nav_items = new HashMap<Integer, String>();
        int running_total = 10;
        nav_items.put(running_total++, "challenge");
        nav_items.put(running_total++, "invites");

        if (event.getWhoClicked().isOp()) {
            nav_items.put(running_total++, "admin");
        }

        for (Integer key : nav_items.keySet()) {
            if (key != event.getSlot()) {
                continue;
            }

            switch (nav_items.get(key).toLowerCase()) {
                case "challenge":
                    Views.openChallengeMenu(player);
                    break;
                case "invites":
                    Views.openInvitesMenu(player, this.duels);
                    break;
                case "admin":
                    Views.openAdminMenu(player);
                    break;
            }
        }
    }

    private void handleInvites(InventoryClickEvent event) {
        Player clicker = (Player)event.getWhoClicked();
        int slot = event.getSlot();

        if (slot == ClickTypes.BACK_CLOSE_LARGE_MENU) {
            Views.openNavigationMenu(clicker);
            return;
        }

        int page = 1;
        ArrayList<Match> invited_matches = Match.getInvitedMatches(clicker, this.duels);
        int selected_invite_index = slot * page;
        Match selected_invite = invited_matches.get(selected_invite_index);
        if (selected_invite == null) {
            return;
        }

        if (selected_invite.getChallenged().getUniqueId() != clicker.getUniqueId()) {
            return;
        }

        selected_invite.acceptChallenge();
        clicker.closeInventory();
    }

    private void handleChallenge(InventoryClickEvent event) {
        Player clicker = (Player)event.getWhoClicked();
        int slot = event.getSlot();

        if (slot == ClickTypes.BACK_CLOSE_LARGE_MENU) {
            Views.openNavigationMenu(clicker);
            return;
        }

        int page = 1;
        int selected_player_index = slot * page;
        Player selected_player = (Player)Bukkit.getOnlinePlayers().toArray()[selected_player_index];
        if (selected_player == null) {
            return;
        }

        if (Match.isPlayerWaiting(clicker, this.duels)) {
            return;
        }

        Match new_match = new Match(this.duels, clicker, selected_player);
        if (new_match != null) {
            clicker.closeInventory();
        }
        
        this.duels.matches.add(new_match);
        new_match.notifyPlayersOfInvite();
    }

    private void handleAdmin(InventoryClickEvent event) {
        if (event.getSlot() == ClickTypes.BACK_CLOSE_SMALL_MENU) {
            Views.openNavigationMenu((Player)event.getWhoClicked());
            return;
        }
    }

    @EventHandler
    public void onGUIClick(InventoryClickEvent event) {
        Inventory clicked_inventory = event.getClickedInventory();
        if (clicked_inventory == null || clicked_inventory.getHolder() != null || clicked_inventory.getType() != InventoryType.CHEST) {
            return;
        }

        event.setCancelled(true);

        String inv_title = event.getView().getTitle();
        if (inv_title.contains("Navigation")) {
            handleNavigation(event);
            return;
        }
        if (inv_title.contains("Invites")) {
            handleInvites(event);
            return;
        }
        if (inv_title.contains("Challenge")) {
            handleChallenge(event);
            return;
        }
        if (inv_title.contains("Admin")) {
            handleAdmin(event);
            return;
        }
    }
}

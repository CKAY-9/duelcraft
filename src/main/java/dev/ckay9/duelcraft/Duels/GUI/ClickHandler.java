package dev.ckay9.duelcraft.Duels.GUI;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

import dev.ckay9.duelcraft.DuelCraft;
import dev.ckay9.duelcraft.Storage;
import dev.ckay9.duelcraft.Utils;
import dev.ckay9.duelcraft.Duels.DuelType;
import dev.ckay9.duelcraft.Duels.Match;

public class ClickHandler implements Listener {
    public DuelCraft duels;

    public ClickHandler(DuelCraft duels) {
        this.duels = duels;
    }

    private void resetPlayerStats(Player player) {
        ConfigurationSection player_section = Storage.data.getConfigurationSection("players." + player.getUniqueId());
        if (player_section == null) {
            return;
        }

        player_section.set("wins", 0);
        player_section.set("losses", 0);
        player_section.set("kills", 0);
        player_section.set("deaths", 0);
        
        try {
            Storage.data.save(Storage.data_file);
        } catch (IOException ex) {
            this.duels.getLogger().warning(ex.toString());
        }
    }

    private void cancelMatch(Player player) {
        Match match = Match.getCurrentPlayerMatch(player, this.duels);
        if (match == null) {
            return;
        }

        if (!match.hasAccepted() && !match.hasStarted() && !match.hasEnded()) {
            match.deleteMatch();
            player.sendMessage(Utils.formatText("&2&l[DUELS] Successfully deleted match!"));
            player.playSound(player, Sound.BLOCK_NOTE_BLOCK_BELL, 1, 1);
            Views.openNavigationMenu(player, this.duels);
        }
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
        nav_items.put(running_total++, "stat_reset");
        nav_items.put(running_total++, "about");

        boolean is_waiting = Match.isPlayerWaiting(player, this.duels);
        if (is_waiting) {
            nav_items.put(running_total++, "cancel");
        }

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
                case "stat_reset":
                    resetPlayerStats(player);
                    player.sendMessage(Utils.formatText("&2&l[DUELS] Successfully reset stats!"));
                    player.playSound(player, Sound.BLOCK_NOTE_BLOCK_BELL, 1, 1);
                    break;
                case "about":
                    Views.openAboutMenu(player);
                    break;
                case "invites":
                    Views.openInvitesMenu(player, this.duels);
                    break;
                case "cancel":
                    cancelMatch(player);
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
            Views.openNavigationMenu(clicker, this.duels);
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
            Views.openNavigationMenu(clicker, this.duels);
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
        this.duels.matches.add(new_match);
        Views.openDuelTypeSelect(clicker);
    }

    private void handleDuelTypeSelect(InventoryClickEvent event) {
        Player clicker = (Player)event.getWhoClicked();
        int slot = event.getSlot();

        Match match = Match.getCurrentPlayerMatch(clicker, this.duels);
        if (match == null) {
            Views.openChallengeMenu(clicker);
            return;
        }

        if (slot == ClickTypes.BACK_CLOSE_SMALL_MENU) {
            match.deleteMatch();
            Views.openChallengeMenu(clicker);
            return;
        }

        switch (slot) {
            case 10: // Classic
                match.setDuelType(DuelType.CLASSIC);
                break;
            case 11: // Spleef
                match.setDuelType(DuelType.SPLEEF);
                break;
            case 12: // Blow Bow
                match.setDuelType(DuelType.BLOWBOW);
                break;
            default:
                break;
        }

        match.notifyPlayersOfInvite();
        clicker.closeInventory();
    }

    private void clearDuels() {
        for (int i = 0; i < this.duels.matches.size(); i++) {
            Match match = this.duels.matches.get(i);
            match.cleanupMatch();
        } 

        this.duels.matches.clear();
    }

    private void resetStats() {
        ConfigurationSection players_section = Storage.data.getConfigurationSection("players");
        if (players_section == null) {
            return;
        }

        Set<String> keys = players_section.getKeys(false);
        for (String key : keys) {
            ConfigurationSection player_section = players_section.getConfigurationSection(key);
            player_section.set("wins", 0);
            player_section.set("losses", 0);
            player_section.set("kills", 0);
            player_section.set("deaths", 0);
        }

        try {
            Storage.data.save(Storage.data_file);
        } catch (IOException ex) {
            this.duels.getLogger().warning(ex.toString());
        }
    }

    private void handleAdmin(InventoryClickEvent event) {
        if (event.getSlot() == ClickTypes.BACK_CLOSE_SMALL_MENU) {
            Views.openNavigationMenu((Player)event.getWhoClicked(), this.duels);
            return;
        }

        Player clicker = (Player)event.getWhoClicked();
        if (!clicker.isOp()) {
            return;
        }

        int slot = event.getSlot();
        switch (slot) {
            case 10: // Clear duels slot
                clearDuels();
                clicker.sendMessage(Utils.formatText("&2&l[DUELS] Successfully cleared duels!"));
                clicker.playSound(clicker, Sound.BLOCK_NOTE_BLOCK_BELL, 1, 1);
                break;
            case 11: // Reset stats slot
                resetStats();
                clicker.sendMessage(Utils.formatText("&2&l[DUELS] Successfully reset stats!"));
                clicker.playSound(clicker, Sound.BLOCK_NOTE_BLOCK_BELL, 1, 1);
                break;
            default:
                break;
        }
    }

    private void handleAbout(InventoryClickEvent event) {
        Player clicker = (Player)event.getWhoClicked();
        if (event.getSlot() == ClickTypes.BACK_CLOSE_SMALL_MENU) {
            Views.openNavigationMenu(clicker, this.duels);
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
        if (inv_title.contains("About")) {
            handleAbout(event);
            return;
        }
        if (inv_title.contains("Duel Type")) {
            handleDuelTypeSelect(event);
            return;
        }
    }
}

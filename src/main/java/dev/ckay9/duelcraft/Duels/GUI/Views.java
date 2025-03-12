package dev.ckay9.duelcraft.Duels.GUI;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import dev.ckay9.duelcraft.DuelCraft;
import dev.ckay9.duelcraft.Utils;
import dev.ckay9.duelcraft.Duels.Match;

public class Views {
    public static void openNavigationMenu(Player player, DuelCraft duel_craft) {
        player.closeInventory();
        Inventory nav_inventory = Bukkit.createInventory(null, 27, Utils.formatText("&c&lDuelCraft " + DuelCraft.duels_version + ": Navigation"));
        nav_inventory.clear();
        int running_total = 10;

        nav_inventory.setItem(ClickTypes.BACK_CLOSE_SMALL_MENU, GUIHelpers.generateBackButton());

        ItemStack challenge_button = new ItemStack(Material.DIAMOND_SWORD, 1);
        ItemMeta challenge_meta = challenge_button.getItemMeta();
        challenge_meta.setDisplayName(Utils.formatText("&4&lCHALLENGE A PLAYER"));
        challenge_button.setItemMeta(challenge_meta);
        nav_inventory.setItem(running_total++, challenge_button);

        ItemStack invites_button = new ItemStack(Material.MAP, 1);
        ItemMeta invites_meta = invites_button.getItemMeta();
        invites_meta.setDisplayName(Utils.formatText("&a&lVIEW INVITES"));
        invites_button.setItemMeta(invites_meta);
        nav_inventory.setItem(running_total++, invites_button);

        ItemStack reset_stats_button = new ItemStack(Material.TNT, 1);
        ItemMeta reset_stats_meta = reset_stats_button.getItemMeta();
        reset_stats_meta.setDisplayName(Utils.formatText("&4&lRESET STATS"));
        reset_stats_button.setItemMeta(reset_stats_meta);
        nav_inventory.setItem(running_total++, reset_stats_button);

        ItemStack about_button = new ItemStack(Material.BOOK, 1);
        ItemMeta about_meta = about_button.getItemMeta();
        about_meta.setDisplayName(Utils.formatText("&5&lABOUT DUELCRAFT"));
        about_button.setItemMeta(about_meta);
        nav_inventory.setItem(running_total++, about_button);

        boolean is_waiting = Match.isPlayerWaiting(player, duel_craft);
        if (is_waiting) {
            ItemStack cancel_button = new ItemStack(Material.BARRIER, 1);
            ItemMeta cancel_meta = about_button.getItemMeta();
            cancel_meta.setDisplayName(Utils.formatText("&4&lCANCEL DUEL REQUEST"));
            cancel_button.setItemMeta(cancel_meta);
            nav_inventory.setItem(running_total++, cancel_button);
        }

        if (player.isOp()) {
            ItemStack admin_button = new ItemStack(Material.END_PORTAL_FRAME, 1);
            ItemMeta admin_meta = admin_button.getItemMeta();
            admin_meta.setDisplayName(Utils.formatText("&c&lADMIN"));
            admin_button.setItemMeta(admin_meta);
            nav_inventory.setItem(running_total++, admin_button);
        }

        player.openInventory(nav_inventory);
    }

    public static void openChallengeMenu(Player player) {
        player.closeInventory();
        Inventory challenge_inventory = Bukkit.createInventory(null, 54, Utils.formatText("&c&lDuelCraft " + DuelCraft.duels_version + ": Challenge"));
        challenge_inventory.clear();
        int running_total = 0;

        challenge_inventory.setItem(ClickTypes.BACK_CLOSE_LARGE_MENU, GUIHelpers.generateBackButton());

        for (Player ply : Bukkit.getOnlinePlayers()) {
            if (running_total >= 36) {
                break;
            }
            
            if (ply.getUniqueId() == player.getUniqueId()) {
                continue;
            }

            ItemStack player_head = new ItemStack(Material.PLAYER_HEAD, 1);
            SkullMeta head_meta = (SkullMeta) player_head.getItemMeta();
            head_meta.setDisplayName(ply.getName());
            head_meta.setOwningPlayer(Bukkit.getOfflinePlayer(ply.getUniqueId()));
            player_head.setItemMeta(head_meta);
            challenge_inventory.setItem(running_total++, player_head);
        }

        player.openInventory(challenge_inventory);
    }

    public static void openInvitesMenu(Player player, DuelCraft duel_craft) {
        player.closeInventory();
        Inventory invites_inventory = Bukkit.createInventory(null, 54, Utils.formatText("&c&lDuelCraft " + DuelCraft.duels_version + ": Invites"));
        invites_inventory.clear();
        int running_total = 0;
        int page = 1;
        int max_slot = 36 * page;
        int index_start = (page - 1) * 36;
        ArrayList<Match> invited_machtes = Match.getInvitedMatches(player, duel_craft);

        invites_inventory.setItem(ClickTypes.BACK_CLOSE_LARGE_MENU, GUIHelpers.generateBackButton());
        if (invited_machtes.size() >= max_slot) {
            invites_inventory.setItem(ClickTypes.NEXT_LARGE_MENU, GUIHelpers.generateNextButton());
        }

        for (int i = index_start; i < invited_machtes.size(); i++) {
            if (i >= max_slot) {
                break;
            }

            Match match = invited_machtes.get(i);
            Player ply = match.getChallenger();
            ItemStack player_head = new ItemStack(Material.PLAYER_HEAD, 1);
            SkullMeta head_meta = (SkullMeta) player_head.getItemMeta();
            head_meta.setDisplayName(ply.getName());
            head_meta.setOwningPlayer(Bukkit.getOfflinePlayer(ply.getUniqueId()));
            player_head.setItemMeta(head_meta);
            invites_inventory.setItem(running_total++, player_head);
        }

        player.openInventory(invites_inventory);
    }

    public static void openDuelTypeSelect(Player player) {
        player.closeInventory();
        if (!player.isOp()) {
            return;
        }
        
        Inventory duel_inventory = Bukkit.createInventory(null, 27, Utils.formatText("&c&lDuelCraft " + DuelCraft.duels_version + ": Duel Type"));
        duel_inventory.clear();
        int running_total = 10;

        duel_inventory.setItem(ClickTypes.BACK_CLOSE_SMALL_MENU, GUIHelpers.generateBackButton());

        ItemStack classic_button = new ItemStack(Material.GOLDEN_SWORD, 1);
        ItemMeta classic_meta = classic_button.getItemMeta();
        classic_meta.setDisplayName(Utils.formatText("&6&lCLASSIC DUEL"));
        List<String> classic_lore = new ArrayList<>();
        classic_lore.add(Utils.formatText("&6This is your classic 1v1. A simple circular arena, last one remaining wins!"));
        classic_meta.setLore(classic_lore);
        classic_button.setItemMeta(classic_meta);
        duel_inventory.setItem(running_total++, classic_button);

        ItemStack spleef_button = new ItemStack(Material.SNOW, 1);
        ItemMeta spleef_meta = spleef_button.getItemMeta();
        spleef_meta.setDisplayName(Utils.formatText("&3&lSPLEEF DUEL"));
        List<String> spleef_lore = new ArrayList<>();
        spleef_lore.add(Utils.formatText("&3The classic Spleef gamemode. A vertical tower, don't fall too far!"));
        spleef_meta.setLore(spleef_lore);
        spleef_button.setItemMeta(spleef_meta);
        duel_inventory.setItem(running_total++, spleef_button);

        ItemStack bow_button = new ItemStack(Material.BOW, 1);
        ItemMeta bow_meta = bow_button.getItemMeta();
        bow_meta.setDisplayName(Utils.formatText("&c&lBLOW BOW"));
        List<String> bow_lore = new ArrayList<>();
        bow_lore.add(Utils.formatText("&cTry to knock your opponent off the platform with &lEXPLOSIVE BOWS"));
        bow_meta.setLore(bow_lore);
        bow_button.setItemMeta(bow_meta);
        duel_inventory.setItem(running_total++, bow_button);

        player.openInventory(duel_inventory);
    }

    public static void openAdminMenu(Player player) {
        player.closeInventory();
        if (!player.isOp()) {
            return;
        }
        
        Inventory admin_inventory = Bukkit.createInventory(null, 27, Utils.formatText("&c&lDuelCraft " + DuelCraft.duels_version + ": Admin"));
        admin_inventory.clear();
        int running_total = 10;

        admin_inventory.setItem(ClickTypes.BACK_CLOSE_SMALL_MENU, GUIHelpers.generateBackButton());

        ItemStack clear_duels_button = new ItemStack(Material.IRON_BARS, 1);
        ItemMeta clear_duels_meta = clear_duels_button.getItemMeta();
        clear_duels_meta.setDisplayName(Utils.formatText("&c&lCLEAR ALL ONGOING DUELS"));
        List<String> cd_lore = new ArrayList<>();
        cd_lore.add(Utils.formatText("&c&lThis will cleanup and clear all duels that are active."));
        clear_duels_meta.setLore(cd_lore);
        clear_duels_button.setItemMeta(clear_duels_meta);
        admin_inventory.setItem(running_total++, clear_duels_button);

        ItemStack reset_stats_button = new ItemStack(Material.LEATHER_HELMET, 1);
        ItemMeta reset_stats_meta = reset_stats_button.getItemMeta();
        reset_stats_meta.setDisplayName(Utils.formatText("&c&lRESET ALL STATS"));
        List<String> rs_lore = new ArrayList<>();
        rs_lore.add(Utils.formatText("&c&lThis will reset all save stats for players (i.e. wins/losses are set to 0)."));
        reset_stats_meta.setLore(rs_lore);
        reset_stats_button.setItemMeta(reset_stats_meta);
        admin_inventory.setItem(running_total++, reset_stats_button);

        player.openInventory(admin_inventory);
    }

    public static void openAboutMenu(Player player) {
        player.closeInventory();
        if (!player.isOp()) {
            return;
        }

        Inventory about_inventory = Bukkit.createInventory(null, 27, Utils.formatText("&c&lDuelCraft " + DuelCraft.duels_version + ": About"));
        about_inventory.clear();

        about_inventory.setItem(ClickTypes.BACK_CLOSE_SMALL_MENU, GUIHelpers.generateBackButton());

        ItemStack information_text = new ItemStack(Material.PAPER, 1);
        ItemMeta info_meta = information_text.getItemMeta();
        info_meta.setDisplayName(Utils.formatText("&6&l* DuelCraft by CKAY9 *"));
        List<String> info_lore = new ArrayList<>();
        info_lore.add(Utils.formatText("&1Installed Version: " + DuelCraft.duels_version));
        info_lore.add(Utils.formatText("&9GitHub Repository: https://github.com/CKAY-9/duelcraft"));
        info_lore.add(Utils.formatText("DuelCraft is a simple dueling plugin for Minecraft servers."));
        info_lore.add(Utils.formatText("It offers easy-to-use and configurable 1v1 matches (duels)"));
        info_lore.add(Utils.formatText("seperated from the rest of the server."));
        info_meta.setLore(info_lore);
        information_text.setItemMeta(info_meta);
        about_inventory.setItem(13, information_text);

        player.openInventory(about_inventory);
    }
}

package dev.ckay9.duelcraft.Duels.GUI;

import java.util.ArrayList;

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
    public static void openNavigationMenu(Player player) {
        player.closeInventory();
        Inventory nav_inventory = Bukkit.createInventory(null, 27, Utils.formatText("&c&lDuelCraft: Navigation"));
        nav_inventory.clear();
        int running_total = 10;

        nav_inventory.setItem(ClickTypes.BACK_CLOSE_SMALL_MENU, GUIHelpers.generateBackButton());

        ItemStack challenge_button = new ItemStack(Material.BLACK_CONCRETE, 1);
        ItemMeta challenge_meta = challenge_button.getItemMeta();
        challenge_meta.setDisplayName(Utils.formatText("&lCHALLENGE A PLAYER"));
        challenge_button.setItemMeta(challenge_meta);
        nav_inventory.setItem(running_total++, challenge_button);

        ItemStack invites_button = new ItemStack(Material.GREEN_CONCRETE, 1);
        ItemMeta invites_meta = invites_button.getItemMeta();
        invites_meta.setDisplayName(Utils.formatText("&a&lVIEW INVITES"));
        invites_button.setItemMeta(invites_meta);
        nav_inventory.setItem(running_total++, invites_button);

        if (player.isOp()) {
            ItemStack admin_button = new ItemStack(Material.RED_CONCRETE, 1);
            ItemMeta admin_meta = admin_button.getItemMeta();
            admin_meta.setDisplayName(Utils.formatText("&c&lADMIN"));
            admin_button.setItemMeta(admin_meta);
            nav_inventory.setItem(running_total++, admin_button);
        }

        player.openInventory(nav_inventory);
    }

    public static void openChallengeMenu(Player player) {
        player.closeInventory();
        Inventory challenge_inventory = Bukkit.createInventory(null, 54, Utils.formatText("&c&lDuelCraft: Challenge"));
        challenge_inventory.clear();
        int running_total = 0;

        challenge_inventory.setItem(ClickTypes.BACK_CLOSE_LARGE_MENU, GUIHelpers.generateBackButton());

        for (Player ply : Bukkit.getOnlinePlayers()) {
            if (running_total >= 36) {
                break;
            }
            
            if (ply.getUniqueId() == player.getUniqueId()) {
                //continue;
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
        Inventory invites_inventory = Bukkit.createInventory(null, 54, Utils.formatText("&c&lDuelCraft: Invites"));
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

    public static void openAdminMenu(Player player) {
        player.closeInventory();
        Inventory nav_inventory = Bukkit.createInventory(null, 27, Utils.formatText("&c&lDuelCraft: Admin"));
        nav_inventory.clear();

        nav_inventory.setItem(ClickTypes.BACK_CLOSE_SMALL_MENU, GUIHelpers.generateBackButton());

        player.openInventory(nav_inventory);
    }
}

package dev.ckay9.duelcraft;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

public class Utils {
    public static String formatText(String s) {
        return ChatColor.translateAlternateColorCodes('&', s);
    }

    public static DuelCraft getPlugin() {
        return (DuelCraft)Bukkit.getPluginManager().getPlugin("DuelCraft");
    }
}

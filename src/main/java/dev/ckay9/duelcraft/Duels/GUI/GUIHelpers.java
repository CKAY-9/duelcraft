package dev.ckay9.duelcraft.Duels.GUI;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import dev.ckay9.duelcraft.Utils;

public class GUIHelpers {
    public static ItemStack generateBackButton() {
        ItemStack back_button = new ItemStack(Material.BARRIER, 1);
        ItemMeta back_meta = back_button.getItemMeta();
        back_meta.setDisplayName(Utils.formatText("&c&lCLOSE/BACK"));
        back_button.setItemMeta(back_meta);
        return back_button;
    }

    public static ItemStack generateNextButton() {
        ItemStack next_button = new ItemStack(Material.BOOK, 1);
        ItemMeta next_meta = next_button.getItemMeta();
        next_meta.setDisplayName(Utils.formatText("&a&lNEXT"));
        next_button.setItemMeta(next_meta);
        return next_button;
    }
}

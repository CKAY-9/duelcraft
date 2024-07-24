package dev.ckay9.duelcraft.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import dev.ckay9.duelcraft.DuelCraft;
import dev.ckay9.duelcraft.Duels.GUI.Views;

public class DuelCommand implements CommandExecutor {
    public DuelCraft duels;

    public DuelCommand(DuelCraft duels) {
        this.duels = duels;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return false;
        }

        Player player = (Player) sender;

        if (args.length == 0) {
            Views.openNavigationMenu(player);
            return false;
        }

        return false;
    }
}

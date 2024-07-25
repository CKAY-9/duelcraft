package dev.ckay9.duelcraft.Commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import dev.ckay9.duelcraft.DuelCraft;
import dev.ckay9.duelcraft.Duels.Match;
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

        String player_name = args[0];
        Player selected_player = Bukkit.getPlayerExact(player_name);
        if (selected_player == null) {
            return false;
        }

        if (Match.isPlayerWaiting(player, this.duels)) {
            return false;
        }

        Match new_match = new Match(this.duels, player, selected_player);
        if (new_match != null) {
            player.closeInventory();
        }
        
        this.duels.matches.add(new_match);
        new_match.notifyPlayersOfInvite();

        return false;
    }
}

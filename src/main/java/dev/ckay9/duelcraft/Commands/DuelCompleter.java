package dev.ckay9.duelcraft.Commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import dev.ckay9.duelcraft.DuelCraft;
import dev.ckay9.duelcraft.Duels.Match;

public class DuelCompleter implements TabCompleter {
    DuelCraft duel_craft;

    public DuelCompleter(DuelCraft duel_craft) {
        this.duel_craft = duel_craft;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return Collections.emptyList();
        }

        Player player = (Player)sender;

        if (args.length == 1) {
            ArrayList<String> player_names = new ArrayList<>();
            for (Player temp_player : Bukkit.getOnlinePlayers()) {
                player_names.add(temp_player.getName());
            }

            return player_names;            
        }

        if (args.length == 2) {
            ArrayList<String> completers = new ArrayList<>();
            ArrayList<Match> matches = Match.getInvitedMatches(player, this.duel_craft);
            boolean is_waiting = Match.isPlayerWaiting(player, this.duel_craft);

            if (!is_waiting) {
                completers.add("spleef");
                completers.add("classic");
                completers.add("blowbow");
            } else {
                completers.add("delete");
            }

            if (matches.size() >= 1) {
                completers.add("decline");
                completers.add("accept");
            }

            return completers;
        }

        return Collections.emptyList();
    }
}

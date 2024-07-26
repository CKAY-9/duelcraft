package dev.ckay9.duelcraft.Commands;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import dev.ckay9.duelcraft.DuelCraft;
import dev.ckay9.duelcraft.Utils;
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
            Views.openNavigationMenu(player, this.duels);
            return false;
        }

        String player_name = args[0];
        if (player_name == null) {
            return false;
        }

        Player selected_player = Bukkit.getPlayerExact(player_name);
        if (selected_player == null) {
            return false;
        }

        ArrayList<Match> matches = Match.getInvitedMatches(player, this.duels);
        Match active_match = null;
        boolean flag = false;
        for (int i = 0; i < matches.size(); i++) {
            Match temp = matches.get(i);
            if (temp.getChallenger().getUniqueId() == selected_player.getUniqueId()) {
                active_match = temp;
                flag = true;
                break;
            }
        }

        if (args.length <= 1) {
            if (flag && active_match != null) {
                active_match.acceptChallenge();
                return false;
            }

            Match.createClassicMatch(this.duels, player, selected_player);
            return false;
        } else {
            String sub_command = args[1];
            switch (sub_command.toLowerCase()) {
                case "classic":
                    Match.createClassicMatch(this.duels, player, selected_player);
                    break;
                case "spleef":
                    Match.createSpleefMatch(duels, player, selected_player);
                    break;
                case "accept":
                    if (active_match == null) {
                        break;
                    }
                    active_match.acceptChallenge();
                    break;
                case "decline":
                    if (active_match == null) {
                        break;
                    }
                    active_match.declineChallenge();
                    break;
                case "delete":
                    if (active_match == null) {
                        break;
                    }
                    active_match.deleteMatch();
                    player.sendMessage(Utils.formatText("&2&l[DUELS] Successfully deleted match!"));
                    player.playSound(player, Sound.BLOCK_NOTE_BLOCK_BELL, 1, 1);
                default:
                    break;
            }

            return false;
        }

        // return false;
    }
}

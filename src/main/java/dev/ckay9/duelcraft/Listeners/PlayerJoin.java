package dev.ckay9.duelcraft.Listeners;

import java.io.IOException;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import dev.ckay9.duelcraft.DuelCraft;
import dev.ckay9.duelcraft.Storage;
import dev.ckay9.duelcraft.Utils;

public class PlayerJoin implements Listener {
    private DuelCraft duels;

    public PlayerJoin(DuelCraft duels) {
        this.duels = duels;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        String player_key = "players." + player.getUniqueId().toString();
        if (!Storage.data.isSet(player_key)) {
            Storage.data.set(player_key + ".wins", 0);
            Storage.data.set(player_key + ".losses", 0);
            Storage.data.set(player_key + ".kills", 0);
            Storage.data.set(player_key + ".deaths", 0);

            try {
                Storage.data.save(Storage.data_file);
            } catch (IOException ex) {
                Utils.getPlugin().getLogger().warning(ex.toString());
            }
        }
    }
}

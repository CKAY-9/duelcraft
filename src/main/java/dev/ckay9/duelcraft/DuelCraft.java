package dev.ckay9.duelcraft;

import java.util.ArrayList;

import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import dev.ckay9.duelcraft.Commands.DuelCommand;
import dev.ckay9.duelcraft.Duels.Match;
import dev.ckay9.duelcraft.Duels.GUI.ClickHandler;
import dev.ckay9.duelcraft.Listeners.PlayerDamage;
import dev.ckay9.duelcraft.Listeners.PlayerDeath;
import dev.ckay9.duelcraft.Listeners.PlayerJoin;
import dev.ckay9.duelcraft.Listeners.PlayerLeave;
import dev.ckay9.duelcraft.Tasks.MatchClearer;

public class DuelCraft extends JavaPlugin {
    public ArrayList<Match> matches = new ArrayList<>();
    public MatchClearer match_clearer;

    @Override
    public void onEnable() {
        Storage.initializeConfigFiles();
        Storage.initializeDataFiles();

        this.match_clearer = new MatchClearer(this);
        int mc_id = this.getServer().getScheduler().scheduleSyncRepeatingTask(this, match_clearer, 20L, 20L);
        this.match_clearer.runnable_id = mc_id;

        PluginManager manager = this.getServer().getPluginManager();

        this.getCommand("duel").setExecutor(new DuelCommand(this));
        
        manager.registerEvents(new ClickHandler(this), this);
        manager.registerEvents(new PlayerJoin(this), this);
        manager.registerEvents(new PlayerLeave(this), this);
        manager.registerEvents(new PlayerDamage(this), this);
        manager.registerEvents(new PlayerDeath(this), this);
    }

    @Override
    public void onDisable() {
        for (int i = 0; i < matches.size(); i++) {
            Match match = matches.get(i);
            match.cleanupMatch();
        } 
        this.match_clearer.cancelTask();
    }
}
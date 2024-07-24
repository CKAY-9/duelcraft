package dev.ckay9.duelcraft;

import java.util.ArrayList;

import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import dev.ckay9.duelcraft.Commands.DuelCommand;
import dev.ckay9.duelcraft.Duels.Match;
import dev.ckay9.duelcraft.Duels.GUI.ClickHandler;
import dev.ckay9.duelcraft.Listeners.PlayerDeath;
import dev.ckay9.duelcraft.Listeners.PlayerJoin;
import dev.ckay9.duelcraft.Listeners.PlayerLeave;

public class DuelCraft extends JavaPlugin {
    public ArrayList<Match> matches = new ArrayList<>();

    @Override
    public void onEnable() {
        Storage.initializeConfigFiles();
        Storage.initializeDataFiles();

        PluginManager manager = this.getServer().getPluginManager();

        this.getCommand("duel").setExecutor(new DuelCommand(this));
        
        manager.registerEvents(new ClickHandler(this), this);
        manager.registerEvents(new PlayerJoin(this), this);
        manager.registerEvents(new PlayerLeave(this), this);
        manager.registerEvents(new PlayerDeath(this), this);
    }

    @Override
    public void onDisable() {
        for (int i = 0; i < matches.size(); i++) {
            Match match = matches.get(i);
            match.cleanupMatch();
        } 
    }
}
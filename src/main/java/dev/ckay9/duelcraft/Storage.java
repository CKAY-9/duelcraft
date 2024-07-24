package dev.ckay9.duelcraft;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

public class Storage {
    public static File config_file;
    public static YamlConfiguration config;

    public static File data_file;
    public static YamlConfiguration data;

    public static void initializeDataFiles() {
        try {
            data_file = new File(Utils.getPlugin().getDataFolder(), "data.yml");
            if (!data_file.exists()) {
                if (data_file.getParentFile().mkdirs()) {
                    Utils.getPlugin().getLogger().info("Created data folder");
                }
                if (data_file.createNewFile()) {
                    Utils.getPlugin().getLogger().info("Created duels data file");
                }
            }

            data = YamlConfiguration.loadConfiguration(data_file);
        } catch (IOException ex) {
            Utils.getPlugin().getLogger().warning(ex.toString());
        }
    }

    public static void initializeConfigFiles() {
        try {
            config_file = new File(Utils.getPlugin().getDataFolder(), "config.yml");
            if (!config_file.exists()) {
                if (config_file.getParentFile().mkdirs()) {
                    Utils.getPlugin().getLogger().info("Created data folder");
                }
                if (config_file.createNewFile()) {
                    Utils.getPlugin().getLogger().info("Created config file");
                }
            }

            config = YamlConfiguration.loadConfiguration(config_file);
            if (!config.isSet("config.match_accept_timeout")) {
                config.set("config.match_accept_timeout", 300);
            }

            if (!config.isSet("config.match_accept_timeout")) {
                config.set("config.match_accept_timeout", 300);
            }

            if (!config.isSet("config.duel.hotbar")) {
                ConfigurationSection section = config.getConfigurationSection("config.duel.hotbar");
                section.set("config.duel.hotbar.someid", section);
            }

            config.save(config_file);
        } catch (IOException ex) {
            Utils.getPlugin().getLogger().warning(ex.toString());
        }
    }

}

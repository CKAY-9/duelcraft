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

            if (!config.isSet("config.spleef.wall_height")) {
                config.set("config.spleef.wall_height", 5);
            }

            if (!config.isSet("config.spleef.radius")) {
                config.set("config.spleef.radius", 10);
            }

            if (!config.isSet("config.blowbow.platform_radius")) {
                config.set("config.blowbow.platform_radius", 8);
            }

            if (!config.isSet("config.spleef.floors")) {
                config.set("config.spleef.floors", 8);
            }

            if (!config.isSet("config.arena.radius")) {
                config.set("config.arena.radius", 32);
            }

            if (!config.isSet("config.arena.wall_height")) {
                config.set("config.arena.wall_height", 10);
            }

            if (!config.isSet("config.duel.hotbar")) {
                config.createSection("config.duel.hotbar");
                ConfigurationSection section = config.getConfigurationSection("config.duel.hotbar");
                section.set("someid1.material", "minecraft:netherite_sword");
                section.set("someid1.count", 1);
                section.set("someid2.material", "minecraft:netherite_axe");
                section.set("someid2.count", 1);
                section.set("someid3.material", "minecraft:enchanted_golden_apple");
                section.set("someid3.count", 64);
            }

            if (!config.isSet("config.duel.off_hand")) {
                config.createSection("config.duel.off_hand");
                ConfigurationSection section = config.getConfigurationSection("config.duel.off_hand");
                section.set("material", "minecraft:shield");
                section.set("count", 64);
            }

            if (!config.isSet("config.duel.armor")) {
                config.createSection("config.duel.armor");
                ConfigurationSection section = config.getConfigurationSection("config.duel.armor");
                List<String> armor_pieces = new ArrayList<>();
                armor_pieces.add("minecraft:netherite_helmet");
                armor_pieces.add("minecraft:netherite_chestplate");
                armor_pieces.add("minecraft:netherite_leggings");
                armor_pieces.add("minecraft:netherite_boots");
                section.set("pieces", armor_pieces);
            }

            config.save(config_file);
        } catch (IOException ex) {
            Utils.getPlugin().getLogger().warning(ex.toString());
        }
    }

}

package dev.ckay9.duelcraft.Duels;

import java.util.Random;

import org.bukkit.Difficulty;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.generator.ChunkGenerator;
import org.joml.Math;

import dev.ckay9.duelcraft.DuelCraft;
import dev.ckay9.duelcraft.Storage;

public class DuelWorld extends ChunkGenerator {
    DuelCraft duels;
    long world_id = -1;
    World duel_world;
    Location center_location;
    private int teleport_count = 0;
    public int arena_radius = Storage.config.getInt("config.arena.radius", 32);
    public int wall_height = Storage.config.getInt("config.arena.wall_height", 10);
    public int floor_count = Storage.config.getInt("config.spleef.floors", 8);

    public DuelWorld(DuelCraft duels) {
        this.duels = duels;
    }

    public World generateWorld(String name) {
        WorldCreator world_creator = new WorldCreator("match_" + name);

        world_creator.type(WorldType.FLAT);
        world_creator.generateStructures(false);

        World new_world = world_creator.createWorld();
        new_world.setDifficulty(Difficulty.PEACEFUL);
        return new_world;
    }

    private Material randomWallMaterial() {
        Material[] materials = new Material[] {
                Material.STONE_BRICKS,
                Material.STONE_BRICKS,
                Material.STONE_BRICKS,
                Material.COBBLESTONE,
                Material.MOSSY_STONE_BRICKS,
                Material.CRACKED_STONE_BRICKS,
                Material.CHISELED_STONE_BRICKS
        };

        Random random = new Random();
        int random_index = random.nextInt(0, materials.length);
        return materials[random_index];
    }

    public void constructSpleefArena() {
        Location center_location = this.getCenterLocation();
        wall_height = Storage.config.getInt("config.spleef.wall_height", 5);
        arena_radius = Storage.config.getInt("config.spleef.radius", 10);

        // Fill floor
        for (int floor = 0; floor < (floor_count + 1); floor++) {
            for (int x = -100; x < 100; x++) {
                for (int z = -100; z < 100; z++) {
                    if (floor == floor_count) {
                        Block block = this.getWorld().getBlockAt(
                                new Location(this.getWorld(),
                                        center_location.getX() + x,
                                        center_location.getY() - (1 + (floor * wall_height)),
                                        center_location.getZ() + z));
                        block.setType(Material.BEDROCK);
                        continue;
                    }

                    Block block = this.getWorld().getBlockAt(
                            new Location(this.getWorld(),
                                    center_location.getX() + x,
                                    center_location.getY() - (1 + (floor * wall_height)),
                                    center_location.getZ() + z));
                    block.setType(Material.SNOW_BLOCK);

                    Block light_block = this.getWorld().getBlockAt(
                            new Location(this.getWorld(),
                                    center_location.getX() + x,
                                    center_location.getY() - (floor * wall_height),
                                    center_location.getZ() + z));
                    light_block.setType(Material.LIGHT);
                }
            }
        }

        // Construct top walls
        int wall_count = arena_radius;
        double wall_deg_per_turn = 1;
        for (int wall = 0; wall < wall_count; wall++) {
            for (int deg = 0; deg < 360; deg += wall_deg_per_turn) {
                double rads = Math.toRadians(deg);
                double x_distance = Math.cos(rads) * (arena_radius - wall);
                double z_distance = Math.sin(rads) * (arena_radius - wall);

                for (int y_add = 0; y_add < wall_height; y_add++) {
                    Location block_location = new Location(
                            this.getWorld(),
                            center_location.getX() + x_distance,
                            center_location.getY() + y_add + (wall * wall_height),
                            center_location.getZ() + z_distance);
                    Block block = this.getWorld().getBlockAt(block_location);
                    if (y_add == 0 && wall != 0) {
                        block.setType(Material.GLOWSTONE);
                    } else {
                        block.setType(this.randomWallMaterial());
                    }
                }
            }
        }

        // Construct down walls
        for (int floor = 0; floor < floor_count; floor++) {
            for (int deg = 0; deg < 360; deg += wall_deg_per_turn) {
                double rads = Math.toRadians(deg);
                double x_distance = Math.cos(rads) * (arena_radius);
                double z_distance = Math.sin(rads) * (arena_radius);

                for (int y_add = 0; y_add < wall_height; y_add++) {
                    Location block_location = new Location(
                            this.getWorld(),
                            center_location.getX() + x_distance,
                            center_location.getY() - (y_add + (floor * wall_height)),
                            center_location.getZ() + z_distance);
                    Block block = this.getWorld().getBlockAt(block_location);
                    block.setType(this.randomWallMaterial());
                }
            }
        }
    }

    public void constructBlowBowArena() {
        Location center_location = this.getCenterLocation();
        this.arena_radius = Storage.config.getInt("config.blowbow.platform_radius", 32);
        this.wall_height = 40;
        double deg_per_turn = 1;

        for (int x = -arena_radius * 5; x < arena_radius * 5; x++) {
            for (int z = -arena_radius * 5; z < arena_radius * 5; z++) {
                Location point = new Location(
                        this.getWorld(),
                        center_location.getX() + x,
                        center_location.getY() - 10,
                        center_location.getZ() + z);
                Block block = this.getWorld().getBlockAt(point);
                block.setType(Material.LAVA);
            }
        }

        for (int x = -arena_radius; x < arena_radius; x++) {
            for (int z = -arena_radius; z < arena_radius; z++) {
                Location point = new Location(
                        this.getWorld(),
                        center_location.getX() + x,
                        center_location.getY() - 1,
                        center_location.getZ() + z);
                double distance = Math
                        .sqrt(((point.getX() - center_location.getX()) * (point.getX() - center_location.getX()))
                                + ((point.getZ() - center_location.getZ()) * (point.getZ() - center_location.getZ())));
                boolean inside_check = distance < arena_radius;
                if (!inside_check) {
                    continue;
                }

                Block block = this.getWorld().getBlockAt(point);
                block.setType(Material.WHITE_CONCRETE);
            }
        }

        for (int deg = 0; deg < 360; deg += deg_per_turn) {
            double rads = Math.toRadians(deg);
            double x_distance = Math.cos(rads) * (arena_radius * 2);
            double z_distance = Math.sin(rads) * (arena_radius * 2);

            for (int y_add = 0; y_add < wall_height; y_add++) {
                Location block_location = new Location(
                        this.getWorld(),
                        center_location.getX() + x_distance,
                        center_location.getY() + y_add,
                        center_location.getZ() + z_distance);
                Block block = this.getWorld().getBlockAt(block_location);
                block.setType(Material.OBSIDIAN);
            }
        }
    }

    public void constructClassicArena() {
        Location center_location = this.getCenterLocation();

        // Fill floor
        for (int x = -100; x < 100; x++) {
            for (int z = -100; z < 100; z++) {
                Block block = this.getWorld().getBlockAt(
                        new Location(this.getWorld(),
                                center_location.getX() + x,
                                center_location.getY() - 1,
                                center_location.getZ() + z));

                block.setType(Material.GRASS_BLOCK);
            }
        }

        // Construct light rights
        int ring_count = (int) Math.ceil(arena_radius * 0.5);
        int blocks_per_ring = 4;
        double light_deg_per_turn = 1;
        for (int ring = 0; ring < ring_count; ring++) {
            for (int deg = 0; deg < 360; deg += light_deg_per_turn) {
                double rads = Math.toRadians(deg);
                double x_distance = Math.cos(rads) * (ring * blocks_per_ring);
                double z_distance = Math.sin(rads) * (ring * blocks_per_ring);
                Location block_location = new Location(
                        this.getWorld(),
                        center_location.getX() + x_distance,
                        center_location.getY() - 1,
                        center_location.getZ() + z_distance);
                Block block = this.getWorld().getBlockAt(block_location);
                block.setType(Material.GLOWSTONE);
            }
        }

        // Construct walls
        int wall_count = arena_radius;
        double wall_deg_per_turn = 1;
        for (int wall = 0; wall < wall_count; wall++) {
            for (int deg = 0; deg < 360; deg += wall_deg_per_turn) {
                double rads = Math.toRadians(deg);
                double x_distance = Math.cos(rads) * (arena_radius - wall);
                double z_distance = Math.sin(rads) * (arena_radius - wall);

                for (int y_add = 0; y_add < wall_height; y_add++) {
                    Location block_location = new Location(
                            this.getWorld(),
                            center_location.getX() + x_distance,
                            center_location.getY() + y_add + (wall * wall_height),
                            center_location.getZ() + z_distance);
                    Block block = this.getWorld().getBlockAt(block_location);
                    if (y_add == 0 && wall != 0) {
                        block.setType(Material.GLOWSTONE);
                    } else {
                        block.setType(this.randomWallMaterial());
                    }
                }
            }
        }
    }

    public void teleportPlayerToWorldSpawn(Player player) {
        if (this.getWorldID() == -1 || this.getWorld() == null) {
            return;
        }

        // teleport players to different positions
        if (teleport_count % 2 == 0) {
            player.teleport(this.getCenterLocation().clone().add(arena_radius * 0.5, 0, 0));
        } else {
            player.teleport(this.getCenterLocation().clone().add(-(arena_radius * 0.5), 0, 0));
        }

        teleport_count++;
    }

    public static Match getMatchFromWorld(DuelCraft duel_craft, World world) {
        for (int i = 0; i < duel_craft.matches.size(); i++) {
            Match match = duel_craft.matches.get(i);
            DuelWorld duel_world = match.getDuelWorld();
            World a_world = duel_world.getWorld();
            
            if (a_world == world) {
                return match;
            }
        }

        return null;
    }

    public long getWorldID() {
        return this.world_id;
    }

    public World getWorld() {
        return this.duel_world;
    }

    public Location getCenterLocation() {
        return this.center_location;
    }

    public void setWorldID(long id) {
        this.world_id = id;
    }

    public void setWorld(World world) {
        this.duel_world = world;
    }

    public void setCenterLocation(Location location) {
        this.center_location = location;
    }
}

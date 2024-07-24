package dev.ckay9.duelcraft.Duels;

import java.util.Random;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.generator.ChunkGenerator;

import dev.ckay9.duelcraft.DuelCraft;

public class DuelWorld extends ChunkGenerator {
    DuelCraft duels;
    long world_id = -1;
    World duel_world;
    Location center_location;

    public DuelWorld(DuelCraft duels) {
        this.duels = duels;
    }

    public World generateWorld(String name) {
        WorldCreator world_creator = new WorldCreator(name);

        world_creator.environment(World.Environment.NORMAL);
        world_creator.type(WorldType.FLAT);

        World new_world = world_creator.createWorld();
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

    public void constructArena() {
        Location center_location = this.getCenterLocation();
        int radius = 15;

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
        int ring_count = 3;
        int blocks_per_ring = 5;
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
                    center_location.getZ() + z_distance
                );
                Block block = this.getWorld().getBlockAt(block_location);
                block.setType(Material.GLOWSTONE);
            }
        }

        // Construct walls
        int wall_height = 10;
        int wall_count = radius;
        double wall_deg_per_turn = 1;
        for (int wall = 0; wall < wall_count; wall++) {
            for (int deg = 0; deg < 360; deg += wall_deg_per_turn) {
                double rads = Math.toRadians(deg);
                double x_distance = Math.cos(rads) * (radius - wall);
                double z_distance = Math.sin(rads) * (radius - wall);

                for (int y_add = 0; y_add < wall_height; y_add++) {
                    Location block_location = new Location(
                        this.getWorld(),
                        center_location.getX() + x_distance,
                        center_location.getY() + y_add + (wall * wall_height),
                        center_location.getZ() + z_distance
                    );
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

        player.teleport(this.getCenterLocation());
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

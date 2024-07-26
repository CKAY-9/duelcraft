package dev.ckay9.duelcraft.Listeners;

import org.bukkit.World;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.projectiles.ProjectileSource;

import dev.ckay9.duelcraft.DuelCraft;
import dev.ckay9.duelcraft.Duels.DuelWorld;
import dev.ckay9.duelcraft.Duels.Match;

public class ProjectileEvents implements Listener {
    DuelCraft duel_craft;

    public ProjectileEvents(DuelCraft duel_craft) {
        this.duel_craft = duel_craft;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onProjectileHit(ProjectileHitEvent event) {
        Projectile projectile = event.getEntity();
        if (!(projectile instanceof Arrow)) {
            return;
        }

        Arrow arrow = (Arrow)projectile;
        ProjectileSource source = arrow.getShooter();
        if (!(source instanceof Player)) {
            return;
        }

        World world = arrow.getWorld();
        Match match = DuelWorld.getMatchFromWorld(this.duel_craft, world);
        if (match == null) {
            return;
        }

        Player shooter = (Player)source;
        ItemStack item_stack = shooter.getInventory().getItemInMainHand();
        ItemMeta item_meta = item_stack.getItemMeta();
        String display_name = item_meta.getDisplayName();
        String item_name = item_meta.getItemName();
        if (!display_name.contains("EXPLOSIVE BOW") || !item_name.contains("EXPLOSIVE BOW")) {
            return;
        }

        world.createExplosion(arrow.getLocation(), (float)(arrow.getDamage() * 35), false, false);
    }
}

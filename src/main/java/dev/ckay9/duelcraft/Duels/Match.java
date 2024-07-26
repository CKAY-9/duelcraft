package dev.ckay9.duelcraft.Duels;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import dev.ckay9.duelcraft.DuelCraft;
import dev.ckay9.duelcraft.Storage;
import dev.ckay9.duelcraft.Utils;

class EndCountdown implements Runnable {
    private DuelCraft duel_craft;
    private Match match;
    public int runnable_id = -1;
    private String[] colors = new String[] { "&a", "&2", "&6", "&c", "&4" };
    private int count = colors.length; // must be the same length of colors

    public EndCountdown(DuelCraft duel_craft, Match match) {
        this.duel_craft = duel_craft;
        this.match = match;
    }

    @Override
    public void run() {
        if (!match.hasStarted()) {
            return;
        }

        Player challenged = match.getChallenged();
        Player challenger = match.getChallenger();
        for (int i = 0; i < 3; i++) {
            challenged.sendMessage(Utils
                    .formatText(colors[colors.length - count] + "&l[DUELS] Leaving duel in " + this.count + "s..."));
            challenger.sendMessage(Utils
                    .formatText(colors[colors.length - count] + "&l[DUELS] Leaving duel in " + this.count + "s..."));
        }
        challenged.playSound(challenged, Sound.BLOCK_NOTE_BLOCK_BELL, 1, 1);
        challenger.playSound(challenger, Sound.BLOCK_NOTE_BLOCK_BELL, 1, 1);

        count--;
        if (count <= 0) {
            this.duel_craft.getServer().getScheduler().cancelTask(this.runnable_id);
            match.cleanupMatch();
        }
    }
}

class StartCountdown implements Runnable {
    private DuelCraft duel_craft;
    private Match match;
    public int runnable_id = -1;
    private String[] colors = new String[] { "&a", "&2", "&6", "&c", "&4" };
    private int count = colors.length; // must be the same length of colors

    public StartCountdown(DuelCraft duel_craft, Match match) {
        this.duel_craft = duel_craft;
        this.match = match;
    }

    @Override
    public void run() {
        if (match.hasStarted()) {
            return;
        }

        Player challenged = match.getChallenged();
        Player challenger = match.getChallenger();
        for (int i = 0; i < 3; i++) {
            challenged.sendMessage(Utils
                    .formatText(colors[colors.length - count] + "&l[DUELS] Starting duel in " + this.count + "s..."));
            challenger.sendMessage(Utils
                    .formatText(colors[colors.length - count] + "&l[DUELS] Starting duel in " + this.count + "s..."));
        }
        challenged.playSound(challenged, Sound.BLOCK_NOTE_BLOCK_BELL, 1, 1);
        challenger.playSound(challenger, Sound.BLOCK_NOTE_BLOCK_BELL, 1, 1);

        count--;
        if (count <= 0) {
            this.duel_craft.getServer().getScheduler().cancelTask(this.runnable_id);
            match.beginGame();
        }
    }
}

public class Match {
    private Player challenger;
    private Player challenged;

    private Inventory challenger_inventory;
    private Inventory challenged_inventory;
    private Location challenger_location;
    private Location challenged_location;

    private boolean created = false;
    private boolean accepted = false;
    private boolean started = false;
    private boolean ended = false;
    private int seconds_remaining = Storage.config.getInt("config.match_accept_timeout", 300);

    private DuelType duel_type = DuelType.CLASSIC;
    private DuelWorld duel_world;
    private DuelCraft duels;

    public Match(DuelCraft duels, Player challenger, Player challenged) {
        this.duels = duels;
        this.setChallenger(challenger);
        this.setChallenged(challenged);
    }

    public void notifyPlayersOfInvite() {
        this.setCreated(true);
        this.getChallenged().playSound(this.getChallenged(), Sound.BLOCK_NOTE_BLOCK_BELL, 1, 1);
        this.getChallenger().playSound(this.getChallenger(), Sound.BLOCK_NOTE_BLOCK_BELL, 1, 1);
        this.getChallenger()
                .sendMessage(Utils.formatText("&a&l[DUELS] Sent invite to " + this.getChallenged().getName() + "!"));
        
        String duel_mode = "Classic Duel";
        if (this.getDuelType() == DuelType.CLASSIC) {
            duel_mode = "Classic Duel";
        } else if (this.getDuelType() == DuelType.SPLEEF) {
            duel_mode = "Spleef Duel";
        }

        this.getChallenged()
                .sendMessage(Utils.formatText("&a&l[DUELS] You have been challenged to a " + duel_mode + " by "
                        + this.getChallenger().getName() + "! Do '/duel " + this.getChallenger().getName()
                        + "' to accept or '/duel' to see all your invites."));
    }

    public void notifyPlayersOfExpire() {
        this.getChallenged().playSound(this.getChallenged(), Sound.BLOCK_NOTE_BLOCK_BELL, 1, 1);
        this.getChallenger().playSound(this.getChallenger(), Sound.BLOCK_NOTE_BLOCK_BELL, 1, 1);
        this.getChallenger().sendMessage(
                Utils.formatText("&4&l[DUELS] Your challenge to " + this.getChallenged().getName() + " has expired."));
        this.getChallenged().sendMessage(
                Utils.formatText("&4&l[DUELS] Your invite by " + this.getChallenger().getName() + " has expired."));
    }

    public void declineChallenge() {
        this.getChallenged().sendMessage(Utils.formatText("&2&l[DUELS] You have declined the request!"));
        this.getChallenged().playSound(this.getChallenged(), Sound.BLOCK_NOTE_BLOCK_BELL, 1, 1);
        this.getChallenger().sendMessage(Utils.formatText("&2&l[DUELS] Duel request was declined!"));
        this.getChallenger().playSound(this.getChallenger(), Sound.BLOCK_NOTE_BLOCK_BELL, 1, 1);
        this.deleteMatch();
    }

    public void acceptChallenge() {
        this.setAccepted(true);
        this.prepareGame();

        StartCountdown start_count = new StartCountdown(this.duels, this);
        int id = this.duels.getServer().getScheduler().scheduleSyncRepeatingTask(this.duels, start_count, 20L, 20L);
        start_count.runnable_id = id;
    }

    private void fillInventories() {
        Player challenged = this.getChallenged();
        Player challenger = this.getChallenger();
        PlayerInventory cd_inv = challenged.getInventory();
        PlayerInventory cr_inv = challenger.getInventory();

        cd_inv.clear();
        cr_inv.clear();

        if (this.getDuelType() == DuelType.SPLEEF) {
            cd_inv.setItem(0, new ItemStack(Material.DIAMOND_SHOVEL, 1));
            cr_inv.setItem(0, new ItemStack(Material.DIAMOND_SHOVEL, 1));
            cd_inv.setItem(1, new ItemStack(Material.FISHING_ROD, 1));
            cr_inv.setItem(1, new ItemStack(Material.FISHING_ROD, 1));
            
            return;
        }

        if (this.getDuelType() == DuelType.CLASSIC) {

            // Hotbar Items
            ConfigurationSection hotbar_section = Storage.config.getConfigurationSection("config.duel.hotbar");
            Set<String> keys = hotbar_section.getKeys(false);
            int position = 0;
            for (String key : keys) {
                if (position > 9) {
                    break;
                }

                ConfigurationSection hotbar_item = hotbar_section.getConfigurationSection(key);
                String material_string = hotbar_item.getString("material");
                Material material = Material.matchMaterial(material_string);
                if (material == null) {
                    continue;
                }

                int count = hotbar_item.getInt("count");
                ItemStack stack = new ItemStack(material, count);
                cd_inv.setItem(position, stack);
                cr_inv.setItem(position, stack);
                position++;
            }

            // Off hand item
            ConfigurationSection off_hand_section = Storage.config.getConfigurationSection("config.duel.off_hand");
            String off_hand_string = off_hand_section.getString("material");
            Material off_hand_material = Material.matchMaterial(off_hand_string);
            int off_hand_count = off_hand_section.getInt("count");
            ItemStack off_hand_stack = new ItemStack(off_hand_material, off_hand_count);
            cd_inv.setItemInOffHand(off_hand_stack);
            cr_inv.setItemInOffHand(off_hand_stack);

            // Armor
            ConfigurationSection armor_section = Storage.config.getConfigurationSection("config.duel.armor");
            List<String> armor_pieces = armor_section.getStringList("pieces");

            Material helmet_material = Material.matchMaterial(armor_pieces.get(0));
            ItemStack helmet_stack = new ItemStack(helmet_material, 1);
            Material chestplate_material = Material.matchMaterial(armor_pieces.get(1));
            ItemStack chestplate_stack = new ItemStack(chestplate_material, 1);
            Material leggings_material = Material.matchMaterial(armor_pieces.get(2));
            ItemStack leggings_stack = new ItemStack(leggings_material, 1);
            Material boots_material = Material.matchMaterial(armor_pieces.get(3));
            ItemStack boots_stack = new ItemStack(boots_material, 1);

            cd_inv.setHelmet(helmet_stack);
            cd_inv.setChestplate(chestplate_stack);
            cd_inv.setLeggings(leggings_stack);
            cd_inv.setBoots(boots_stack);
            cr_inv.setHelmet(helmet_stack);
            cr_inv.setChestplate(chestplate_stack);
            cr_inv.setLeggings(leggings_stack);
            cr_inv.setBoots(boots_stack);

            return;
        }
    }

    private void prepareGame() {
        this.setChallengedInventory(this.getChallenged().getInventory());
        this.setChallengedLocation(this.getChallenged().getLocation());
        this.setChallengerInventory(this.getChallenger().getInventory());
        this.setChallengerLocation(this.getChallenger().getLocation());

        this.setDuelWorld(new DuelWorld(this.duels));
        DuelWorld world = this.getDuelWorld();
        Random random = new Random();
        long random_id = random.nextLong();
        world.setWorld(world.generateWorld(String.valueOf(random_id)));
        world.setWorldID(random_id);
        world.setCenterLocation(new Location(world.getWorld(), 0, 100, 0));
        if (this.getDuelType() == DuelType.CLASSIC) {
            world.constructClassicArena();
        }

        if (this.getDuelType() == DuelType.SPLEEF) {
            world.constructSpleefArena();
        }
    }

    public void beginGame() {
        this.setStarted(true);
        this.fillInventories();

        Player challenger = this.getChallenger();
        Player challenged = this.getChallenged();

        this.getDuelWorld().teleportPlayerToWorldSpawn(challenger);
        this.getDuelWorld().teleportPlayerToWorldSpawn(challenged);
        challenger.setGameMode(GameMode.SURVIVAL);
        challenged.setGameMode(GameMode.SURVIVAL);
        challenger.setHealth(20);
        challenged.setHealth(20);
        challenger.setSaturation(20);
        challenged.setSaturation(20);
    }

    public void endGameAndDeclareWinner(Player winner, Player loser) {
        this.setEnded(true);
        Bukkit.broadcastMessage(Utils
                .formatText("&6&l[DUELS] " + winner.getName() + " has won in a duel against " + loser.getName() + "!"));

        ConfigurationSection winner_section = Storage.data.getConfigurationSection("players." + winner.getUniqueId());
        ConfigurationSection loser_section = Storage.data.getConfigurationSection("players." + loser.getUniqueId());
        int winner_wins = winner_section.getInt("wins", 0) + 1;
        int winner_kills = winner_section.getInt("kills", 0) + 1;
        int loser_losses = loser_section.getInt("losses", 0) + 1;
        int loser_deaths = loser_section.getInt("deaths", 0) + 1;

        winner_section.set("wins", winner_wins);
        winner_section.set("kills", winner_kills);
        loser_section.set("losses", loser_losses);
        loser_section.set("deaths", loser_deaths);

        try {
            Storage.data.save(Storage.data_file);
        } catch (IOException ex) {
            this.duels.getLogger().warning(ex.toString());
        }

        EndCountdown end_count = new EndCountdown(this.duels, this);
        int id = this.duels.getServer().getScheduler().scheduleSyncRepeatingTask(this.duels, end_count, 20L, 20L);
        end_count.runnable_id = id;
    }

    public void cleanupMatch() {
        Player challenger = this.getChallenger();
        Player challenged = this.getChallenged();

        challenged.teleport(this.getChallengedLocation());
        challenged.getInventory().clear();
        this.getChallenged().getInventory().setContents(this.getChallengedInventory().getContents());
        // this.getChallenged().spigot().respawn();

        challenger.teleport(this.getChallengerLocation());
        challenger.getInventory().clear();
        challenger.getInventory().setContents(this.getChallengerInventory().getContents());
        // this.getChallenged().spigot().respawn();

        challenger.setHealth(20);
        challenged.setHealth(20);
        challenger.setSaturation(20);
        challenged.setSaturation(20);

        Bukkit.unloadWorld(this.getDuelWorld().getWorld(), false);
        this.getDuelWorld().getWorld().getWorldFolder().delete();

        this.deleteMatch();
    }

    public void deleteMatch() {
        this.duels.matches.remove(this);
    }

    public static Match createSpleefMatch(DuelCraft duel_craft, Player challenger, Player challenged) {
        Match new_match = new Match(duel_craft, challenger, challenged);
        challenger.closeInventory();          
        duel_craft.matches.add(new_match);
        new_match.setDuelType(DuelType.SPLEEF);
        new_match.notifyPlayersOfInvite();

        return new_match;
    }

    public static Match createClassicMatch(DuelCraft duel_craft, Player challenger, Player challenged) {
        Match new_match = new Match(duel_craft, challenger, challenged);
        challenger.closeInventory();          
        duel_craft.matches.add(new_match);
        new_match.setDuelType(DuelType.CLASSIC);
        new_match.notifyPlayersOfInvite();

        return new_match;
    }

    public static ArrayList<Match> getInvitedMatches(Player player, DuelCraft duel_craft) {
        ArrayList<Match> matches = new ArrayList<>();
        for (int i = 0; i < duel_craft.matches.size(); i++) {
            Match match = duel_craft.matches.get(i);
            if (match.getChallenged().getUniqueId() == player.getUniqueId()) {
                matches.add(match);
            }
        }

        return matches;
    }

    public static Match getCurrentPlayerMatch(Player player, DuelCraft duel_craft) {
        UUID uuid = player.getUniqueId();
        for (int i = 0; i < duel_craft.matches.size(); i++) {
            Match match = duel_craft.matches.get(i);
            if (match.getChallenged().getUniqueId() == uuid || match.getChallenger().getUniqueId() == uuid) {
                return match;
            }
        }
        
        return null;
    }

    public static boolean isPlayerWaiting(Player player, DuelCraft duel_craft) {
        for (int i = 0; i < duel_craft.matches.size(); i++) {
            Match match = duel_craft.matches.get(i);
            if (match.getChallenger().getUniqueId() == player.getUniqueId()) {
                return true;
            }
        }

        return false;
    }

    public static void handlePlayerAbandon(Player player, DuelCraft duel_craft) {
        Match match = Match.getCurrentPlayerMatch(player, duel_craft);
        if (match == null) {
            return;
        }

        if (!match.hasStarted()) {
            return;
        }

        Player winner = match.getChallenged();
        Player losser = match.getChallenger();
        if (player.getUniqueId() == winner.getUniqueId()) {
            winner = match.getChallenger();
            losser = match.getChallenged();
        }

        match.endGameAndDeclareWinner(winner, losser);
    }

    public boolean hasBeenCreated() {
        return this.created;
    }

    public boolean hasAccepted() {
        return this.accepted;
    }

    public boolean hasStarted() {
        return this.started;
    }

    public boolean hasEnded() {
        return this.ended;
    }

    public Player getChallenger() {
        return this.challenger;
    }

    public Player getChallenged() {
        return this.challenged;
    }

    public Inventory getChallengedInventory() {
        return this.challenged_inventory;
    }

    public Inventory getChallengerInventory() {
        return this.challenger_inventory;
    }

    public Location getChallengerLocation() {
        return this.challenger_location;
    }

    public Location getChallengedLocation() {
        return this.challenged_location;
    }

    public int getRemainingTimeInSeconds() {
        return this.seconds_remaining;
    }

    public DuelWorld getDuelWorld() {
        return this.duel_world;
    }

    public DuelType getDuelType() {
        return this.duel_type;
    }

    public void setAccepted(boolean val) {
        this.accepted = val;
    }

    public void setCreated(boolean val) {
        this.created = val;
    }

    public void setStarted(boolean val) {
        this.started = val;
    }

    public void setEnded(boolean val) {
        this.ended = val;
    }

    public void setChallenger(Player player) {
        this.challenger = player;
    }

    public void setChallenged(Player player) {
        this.challenged = player;
    }

    public void setTimeRemainingInSeconds(int seconds) {
        this.seconds_remaining = seconds;
    }

    public void setChallengerInventory(Inventory inv) {
        this.challenger_inventory = inv;
    }

    public void setChallengedInventory(Inventory inv) {
        this.challenged_inventory = inv;
    }

    public void setChallengerLocation(Location loc) {
        this.challenger_location = loc;
    }

    public void setChallengedLocation(Location loc) {
        this.challenged_location = loc;
    }

    public void setDuelWorld(DuelWorld world) {
        this.duel_world = world;
    }

    public void setDuelType(DuelType type) {
        this.duel_type = type;
    }
}

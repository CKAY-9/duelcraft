package dev.ckay9.duelcraft.Duels;

import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import dev.ckay9.duelcraft.DuelCraft;
import dev.ckay9.duelcraft.Utils;

public class Match {
    private Player challenger;
    private Player challenged;
    
    private Inventory challenger_inventory;
    private Inventory challenged_inventory;
    private Location challenger_location;
    private Location challenged_location;

    private boolean accepted = false;
    private boolean started = false;
    private boolean ended = false;
    private int seconds_remaining = 300;

    private DuelWorld duel_world;
    private DuelCraft duels;
    
    public Match(DuelCraft duels, Player challenger, Player challenged) {
        this.duels = duels;
        this.setChallenger(challenger);
        this.setChallenged(challenged);
    }

    public void notifyChallengedOfMatch() {
        this.getChallenger().sendMessage(Utils.formatText("&a&lSent invite to " + this.getChallenged().getName() + "!"));
        this.getChallenged().sendMessage(Utils.formatText("&a&lYou have been challenged to a DUEL by " + this.getChallenger().getName() + "! Do /duel to see your invites."));
    }

    public void acceptChallenge() {
        this.setAccepted(true);
        this.prepareGame();
        this.beginGame();
    }

    private void prepareGame() {
        this.setDuelWorld(new DuelWorld(this.duels));
        DuelWorld world = this.getDuelWorld();
        Random random = new Random();
        long random_id = random.nextLong();
        world.setWorld(world.generateWorld(String.valueOf(random_id)));
        world.setWorldID(random_id);
        world.setCenterLocation(new Location(world.getWorld(), 0, 100, 0));
        world.constructArena();

        this.setChallengedInventory(this.getChallenged().getInventory());
        this.setChallengedLocation(this.getChallenged().getLocation());
        this.setChallengerInventory(this.getChallenger().getInventory());
        this.setChallengerLocation(this.getChallenger().getLocation());

        this.getChallenged().getInventory().clear();
        this.getChallenger().getInventory().clear();
    }

    private void beginGame() {
        this.setStarted(true);
        this.getDuelWorld().teleportPlayerToWorldSpawn(this.getChallenged());
        this.getDuelWorld().teleportPlayerToWorldSpawn(this.getChallenger());
    }

    public void endGameAndDeclareWinner(Player winner, Player losser) {
        this.setEnded(true);

        Bukkit.broadcastMessage(Utils.formatText("&a&l" + winner.getName() + " has won in a duel against " + losser.getName() + "!"));

        this.cleanupMatch();
    }

    public void cleanupMatch() {
        int match_index = -1;
        for (int i = 0; i < this.duels.matches.size(); i++) {
            if (this == this.duels.matches.get(i)) {
                match_index = i;
                break;
            }
        }

        this.getChallenged().spigot().respawn();
        this.getChallenged().teleport(this.getChallengedLocation());
        this.getChallenged().getInventory().clear();
        this.getChallenged().getInventory().setContents(this.getChallengedInventory().getContents());

        this.getChallenger().spigot().respawn();
        this.getChallenger().teleport(this.getChallengerLocation());
        this.getChallenger().getInventory().clear();
        this.getChallenger().getInventory().setContents(this.getChallengerInventory().getContents());

        this.getDuelWorld().getWorld().getWorldFolder().delete();
        Bukkit.unloadWorld(this.getDuelWorld().getWorld(), false);

        this.duels.matches.remove(match_index);
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

    public void setAccepted(boolean val) {
        this.accepted = val;
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
}

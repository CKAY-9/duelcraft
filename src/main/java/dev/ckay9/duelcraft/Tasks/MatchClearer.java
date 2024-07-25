package dev.ckay9.duelcraft.Tasks;

import dev.ckay9.duelcraft.DuelCraft;
import dev.ckay9.duelcraft.Duels.Match;

public class MatchClearer implements Runnable {
    public int runnable_id = -1;
    private DuelCraft duel_craft;

    public MatchClearer(DuelCraft duel_craft) {
        this.duel_craft = duel_craft;
    }

    public void cancelTask() {
        this.duel_craft.getServer().getScheduler().cancelTask(this.runnable_id);
    }

    @Override
    public void run() {
        for (int i = 0; i < this.duel_craft.matches.size(); i++) {
            Match match = this.duel_craft.matches.get(i);
            if (match.hasAccepted() || match.hasStarted() || match.hasEnded()) {
                continue;
            }
            
            match.setTimeRemainingInSeconds(match.getRemainingTimeInSeconds() - 1);
            if (match.getRemainingTimeInSeconds() > 0) {
                return;
            }

            
            this.duel_craft.matches.remove(i);
        }
    }
}

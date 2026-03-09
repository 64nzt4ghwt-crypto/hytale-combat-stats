package com.howlstudio.combats;

public class PlayerStats {
    private int kills = 0;
    private int deaths = 0;
    private int killStreak = 0;
    private int bestStreak = 0;
    private String lastKilledBy = null;

    public void recordKill(String victimName) {
        kills++;
        killStreak++;
        if (killStreak > bestStreak) bestStreak = killStreak;
    }

    public void recordDeath(String killerName) {
        deaths++;
        killStreak = 0;
        lastKilledBy = killerName;
    }

    public int getKills() { return kills; }
    public int getDeaths() { return deaths; }
    public int getKillStreak() { return killStreak; }
    public int getBestStreak() { return bestStreak; }
    public String getLastKilledBy() { return lastKilledBy; }

    public double getKDR() {
        return deaths == 0 ? kills : Math.round((double) kills / deaths * 100.0) / 100.0;
    }

    public String getSummary(String playerName) {
        return "§6" + playerName + " §f| Kills: §a" + kills
                + " §f| Deaths: §c" + deaths
                + " §f| K/D: §e" + getKDR()
                + " §f| Best streak: §d" + bestStreak;
    }
}

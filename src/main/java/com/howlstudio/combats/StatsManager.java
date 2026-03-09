package com.howlstudio.combats;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class StatsManager {
    private final Map<UUID, PlayerStats> stats = new ConcurrentHashMap<>();
    private final Map<UUID, String> names = new ConcurrentHashMap<>();

    public PlayerStats getStats(UUID uuid) {
        return stats.computeIfAbsent(uuid, k -> new PlayerStats());
    }

    public void trackName(UUID uuid, String name) {
        names.put(uuid, name);
    }

    public void onJoin(UUID uuid, String name) {
        trackName(uuid, name);
        stats.computeIfAbsent(uuid, k -> new PlayerStats());
    }

    public void onLeave(UUID uuid) {
        // Keep stats in memory for leaderboard; could persist to disk here
    }

    public String getName(UUID uuid) {
        return names.getOrDefault(uuid, uuid.toString().substring(0, 8));
    }

    /** Top N players by kills. */
    public List<Map.Entry<UUID, PlayerStats>> getLeaderboard(int limit) {
        return stats.entrySet().stream()
                .sorted((a, b) -> b.getValue().getKills() - a.getValue().getKills())
                .limit(limit)
                .collect(Collectors.toList());
    }
}

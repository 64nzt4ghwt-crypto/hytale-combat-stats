package com.howlstudio.combats;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class StatsCommand extends AbstractPlayerCommand {
    private final StatsManager manager;

    public StatsCommand(StatsManager manager) {
        super("stats", "View your PvP stats. /stats [top] | /stats kill <player> | /stats die <player>");
        this.manager = manager;
    }

    @Override
    protected void execute(CommandContext ctx, Store<EntityStore> store, Ref<EntityStore> ref,
                           PlayerRef playerRef, World world) {
        UUID uuid = playerRef.getUuid();
        if (uuid == null) return;

        String input = ctx.getInputString().trim();
        String[] parts = input.split("\\s+");
        String[] args = parts.length > 1 ? Arrays.copyOfRange(parts, 1, parts.length) : new String[0];

        if (args.length == 0) {
            showStats(playerRef, uuid);
            return;
        }

        switch (args[0].toLowerCase()) {
            case "top" -> showLeaderboard(playerRef);
            // Admin/integration: /stats kill <victim> records a kill for you
            case "kill" -> {
                if (args.length < 2) { playerRef.sendMessage(Message.raw("§cUsage: /stats kill <victim_name>")); return; }
                String victim = args[1];
                manager.getStats(uuid).recordKill(victim);
                manager.trackName(uuid, playerRef.getUsername() != null ? playerRef.getUsername() : "?");
                int streak = manager.getStats(uuid).getKillStreak();
                playerRef.sendMessage(Message.raw("§6[Stats] §fKill recorded vs §c" + victim
                        + "§f. Streak: §e" + streak));
            }
            // Admin/integration: /stats die <killer> records a death for you
            case "die" -> {
                if (args.length < 2) { playerRef.sendMessage(Message.raw("§cUsage: /stats die <killer_name>")); return; }
                String killer = args[1];
                manager.getStats(uuid).recordDeath(killer);
                playerRef.sendMessage(Message.raw("§6[Stats] §fDeath recorded. Killed by §c" + killer));
            }
            default -> showStats(playerRef, uuid);
        }
    }

    private void showStats(PlayerRef ref, UUID uuid) {
        String name = ref.getUsername() != null ? ref.getUsername() : "You";
        PlayerStats s = manager.getStats(uuid);
        ref.sendMessage(Message.raw("§6[CombatStats] " + s.getSummary(name)));
        if (s.getLastKilledBy() != null) {
            ref.sendMessage(Message.raw("  §7Last killed by: §c" + s.getLastKilledBy()));
        }
        if (s.getKills() == 0 && s.getDeaths() == 0) {
            ref.sendMessage(Message.raw("  §7No combat data yet. Use §f/stats kill <name> §7to record."));
        }
    }

    private void showLeaderboard(PlayerRef ref) {
        List<Map.Entry<UUID, PlayerStats>> top = manager.getLeaderboard(10);
        ref.sendMessage(Message.raw("§6[CombatStats] §eTop 10 Players by Kills:"));
        if (top.isEmpty()) {
            ref.sendMessage(Message.raw("  §7No combat data yet."));
            return;
        }
        int rank = 1;
        for (Map.Entry<UUID, PlayerStats> entry : top) {
            String name = manager.getName(entry.getKey());
            PlayerStats s = entry.getValue();
            String medal = rank == 1 ? "§6①" : rank == 2 ? "§7②" : rank == 3 ? "§c③" : "§f" + rank + ".";
            ref.sendMessage(Message.raw("  " + medal + " §f" + name
                    + " §7— §aK:§f" + s.getKills() + " §cD:§f" + s.getDeaths()
                    + " §eKDR:§f" + s.getKDR()
                    + (s.getBestStreak() >= 3 ? " §d(best:" + s.getBestStreak() + ")" : "")));
            rank++;
        }
    }
}

package com.howlstudio.combats;

import com.hypixel.hytale.server.core.command.system.CommandManager;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;

public final class CombatStatsPlugin extends JavaPlugin {

    private StatsManager statsManager;

    public CombatStatsPlugin(JavaPluginInit init) {
        super(init);
    }

    @Override
    protected void setup() {
        System.out.println("[CombatStats] Loading...");

        statsManager = new StatsManager();

        new CombatListener(statsManager).register();
        CommandManager.get().register(new StatsCommand(statsManager));

        System.out.println("[CombatStats] Ready. /stats and /stats top available.");
    }
}

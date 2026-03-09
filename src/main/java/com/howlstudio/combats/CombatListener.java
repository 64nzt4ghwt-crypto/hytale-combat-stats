package com.howlstudio.combats;

import com.hypixel.hytale.server.core.HytaleServer;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.event.events.player.PlayerDisconnectEvent;
import com.hypixel.hytale.server.core.event.events.player.PlayerReadyEvent;
import com.hypixel.hytale.server.core.universe.PlayerRef;

import java.util.UUID;

public class CombatListener {
    private final StatsManager manager;

    public CombatListener(StatsManager manager) {
        this.manager = manager;
    }

    public void register() {
        var bus = HytaleServer.get().getEventBus();
        bus.registerGlobal(PlayerReadyEvent.class, this::onPlayerReady);
        bus.registerGlobal(PlayerDisconnectEvent.class, this::onPlayerDisconnect);
    }

    private void onPlayerReady(PlayerReadyEvent event) {
        Player player = event.getPlayer();
        if (player == null) return;
        PlayerRef ref = player.getPlayerRef();
        if (ref == null) return;
        UUID uuid = ref.getUuid();
        String name = ref.getUsername() != null ? ref.getUsername() : (uuid != null ? uuid.toString().substring(0, 8) : "Unknown");
        if (uuid != null) manager.onJoin(uuid, name);
    }

    private void onPlayerDisconnect(PlayerDisconnectEvent event) {
        PlayerRef ref = event.getPlayerRef();
        if (ref == null) return;
        UUID uuid = ref.getUuid();
        if (uuid != null) manager.onLeave(uuid);
    }
}

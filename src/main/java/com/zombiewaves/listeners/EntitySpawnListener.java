package com.zombiewaves.listeners;

import com.zombiewaves.ZombieWaves;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;

import java.util.Arrays;
import java.util.List;

public class EntitySpawnListener implements Listener {

    private final ZombieWaves plugin;
    
    private static final List<EntityType> WAVE_MOB_TYPES = Arrays.asList(
        EntityType.ZOMBIE,
        EntityType.SKELETON,
        EntityType.HUSK
    );

    public EntitySpawnListener(ZombieWaves plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        Entity entity = event.getEntity();
        
        // Only handle wave mob types
        if (!WAVE_MOB_TYPES.contains(entity.getType())) {
            return;
        }
        
        // Check if game is running and wave is in progress
        if (!plugin.getGameManager().isGameRunning()) {
            return;
        }
        
        // Check if this spawn was from our wave manager
        // (we track mobs by UUID, so we check if the mob is already in our tracked list)
        // If it's not tracked by us and game is running, it might be a natural spawn
        // We can optionally prevent natural spawns during waves
        
        // For now, we allow natural spawns but they won't be tracked as wave mobs
        // and won't give rewards
    }
}
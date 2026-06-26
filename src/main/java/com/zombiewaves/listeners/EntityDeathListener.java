package com.zombiewaves.listeners;

import com.zombiewaves.ZombieWaves;
import com.zombiewaves.utils.ConfigManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class EntityDeathListener implements Listener {

    private final ZombieWaves plugin;
    
    // List of mob types that count as zombie waves mobs
    private static final List<EntityType> WAVE_MOB_TYPES = Arrays.asList(
        EntityType.ZOMBIE,
        EntityType.SKELETON,
        EntityType.HUSK
    );

    public EntityDeathListener(ZombieWaves plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onEntityDeath(EntityDeathEvent event) {
        Entity entity = event.getEntity();
        
        // Check if this is a wave mob
        if (!WAVE_MOB_TYPES.contains(entity.getType())) {
            return;
        }
        
        // Check if the mob is tracked by our wave manager
        UUID mobId = entity.getUniqueId();
        if (!plugin.getWaveManager().getActiveMobIds().contains(mobId)) {
            // Not a tracked wave mob
            return;
        }
        
        // Get the killer
        Player killer = (entity instanceof org.bukkit.entity.LivingEntity living) ? living.getKiller() : null;
        
        if (killer != null) {
            // Add kill to player stats
            plugin.getGameManager().addKill(killer);
            
            // Calculate and add gold
            ConfigManager.MobTypeConfig mobType = getMobTypeConfig(entity.getType().name().toLowerCase());
            int goldReward = mobType.getGoldPerKill();
            
            // Apply wave bonus (extra gold based on wave number)
            int wave = plugin.getGameManager().getCurrentWave();
            goldReward += (wave * 1); // +1 gold per wave level
            
            plugin.getGameManager().addGold(killer, goldReward);
            
            // Drop gold item
            event.getDrops().clear(); // Clear default drops
            
            // You can add custom loot tables here if needed
        }
        
        // Notify wave manager that mob was killed
        plugin.getWaveManager().onMobKilled(mobId);
    }
    
    private ConfigManager.MobTypeConfig getMobTypeConfig(String type) {
        return plugin.getConfigManager().getMobTypeConfig(type);
    }
}
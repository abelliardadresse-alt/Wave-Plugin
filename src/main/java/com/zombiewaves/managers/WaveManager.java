package com.zombiewaves.managers;

import com.zombiewaves.ZombieWaves;
import com.zombiewaves.utils.ConfigManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Zombie;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Husk;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

public class WaveManager {

    private final ZombieWaves plugin;
    private final Set<UUID> activeMobs;
    private final List<BukkitTask> activeTasks;
    private int mobsToSpawn;
    private int mobsSpawned;
    private int currentWaveNumber;
    private boolean waveInProgress;
    private BukkitTask spawnTask;

    public WaveManager(ZombieWaves plugin) {
        this.plugin = plugin;
        this.activeMobs = new HashSet<>();
        this.activeTasks = new ArrayList<>();
    }

    public void startWave(int waveNumber) {
        if (waveInProgress) return;
        
        currentWaveNumber = waveNumber;
        waveInProgress = true;
        
        // Calculate total mobs for this wave
        mobsToSpawn = plugin.getConfigManager().getMobCountForWave(waveNumber);
        mobsSpawned = 0;
        
        // Broadcast wave start
        Bukkit.broadcastMessage(plugin.getConfigManager().getPrefix() + 
            plugin.getConfigManager().getMessage("wave-start")
                .replace("{wave}", String.valueOf(waveNumber)));
        
        // Start spawning mobs with delay
        startSpawning();
    }

    private void startSpawning() {
        int spawnDelay = plugin.getConfigManager().getSpawnDelay();
        int maxActive = plugin.getConfigManager().getMaxActiveMobs();
        
        spawnTask = new BukkitRunnable() {
            @Override
            public void run() {
                if (!waveInProgress || !plugin.getGameManager().isGameRunning()) {
                    cancel();
                    return;
                }
                
                // Check if we still have mobs to spawn
                if (mobsSpawned >= mobsToSpawn) {
                    cancel();
                    return;
                }
                
                // Check if we can spawn more (max active limit)
                if (activeMobs.size() >= maxActive) {
                    return;
                }
                
                // Spawn a mob
                spawnMob();
                mobsSpawned++;
                
                // Check if wave is complete
                if (mobsSpawned >= mobsToSpawn && activeMobs.isEmpty()) {
                    // All mobs spawned and killed, end wave
                    waveInProgress = false;
                    onWaveComplete();
                }
            }
        }.runTaskTimer(plugin, 20L, spawnDelay);
        
        activeTasks.add(spawnTask);
    }

    private void spawnMob() {
        // Get spawn points from arena
        List<Location> spawnPoints = getArenaSpawnPoints();
        if (spawnPoints.isEmpty()) {
            plugin.getLogger().warning("No spawn points in selected arena!");
            return;
        }
        
        Location spawnLoc = spawnPoints.get(new Random().nextInt(spawnPoints.size()));
        
        // Get random mob type
        ConfigManager.MobTypeConfig mobType = plugin.getConfigManager().getRandomMobType();
        
        // Spawn the entity
        EntityType entityType;
        try {
            entityType = EntityType.valueOf(mobType.getEntityType());
        } catch (IllegalArgumentException e) {
            plugin.getLogger().warning("Invalid entity type: " + mobType.getEntityType());
            entityType = EntityType.ZOMBIE;
        }
        
        Entity entity = spawnLoc.getWorld().spawnEntity(spawnLoc, entityType);
        
        if (entity instanceof LivingEntity livingEntity) {
            // Apply wave difficulty scaling
            applyDifficultyScaling(livingEntity, mobType);
            
            // Track the mob
            activeMobs.add(entity.getUniqueId());
        }
    }

    private List<Location> getArenaSpawnPoints() {
        String arenaName = plugin.getGameManager().getSelectedArena();
        if (arenaName == null) {
            return new ArrayList<>();
        }
        
        var arena = plugin.getArenaManager().getArena(arenaName);
        if (arena == null) {
            return new ArrayList<>();
        }
        
        return arena.getSpawnPoints();
    }

    private void applyDifficultyScaling(LivingEntity entity, ConfigManager.MobTypeConfig mobType) {
        int wave = currentWaveNumber;
        
        // Calculate scaled health
        double healthMultiplier = 1.0 + (wave * plugin.getConfigManager().getHealthMultiplier());
        double scaledHealth = mobType.getBaseHealth() * healthMultiplier;
        
        // Set max health
        if (entity.getAttribute(Attribute.GENERIC_MAX_HEALTH) != null) {
            entity.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(scaledHealth);
        }
        entity.setHealth(Math.min(scaledHealth, entity.getHealth()));
        
        // Set custom name to show health
        entity.setCustomName("§c" + entity.getType().name() + " §7[§eWave " + wave + "§7] §c" + (int) scaledHealth + " HP");
        entity.setCustomNameVisible(true);
        
        // Apply movement speed scaling (capped at 2.0x)
        double speedMultiplier = Math.min(1.0 + (wave * plugin.getConfigManager().getSpeedMultiplier()), 2.0);
        
        // Note: Speed modification requires NMS or attributes
        // For simplicity, we'll rely on the default speed
    }

    public void onMobKilled(UUID mobId) {
        activeMobs.remove(mobId);
        
        // Check if wave is complete
        if (mobsSpawned >= mobsToSpawn && activeMobs.isEmpty() && waveInProgress) {
            waveInProgress = false;
            onWaveComplete();
        }
    }

    private void onWaveComplete() {
        // Broadcast wave end
        Bukkit.broadcastMessage(plugin.getConfigManager().getPrefix() + 
            plugin.getConfigManager().getMessage("wave-end")
                .replace("{wave}", String.valueOf(currentWaveNumber)));
        
        // Start countdown to next wave
        int waveDelay = plugin.getConfigManager().getWaveDelay();
        plugin.getGameManager().startCountdown(waveDelay);
    }

    public void clearAllMobs() {
        // Cancel all tasks
        for (BukkitTask task : activeTasks) {
            task.cancel();
        }
        activeTasks.clear();
        
        if (spawnTask != null) {
            spawnTask.cancel();
            spawnTask = null;
        }
        
        // Remove all tracked mobs
        for (UUID mobId : activeMobs) {
            Entity entity = Bukkit.getEntity(mobId);
            if (entity != null && !entity.isDead()) {
                entity.remove();
            }
        }
        activeMobs.clear();
        
        waveInProgress = false;
    }

    public int getRemainingMobs() {
        return activeMobs.size() + (mobsToSpawn - mobsSpawned);
    }

    public int getActiveMobCount() {
        return activeMobs.size();
    }

    public boolean isWaveInProgress() {
        return waveInProgress;
    }

    public int getCurrentWaveNumber() {
        return currentWaveNumber;
    }

    public Set<UUID> getActiveMobIds() {
        return activeMobs;
    }
}
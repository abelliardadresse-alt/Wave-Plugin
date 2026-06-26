package com.zombiewaves.managers;

import com.zombiewaves.ZombieWaves;
import com.zombiewaves.utils.Arena;
import com.zombiewaves.utils.ConfigManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

public class WaveManager {

    private final ZombieWaves plugin;
    private final Map<String, Set<UUID>> arenaActiveMobs = new HashMap<>();
    private final Map<String, BukkitTask> arenaSpawnTasks = new HashMap<>();
    private final Map<String, Integer> arenaMobsToSpawn = new HashMap<>();
    private final Map<String, Integer> arenaMobsSpawned = new HashMap<>();
    private final Map<String, Integer> arenaWaveNumbers = new HashMap<>();
    private final Map<String, Boolean> waveInProgressMap = new HashMap<>();

    public WaveManager(ZombieWaves plugin) {
        this.plugin = plugin;
    }

    public void startWave(String arenaName, int waveNumber) {
        String key = arenaName.toLowerCase();
        if (isWaveInProgress(key)) return;
        
        arenaWaveNumbers.put(key, waveNumber);
        setWaveInProgress(key, true);
        
        // Initialize arena data
        arenaActiveMobs.computeIfAbsent(key, k -> new HashSet<>());
        
        // Get player count from arena players
        int playerCount = Math.max(1, plugin.getArenaManager().getPlayerCountInArena(arenaName));
        
        // Calculate total mobs for this wave based on player count
        int mobsToSpawn = plugin.getConfigManager().getMobCountForWave(waveNumber, playerCount);
        arenaMobsToSpawn.put(key, mobsToSpawn);
        arenaMobsSpawned.put(key, 0);
        
        // Broadcast wave start with player info
        String waveMsg = plugin.getConfigManager().getMessage("wave-start")
            .replace("{wave}", String.valueOf(waveNumber));
        broadcastToArena(arenaName, plugin.getConfigManager().getPrefix() + waveMsg);
        
        // Announce mob count
        broadcastToArena(arenaName, plugin.getConfigManager().getPrefix() + 
            "§e" + mobsToSpawn + " mobs incoming! (§7" + playerCount + " players§e)");
        
        // Start spawning mobs with delay
        startSpawning(arenaName);
    }

    private int getMobsToSpawn(String arenaName) {
        return arenaMobsToSpawn.getOrDefault(arenaName.toLowerCase(), 0);
    }

    private int getMobsSpawned(String arenaName) {
        return arenaMobsSpawned.getOrDefault(arenaName.toLowerCase(), 0);
    }

    private void incrementMobsSpawned(String arenaName) {
        String key = arenaName.toLowerCase();
        arenaMobsSpawned.merge(key, 1, Integer::sum);
    }

    private boolean isWaveInProgress(String arenaName) {
        return waveInProgressMap.getOrDefault(arenaName.toLowerCase(), false);
    }

    private void setWaveInProgress(String arenaName, boolean value) {
        waveInProgressMap.put(arenaName.toLowerCase(), value);
    }

    private void startSpawning(String arenaName) {
        int spawnDelay = plugin.getConfigManager().getSpawnDelay();
        int maxActive = plugin.getConfigManager().getMaxActiveMobs();
        String key = arenaName.toLowerCase();
        
        BukkitTask task = new BukkitRunnable() {
            @Override
            public void run() {
                if (!isWaveInProgress(key) || !plugin.getGameManager().isGameInProgress(key)) {
                    cancel();
                    return;
                }
                
                // Check if we still have mobs to spawn
                if (getMobsSpawned(key) >= getMobsToSpawn(key)) {
                    cancel();
                    return;
                }
                
                // Check if we can spawn more (max active limit)
                if (getActiveMobCount(key) >= maxActive) {
                    return;
                }
                
                // Spawn a mob
                spawnMob(arenaName, key);
                incrementMobsSpawned(key);
                
                // Check if wave is complete
                if (getMobsSpawned(key) >= getMobsToSpawn(key) && getActiveMobCount(key) == 0) {
                    // All mobs spawned and killed, end wave
                    setWaveInProgress(key, false);
                    onWaveComplete(arenaName, key);
                    cancel();
                }
            }
        }.runTaskTimer(plugin, 20L, spawnDelay);
        
        arenaSpawnTasks.put(key, task);
    }

    private void spawnMob(String arenaName, String key) {
        // Get spawn points from arena
        Arena arena = plugin.getArenaManager().getArena(arenaName);
        if (arena == null || arena.getSpawnPoints().isEmpty()) {
            plugin.getLogger().warning("No spawn points in arena: " + arenaName);
            return;
        }
        
        List<Location> spawnPoints = arena.getSpawnPoints();
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
            int wave = arenaWaveNumbers.getOrDefault(key, 1);
            applyDifficultyScaling(livingEntity, mobType, wave);
            
            // Track the mob
            arenaActiveMobs.computeIfAbsent(key, k -> new HashSet<>()).add(entity.getUniqueId());
        }
    }

    private void applyDifficultyScaling(LivingEntity entity, ConfigManager.MobTypeConfig mobType, int wave) {
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
    }

    public void onMobKilled(String arenaName, UUID mobId) {
        String key = arenaName.toLowerCase();
        Set<UUID> mobs = arenaActiveMobs.get(key);
        if (mobs != null) {
            mobs.remove(mobId);
        }
        
        // Check if wave is complete
        if (mobs != null && getMobsSpawned(key) >= getMobsToSpawn(key) && mobs.isEmpty() && isWaveInProgress(key)) {
            setWaveInProgress(key, false);
            onWaveComplete(arenaName, key);
        }
    }

    private void onWaveComplete(String arenaName, String key) {
        int wave = arenaWaveNumbers.getOrDefault(key, 1);
        
        // Broadcast wave end
        broadcastToArena(arenaName, plugin.getConfigManager().getPrefix() + 
            plugin.getConfigManager().getMessage("wave-end")
                .replace("{wave}", String.valueOf(wave)));
        
        // Start countdown to next wave
        int waveDelay = plugin.getConfigManager().getWaveDelay();
        plugin.getGameManager().startCountdown(arenaName, waveDelay);
    }

    public void clearAllMobs() {
        // Cancel all spawn tasks
        for (BukkitTask task : arenaSpawnTasks.values()) {
            task.cancel();
        }
        arenaSpawnTasks.clear();
        
        // Remove all tracked mobs
        for (Set<UUID> mobs : arenaActiveMobs.values()) {
            for (UUID mobId : mobs) {
                Entity entity = Bukkit.getEntity(mobId);
                if (entity != null && !entity.isDead()) {
                    entity.remove();
                }
            }
        }
        arenaActiveMobs.clear();
        
        // Reset wave states
        for (String key : waveInProgressMap.keySet()) {
            waveInProgressMap.put(key, false);
        }
    }

    public void clearArenaMobs(String arenaName) {
        String key = arenaName.toLowerCase();
        
        // Cancel spawn task
        BukkitTask task = arenaSpawnTasks.remove(key);
        if (task != null) task.cancel();
        
        // Remove mobs
        Set<UUID> mobs = arenaActiveMobs.remove(key);
        if (mobs != null) {
            for (UUID mobId : mobs) {
                Entity entity = Bukkit.getEntity(mobId);
                if (entity != null && !entity.isDead()) {
                    entity.remove();
                }
            }
        }
        
        // Reset state
        waveInProgressMap.remove(key);
        arenaWaveNumbers.remove(key);
        arenaMobsToSpawn.remove(key);
        arenaMobsSpawned.remove(key);
    }

    public int getRemainingMobs(String arenaName) {
        String key = arenaName.toLowerCase();
        Set<UUID> mobs = arenaActiveMobs.get(key);
        int active = mobs != null ? mobs.size() : 0;
        return active + (getMobsToSpawn(key) - getMobsSpawned(key));
    }

    public int getActiveMobCount(String arenaName) {
        String key = arenaName.toLowerCase();
        Set<UUID> mobs = arenaActiveMobs.get(key);
        return mobs != null ? mobs.size() : 0;
    }

    public int getCurrentWaveNumber(String arenaName) {
        return arenaWaveNumbers.getOrDefault(arenaName.toLowerCase(), 0);
    }

    public Set<UUID> getActiveMobIds(String arenaName) {
        String key = arenaName.toLowerCase();
        return arenaActiveMobs.getOrDefault(key, new HashSet<>());
    }

    private void broadcastToArena(String arenaName, String message) {
        // Broadcast to players in this arena via LobbyManager
        plugin.getLobbyManager().broadcastToArena(arenaName, message);
    }
}
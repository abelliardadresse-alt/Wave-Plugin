package com.zombiewaves.managers;

import com.zombiewaves.ZombieWaves;
import com.zombiewaves.utils.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class GameManager {

    private final ZombieWaves plugin;
    private final Set<String> activeArenas = new HashSet<>();
    private final Map<String, Integer> arenaWaves = new HashMap<>();
    private final Map<UUID, PlayerData> playerDataMap = new HashMap<>();
    private BukkitRunnable countdownTask;
    private int countdownSeconds;

    public GameManager(ZombieWaves plugin) {
        this.plugin = plugin;
    }

    public void reload() {
        // Reset game state
        activeArenas.clear();
        arenaWaves.clear();
    }

    public boolean isGameRunning() {
        return !activeArenas.isEmpty();
    }

    public boolean isGameInProgress(String arenaName) {
        return activeArenas.contains(arenaName.toLowerCase());
    }

    public Set<String> getActiveArenas() {
        return new HashSet<>(activeArenas);
    }

    public String getArenaForPlayer(UUID playerUUID) {
        // Find which arena a player is in
        return null;
    }

    public void startGame(String arenaName) {
        if (isGameInProgress(arenaName)) return;
        
        activeArenas.add(arenaName.toLowerCase());
        arenaWaves.put(arenaName.toLowerCase(), 0);
        
        // Broadcast start message to arena players
        broadcastToArena(arenaName, plugin.getConfigManager().getPrefix() + 
            plugin.getConfigManager().getMessage("game-start"));
        
        // Start first wave after grace period
        int gracePeriod = plugin.getConfigManager().getGracePeriod();
        startCountdown(arenaName, gracePeriod);
    }

    public void stopGame(String arenaName) {
        String key = arenaName.toLowerCase();
        activeArenas.remove(key);
        arenaWaves.remove(key);
        
        if (countdownTask != null) {
            countdownTask.cancel();
            countdownTask = null;
        }
        
        // Clear arena mobs
        plugin.getWaveManager().clearArenaMobs(arenaName);
    }

    public void stopAllGames() {
        for (String arenaName : new HashSet<>(activeArenas)) {
            stopGame(arenaName);
        }
    }

    private void broadcastToArena(String arenaName, String message) {
        // Broadcast to players in this arena
    }

    public void startCountdown(String arenaName, int seconds) {
        countdownSeconds = seconds;
        
        countdownTask = new BukkitRunnable() {
            @Override
            public void run() {
                if (!isGameInProgress(arenaName)) {
                    cancel();
                    return;
                }
                
                countdownSeconds--;
                
                if (countdownSeconds <= 0) {
                    cancel();
                    nextWave(arenaName);
                }
            }
        };
        countdownTask.runTaskTimer(plugin, 20L, 20L);
    }

    public void nextWave(String arenaName) {
        if (!isGameInProgress(arenaName)) return;
        
        String key = arenaName.toLowerCase();
        int currentWave = arenaWaves.get(key);
        currentWave++;
        arenaWaves.put(key, currentWave);
        
        // Check if we've completed all waves
        if (currentWave > plugin.getConfigManager().getTotalWaves()) {
            endGame(arenaName);
            return;
        }
        
        // Start the wave
        plugin.getWaveManager().startWave(arenaName, currentWave);
    }

    private void endGame(String arenaName) {
        int maxWave = plugin.getConfigManager().getTotalWaves();
        broadcastToArena(arenaName, plugin.getConfigManager().getPrefix() + 
            plugin.getConfigManager().getMessage("game-over")
                .replace("{wave}", String.valueOf(maxWave)));
        
        stopGame(arenaName);
    }

    public void onPlayerJoin(Player player) {
        if (!playerDataMap.containsKey(player.getUniqueId())) {
            playerDataMap.put(player.getUniqueId(), new PlayerData());
        }
    }

    public void onPlayerQuit(Player player) {
        // Keep player data for when they rejoin
    }

    public void addKill(Player player) {
        PlayerData data = playerDataMap.get(player.getUniqueId());
        if (data != null) {
            data.addKill();
        }
    }

    public void addGold(Player player, int amount) {
        PlayerData data = playerDataMap.get(player.getUniqueId());
        if (data != null) {
            data.addGold(amount);
        }
    }

    public boolean removeGold(Player player, int amount) {
        PlayerData data = playerDataMap.get(player.getUniqueId());
        if (data != null && data.getGold() >= amount) {
            data.removeGold(amount);
            return true;
        }
        return false;
    }

    public int getPlayerGold(Player player) {
        PlayerData data = playerDataMap.get(player.getUniqueId());
        return data != null ? data.getGold() : 0;
    }

    public int getPlayerKills(Player player) {
        PlayerData data = playerDataMap.get(player.getUniqueId());
        return data != null ? data.getKills() : 0;
    }

    public int getCurrentWave(String arenaName) {
        String key = arenaName.toLowerCase();
        return arenaWaves.getOrDefault(key, 0);
    }

    public int getMaxWave() {
        return plugin.getConfigManager().getTotalWaves();
    }

    public int getRemainingMobs() {
        int total = 0;
        for (String arena : activeArenas) {
            total += plugin.getWaveManager().getRemainingMobs(arena);
        }
        return total;
    }

    public int getCountdownSeconds() {
        return countdownSeconds;
    }

    public PlayerData getPlayerData(Player player) {
        return playerDataMap.get(player.getUniqueId());
    }

    public Map<UUID, PlayerData> getAllPlayerData() {
        return playerDataMap;
    }
}
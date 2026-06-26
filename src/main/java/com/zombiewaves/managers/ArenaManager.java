package com.zombiewaves.managers;

import com.zombiewaves.ZombieWaves;
import com.zombiewaves.utils.Arena;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class ArenaManager {

    private final ZombieWaves plugin;
    private final Map<String, Arena> arenas;
    private final Map<UUID, Location> playerPos1;
    private final Map<UUID, Location> playerPos2;
    private final File arenasFile;

    public ArenaManager(ZombieWaves plugin) {
        this.plugin = plugin;
        this.arenas = new HashMap<>();
        this.playerPos1 = new HashMap<>();
        this.playerPos2 = new HashMap<>();
        this.arenasFile = new File(plugin.getDataFolder(), "arenas.yml");
        loadArenas();
    }

    public void loadArenas() {
        arenas.clear();
        
        if (!arenasFile.exists()) {
            plugin.saveResource("arenas.yml", false);
        }
        
        FileConfiguration config = YamlConfiguration.loadConfiguration(arenasFile);
        
        if (config.contains("arenas")) {
            for (String name : config.getConfigurationSection("arenas").getKeys(false)) {
                Arena arena = config.getSerializable("arenas." + name, Arena.class);
                if (arena != null) {
                    arenas.put(name.toLowerCase(), arena);
                }
            }
        }
        
        plugin.getLogger().info("Loaded " + arenas.size() + " arenas.");
    }

    public void saveArenas() {
        FileConfiguration config = YamlConfiguration.loadConfiguration(arenasFile);
        config.set("arenas", null);
        
        for (Map.Entry<String, Arena> entry : arenas.entrySet()) {
            config.set("arenas." + entry.getKey(), entry.getValue());
        }
        
        try {
            config.save(arenasFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to save arenas: " + e.getMessage());
        }
    }

    public Arena createArena(String name) {
        String key = name.toLowerCase();
        if (arenas.containsKey(key)) {
            return arenas.get(key);
        }
        
        Arena arena = new Arena(name);
        arenas.put(key, arena);
        saveArenas();
        return arena;
    }

    public boolean deleteArena(String name) {
        String key = name.toLowerCase();
        if (arenas.remove(key) != null) {
            saveArenas();
            return true;
        }
        return false;
    }

    public Arena getArena(String name) {
        return arenas.get(name.toLowerCase());
    }

    public Arena getActiveArena() {
        for (Arena arena : arenas.values()) {
            if (arena.isActive()) {
                return arena;
            }
        }
        return null;
    }

    public void setActiveArena(String name) {
        // Deactivate all arenas
        for (Arena arena : arenas.values()) {
            arena.setActive(false);
        }
        
        // Activate the specified arena
        Arena arena = getArena(name);
        if (arena != null) {
            arena.setActive(true);
        }
        
        saveArenas();
    }

    public Collection<Arena> getAllArenas() {
        return arenas.values();
    }

    public boolean arenaExists(String name) {
        return arenas.containsKey(name.toLowerCase());
    }

    // Position selection for players
    public void setPlayerPos1(UUID playerId, Location location) {
        playerPos1.put(playerId, location);
    }

    public void setPlayerPos2(UUID playerId, Location location) {
        playerPos2.put(playerId, location);
    }

    public Location getPlayerPos1(UUID playerId) {
        return playerPos1.get(playerId);
    }

    public Location getPlayerPos2(UUID playerId) {
        return playerPos2.get(playerId);
    }

    public void clearPlayerPositions(UUID playerId) {
        playerPos1.remove(playerId);
        playerPos2.remove(playerId);
    }

    // Arena editing
    public void setArenaPos1(String arenaName, Location location) {
        Arena arena = getArena(arenaName);
        if (arena != null) {
            arena.setPos1(location);
            saveArenas();
        }
    }

    public void setArenaPos2(String arenaName, Location location) {
        Arena arena = getArena(arenaName);
        if (arena != null) {
            arena.setPos2(location);
            saveArenas();
        }
    }

    public void addArenaSpawnPoint(String arenaName, Location location) {
        Arena arena = getArena(arenaName);
        if (arena != null) {
            arena.addSpawnPoint(location);
            saveArenas();
        }
    }

    public void removeArenaSpawnPoint(String arenaName, Location location) {
        Arena arena = getArena(arenaName);
        if (arena != null) {
            arena.removeSpawnPoint(location);
            saveArenas();
        }
    }
}
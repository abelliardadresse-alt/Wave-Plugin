package com.zombiewaves;

import com.zombiewaves.commands.WaveCommand;
import com.zombiewaves.commands.WaveAdminCommand;
import com.zombiewaves.listeners.EntityDeathListener;
import com.zombiewaves.listeners.EntitySpawnListener;
import com.zombiewaves.listeners.PlayerJoinListener;
import com.zombiewaves.listeners.PlayerDeathListener;
import com.zombiewaves.listeners.PlayerInteractListener;
import com.zombiewaves.managers.GameManager;
import com.zombiewaves.managers.ShopManager;
import com.zombiewaves.managers.ScoreboardManager;
import com.zombiewaves.managers.WaveManager;
import com.zombiewaves.utils.ConfigManager;
import org.bukkit.plugin.java.JavaPlugin;

public class ZombieWaves extends JavaPlugin {

    private static ZombieWaves instance;
    private ConfigManager configManager;
    private GameManager gameManager;
    private WaveManager waveManager;
    private ShopManager shopManager;
    private ScoreboardManager scoreboardManager;

    @Override
    public void onEnable() {
        instance = this;
        
        // Save default config
        saveDefaultConfig();
        
        // Initialize config manager
        configManager = new ConfigManager(this);
        
        // Initialize managers
        gameManager = new GameManager(this);
        waveManager = new WaveManager(this);
        shopManager = new ShopManager(this);
        scoreboardManager = new ScoreboardManager(this);
        
        // Register commands
        getCommand("zwave").setExecutor(new WaveCommand(this));
        getCommand("zwaveadmin").setExecutor(new WaveAdminCommand(this));
        
        // Register listeners
        getServer().getPluginManager().registerEvents(new EntityDeathListener(this), this);
        getServer().getPluginManager().registerEvents(new EntitySpawnListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerDeathListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerInteractListener(this), this);
        
        getLogger().info("ZombieWaves has been enabled!");
    }

    @Override
    public void onDisable() {
        // Stop any running game
        if (gameManager != null && gameManager.isGameRunning()) {
            gameManager.stopGame();
        }
        
        getLogger().info("ZombieWaves has been disabled!");
    }

    public void reloadPlugin() {
        reloadConfig();
        configManager.reload();
        if (gameManager != null) {
            gameManager.reload();
        }
    }

    public static ZombieWaves getInstance() {
        return instance;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public GameManager getGameManager() {
        return gameManager;
    }

    public WaveManager getWaveManager() {
        return waveManager;
    }

    public ShopManager getShopManager() {
        return shopManager;
    }

    public ScoreboardManager getScoreboardManager() {
        return scoreboardManager;
    }
}
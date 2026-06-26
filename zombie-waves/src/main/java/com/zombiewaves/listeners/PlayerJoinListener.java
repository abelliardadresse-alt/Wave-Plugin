package com.zombiewaves.listeners;

import com.zombiewaves.ZombieWaves;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {

    private final ZombieWaves plugin;

    public PlayerJoinListener(ZombieWaves plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        
        // Initialize player data
        plugin.getGameManager().onPlayerJoin(player);
        
        // Create scoreboard if game is running
        if (plugin.getGameManager().isGameRunning()) {
            plugin.getScoreboardManager().onPlayerJoin(player);
        }
    }
}
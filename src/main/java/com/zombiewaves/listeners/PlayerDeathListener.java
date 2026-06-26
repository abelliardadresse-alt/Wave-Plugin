package com.zombiewaves.listeners;

import com.zombiewaves.ZombieWaves;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class PlayerDeathListener implements Listener {

    private final ZombieWaves plugin;

    public PlayerDeathListener(ZombieWaves plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        
        // Handle player death during game
        // You could implement respawn system, lose gold, etc.
        
        // For now, just a simple message
        // Could add: respawn timer, death penalty, etc.
    }
}
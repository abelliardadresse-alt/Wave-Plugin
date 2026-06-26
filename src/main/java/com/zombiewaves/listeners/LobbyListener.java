package com.zombiewaves.listeners;

import com.zombiewaves.ZombieWaves;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class LobbyListener implements Listener {

    private final ZombieWaves plugin;

    public LobbyListener(ZombieWaves plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        Player player = event.getPlayer();
        
        // Check if player is in lobby
        if (!plugin.getLobbyManager().isInArena(player)) {
            return;
        }

        ItemStack item = event.getItem();
        if (item == null || item.getType() != Material.DIAMOND) {
            return;
        }

        // Check if it's our force start diamond
        if (!item.hasItemMeta() || !item.getItemMeta().hasDisplayName()) {
            return;
        }

        String displayName = item.getItemMeta().getDisplayName();
        if (!displayName.contains("FORCE START")) {
            return;
        }

        // Check permission
        if (!player.hasPermission("zombiewaves.admin")) {
            player.sendMessage(plugin.getConfigManager().getPrefix() + 
                "§cYou don't have permission to force start!");
            return;
        }

        // Force start the game
        String arenaName = plugin.getLobbyManager().getPlayerArenaName(player);
        if (arenaName != null) {
            // Cancel existing countdown
            plugin.getLobbyManager().stopArenaCountdownForAdmin(arenaName);
            
            // Broadcast
            Bukkit.broadcastMessage(plugin.getConfigManager().getPrefix() + 
                "§6§l" + player.getName() + " §eforced the game to start!");
            
            // Start game immediately
            plugin.getLobbyManager().startGameNow(arenaName);
        }

        event.setCancelled(true);
    }
}

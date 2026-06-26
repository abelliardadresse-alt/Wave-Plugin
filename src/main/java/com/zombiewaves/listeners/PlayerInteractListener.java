package com.zombiewaves.listeners;

import com.zombiewaves.ZombieWaves;
import com.zombiewaves.managers.ShopManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class PlayerInteractListener implements Listener {

    private final ZombieWaves plugin;

    public PlayerInteractListener(ZombieWaves plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Action action = event.getAction();
        
        // Open shop with right-click on a specific item (e.g., compass)
        if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
            ItemStack item = player.getInventory().getItemInMainHand();
            
            // Check if player is holding a compass to open shop
            if (item != null && item.getType() == Material.COMPASS) {
                event.setCancelled(true);
                plugin.getShopManager().openShop(player);
            }
        }
    }
}
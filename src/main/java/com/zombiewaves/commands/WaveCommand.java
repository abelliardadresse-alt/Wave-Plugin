package com.zombiewaves.commands;

import com.zombiewaves.ZombieWaves;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class WaveCommand implements CommandExecutor, TabCompleter {

    private final ZombieWaves plugin;

    public WaveCommand(ZombieWaves plugin) {
        this.plugin = plugin;
        plugin.getCommand("zwave").setTabCompleter(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sendHelp(sender);
            return true;
        }

        String subCommand = args[0].toLowerCase();

        switch (subCommand) {
            case "start" -> {
                if (!sender.hasPermission("zombiewaves.start")) {
                    sender.sendMessage(plugin.getConfigManager().getPrefix() + 
                        plugin.getConfigManager().getMessage("no-permission"));
                    return true;
                }
                if (plugin.getGameManager().isGameRunning()) {
                    sender.sendMessage(plugin.getConfigManager().getPrefix() + 
                        "§cGame is already running!");
                    return true;
                }
                plugin.getGameManager().startGame();
                plugin.getScoreboardManager().onGameStart();
                sender.sendMessage(plugin.getConfigManager().getPrefix() + "§aGame started!");
            }
            case "stop" -> {
                if (!sender.hasPermission("zombiewaves.stop")) {
                    sender.sendMessage(plugin.getConfigManager().getPrefix() + 
                        plugin.getConfigManager().getMessage("no-permission"));
                    return true;
                }
                if (!plugin.getGameManager().isGameRunning()) {
                    sender.sendMessage(plugin.getConfigManager().getPrefix() + 
                        plugin.getConfigManager().getMessage("no-wave-running"));
                    return true;
                }
                plugin.getGameManager().stopGame();
                plugin.getScoreboardManager().onGameEnd();
                sender.sendMessage(plugin.getConfigManager().getPrefix() + "§cGame stopped!");
            }
            case "status" -> {
                if (!plugin.getGameManager().isGameRunning()) {
                    sender.sendMessage(plugin.getConfigManager().getPrefix() + 
                        plugin.getConfigManager().getMessage("no-wave-running"));
                    return true;
                }
                sendStatus(sender);
            }
            case "shop" -> {
                if (!(sender instanceof Player player)) {
                    sender.sendMessage(plugin.getConfigManager().getPrefix() + 
                        "§cThis command can only be used by players!");
                    return true;
                }
                if (!sender.hasPermission("zombiewaves.shop")) {
                    sender.sendMessage(plugin.getConfigManager().getPrefix() + 
                        plugin.getConfigManager().getMessage("no-permission"));
                    return true;
                }
                plugin.getShopManager().openShop(player);
            }
            case "gold" -> {
                if (!(sender instanceof Player player)) {
                    sender.sendMessage(plugin.getConfigManager().getPrefix() + 
                        "§cThis command can only be used by players!");
                    return true;
                }
                int gold = plugin.getGameManager().getPlayerGold(player);
                sender.sendMessage(plugin.getConfigManager().getPrefix() + 
                    "§6Your gold: §e" + gold);
            }
            default -> sendHelp(sender);
        }

        return true;
    }

    private void sendHelp(CommandSender sender) {
        sender.sendMessage(plugin.getConfigManager().getPrefix() + "§eZombie Waves Commands:");
        sender.sendMessage("§e/zwave start §7- Start the game");
        sender.sendMessage("§e/zwave stop §7- Stop the game");
        sender.sendMessage("§e/zwave status §7- Show game status");
        sender.sendMessage("§e/zwave shop §7- Open the shop");
        sender.sendMessage("§e/zwave gold §7- Check your gold");
    }

    private void sendStatus(CommandSender sender) {
        sender.sendMessage("§6§l=== Zombie Waves Status ===");
        sender.sendMessage("§eWave: §f" + plugin.getGameManager().getCurrentWave() + 
            "§e/§f" + plugin.getGameManager().getMaxWave());
        sender.sendMessage("§eMobs remaining: §f" + plugin.getGameManager().getRemainingMobs());
        sender.sendMessage("§eNext wave in: §f" + plugin.getGameManager().getCountdownSeconds() + "s");
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        
        if (args.length == 1) {
            if (sender.hasPermission("zombiewaves.start")) {
                completions.add("start");
            }
            if (sender.hasPermission("zombiewaves.stop")) {
                completions.add("stop");
            }
            completions.add("status");
            completions.add("shop");
            completions.add("gold");
            
            return completions.stream()
                .filter(s -> s.toLowerCase().startsWith(args[0].toLowerCase()))
                .toList();
        }
        
        return completions;
    }
}
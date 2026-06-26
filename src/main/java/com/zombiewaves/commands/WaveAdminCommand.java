package com.zombiewaves.commands;

import com.zombiewaves.ZombieWaves;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;

public class WaveAdminCommand implements CommandExecutor, TabCompleter {

    private final ZombieWaves plugin;

    public WaveAdminCommand(ZombieWaves plugin) {
        this.plugin = plugin;
        plugin.getCommand("zwaveadmin").setTabCompleter(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("zombiewaves.admin")) {
            sender.sendMessage(plugin.getConfigManager().getPrefix() + 
                plugin.getConfigManager().getMessage("no-permission"));
            return true;
        }

        if (args.length == 0) {
            sendHelp(sender);
            return true;
        }

        String subCommand = args[0].toLowerCase();

        switch (subCommand) {
            case "reload" -> {
                plugin.reloadPlugin();
                sender.sendMessage(plugin.getConfigManager().getPrefix() + "§aConfiguration reloaded!");
            }
            case "setwave" -> {
                if (args.length < 2) {
                    sender.sendMessage(plugin.getConfigManager().getPrefix() + 
                        "§cUsage: /zwaveadmin setwave <number>");
                    return true;
                }
                try {
                    int wave = Integer.parseInt(args[1]);
                    sender.sendMessage(plugin.getConfigManager().getPrefix() + 
                        "§eWave is managed by the game system.");
                    sender.sendMessage(plugin.getConfigManager().getPrefix() + 
                        "§eCurrent wave: " + plugin.getGameManager().getCurrentWave());
                } catch (NumberFormatException e) {
                    sender.sendMessage(plugin.getConfigManager().getPrefix() + 
                        "§cInvalid wave number!");
                }
            }
            case "addspawn" -> {
                if (args.length < 2) {
                    sender.sendMessage(plugin.getConfigManager().getPrefix() + 
                        "§cUsage: /zwaveadmin addspawn <mapname>");
                    return true;
                }
                sender.sendMessage(plugin.getConfigManager().getPrefix() + 
                    "§eTo add spawn points, edit the config.yml file.");
            }
            case "forcewave" -> {
                if (!plugin.getGameManager().isGameRunning()) {
                    sender.sendMessage(plugin.getConfigManager().getPrefix() + 
                        "§cNo game is running!");
                    return true;
                }
                plugin.getWaveManager().clearAllMobs();
                plugin.getGameManager().nextWave();
                sender.sendMessage(plugin.getConfigManager().getPrefix() + 
                    "§aForce starting next wave!");
            }
            default -> sendHelp(sender);
        }

        return true;
    }

    private void sendHelp(CommandSender sender) {
        sender.sendMessage(plugin.getConfigManager().getPrefix() + "§eAdmin Commands:");
        sender.sendMessage("§e/zwaveadmin reload §7- Reload configuration");
        sender.sendMessage("§e/zwaveadmin setwave <n> §7- Set current wave");
        sender.sendMessage("§e/zwaveadmin addspawn <map> §7- Add spawn point");
        sender.sendMessage("§e/zwaveadmin forcewave §7- Force next wave");
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        
        if (args.length == 1) {
            completions.add("reload");
            completions.add("setwave");
            completions.add("addspawn");
            completions.add("forcewave");
            
            return completions.stream()
                .filter(s -> s.toLowerCase().startsWith(args[0].toLowerCase()))
                .toList();
        }
        
        return completions;
    }
}
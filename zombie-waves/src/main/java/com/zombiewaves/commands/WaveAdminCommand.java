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
                if (args.length < 3) {
                    sender.sendMessage(plugin.getConfigManager().getPrefix() + 
                        "§cUsage: /zwaveadmin setwave <arena> <number>");
                    return true;
                }
                String arenaName = args[1].toLowerCase();
                if (!plugin.getGameManager().isGameInProgress(arenaName)) {
                    sender.sendMessage(plugin.getConfigManager().getPrefix() + 
                        "§cNo game running in arena '" + arenaName + "'!");
                    return true;
                }
                try {
                    sender.sendMessage(plugin.getConfigManager().getPrefix() + 
                        "§eCurrent wave: " + plugin.getGameManager().getCurrentWave(arenaName));
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
                if (args.length < 2) {
                    sender.sendMessage(plugin.getConfigManager().getPrefix() + 
                        "§cUsage: /zwaveadmin forcewave <arena>");
                    return true;
                }
                String arenaName = args[1].toLowerCase();
                if (!plugin.getGameManager().isGameInProgress(arenaName)) {
                    sender.sendMessage(plugin.getConfigManager().getPrefix() + 
                        "§cNo game running in arena '" + arenaName + "'!");
                    return true;
                }
                plugin.getWaveManager().clearArenaMobs(arenaName);
                plugin.getGameManager().nextWave(arenaName);
                sender.sendMessage(plugin.getConfigManager().getPrefix() + 
                    "§aForce starting next wave in arena '" + arenaName + "'!");
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
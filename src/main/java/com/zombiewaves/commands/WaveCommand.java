package com.zombiewaves.commands;

import com.zombiewaves.ZombieWaves;
import com.zombiewaves.utils.Arena;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
            // Arena commands
            case "setpos1" -> handleSetPos1(sender, args);
            case "setpos2" -> handleSetPos2(sender, args);
            case "addspawn" -> handleAddSpawn(sender, args);
            case "removespawn" -> handleRemoveSpawn(sender, args);
            case "createarena" -> handleCreateArena(sender, args);
            case "deletearena" -> handleDeleteArena(sender, args);
            case "arenas" -> handleListArenas(sender);
            case "selectarena" -> handleSelectArena(sender, args);
            case "infoarena" -> handleInfoArena(sender, args);
            default -> sendHelp(sender);
        }

        return true;
    }

    private void handleSetPos1(CommandSender sender, String[] args) {
        if (!sender.hasPermission("zombiewaves.admin")) {
            sender.sendMessage(plugin.getConfigManager().getPrefix() + 
                plugin.getConfigManager().getMessage("no-permission"));
            return;
        }
        if (!(sender instanceof Player player)) {
            sender.sendMessage(plugin.getConfigManager().getPrefix() + 
                "§cThis command can only be used by players!");
            return;
        }
        if (args.length < 2) {
            sender.sendMessage(plugin.getConfigManager().getPrefix() + 
                "§cUsage: /zwave setpos1 <arenaName>");
            return;
        }
        
        String arenaName = args[1].toLowerCase();
        Arena arena = plugin.getArenaManager().getArena(arenaName);
        if (arena == null) {
            sender.sendMessage(plugin.getConfigManager().getPrefix() + 
                "§cArena '" + arenaName + "' does not exist!");
            return;
        }
        
        Location target = getTargetBlock(player);
        if (target == null) {
            sender.sendMessage(plugin.getConfigManager().getPrefix() + 
                "§cCannot find target block! Look at a block.");
            return;
        }
        
        plugin.getArenaManager().setArenaPos1(arenaName, target);
        sender.sendMessage(plugin.getConfigManager().getPrefix() + 
            "§aPosition 1 set for arena '" + arena.getName() + "' at " + 
            formatLocation(target));
    }

    private void handleSetPos2(CommandSender sender, String[] args) {
        if (!sender.hasPermission("zombiewaves.admin")) {
            sender.sendMessage(plugin.getConfigManager().getPrefix() + 
                plugin.getConfigManager().getMessage("no-permission"));
            return;
        }
        if (!(sender instanceof Player player)) {
            sender.sendMessage(plugin.getConfigManager().getPrefix() + 
                "§cThis command can only be used by players!");
            return;
        }
        if (args.length < 2) {
            sender.sendMessage(plugin.getConfigManager().getPrefix() + 
                "§cUsage: /zwave setpos2 <arenaName>");
            return;
        }
        
        String arenaName = args[1].toLowerCase();
        Arena arena = plugin.getArenaManager().getArena(arenaName);
        if (arena == null) {
            sender.sendMessage(plugin.getConfigManager().getPrefix() + 
                "§cArena '" + arenaName + "' does not exist!");
            return;
        }
        
        Location target = getTargetBlock(player);
        if (target == null) {
            sender.sendMessage(plugin.getConfigManager().getPrefix() + 
                "§cCannot find target block! Look at a block.");
            return;
        }
        
        plugin.getArenaManager().setArenaPos2(arenaName, target);
        sender.sendMessage(plugin.getConfigManager().getPrefix() + 
            "§aPosition 2 set for arena '" + arena.getName() + "' at " + 
            formatLocation(target));
    }

    private void handleAddSpawn(CommandSender sender, String[] args) {
        if (!sender.hasPermission("zombiewaves.admin")) {
            sender.sendMessage(plugin.getConfigManager().getPrefix() + 
                plugin.getConfigManager().getMessage("no-permission"));
            return;
        }
        if (!(sender instanceof Player player)) {
            sender.sendMessage(plugin.getConfigManager().getPrefix() + 
                "§cThis command can only be used by players!");
            return;
        }
        if (args.length < 2) {
            sender.sendMessage(plugin.getConfigManager().getPrefix() + 
                "§cUsage: /zwave addspawn <arenaName>");
            return;
        }
        
        String arenaName = args[1].toLowerCase();
        Arena arena = plugin.getArenaManager().getArena(arenaName);
        if (arena == null) {
            sender.sendMessage(plugin.getConfigManager().getPrefix() + 
                "§cArena '" + arenaName + "' does not exist!");
            return;
        }
        
        Location target = getTargetBlock(player);
        if (target == null) {
            sender.sendMessage(plugin.getConfigManager().getPrefix() + 
                "§cCannot find target block! Look at a block.");
            return;
        }
        
        plugin.getArenaManager().addArenaSpawnPoint(arenaName, target);
        sender.sendMessage(plugin.getConfigManager().getPrefix() + 
            "§aAdded spawn point to arena '" + arena.getName() + "' at " + 
            formatLocation(target));
    }

    private void handleRemoveSpawn(CommandSender sender, String[] args) {
        if (!sender.hasPermission("zombiewaves.admin")) {
            sender.sendMessage(plugin.getConfigManager().getPrefix() + 
                plugin.getConfigManager().getMessage("no-permission"));
            return;
        }
        if (!(sender instanceof Player player)) {
            sender.sendMessage(plugin.getConfigManager().getPrefix() + 
                "§cThis command can only be used by players!");
            return;
        }
        if (args.length < 2) {
            sender.sendMessage(plugin.getConfigManager().getPrefix() + 
                "§cUsage: /zwave removespawn <arenaName>");
            return;
        }
        
        String arenaName = args[1].toLowerCase();
        Arena arena = plugin.getArenaManager().getArena(arenaName);
        if (arena == null) {
            sender.sendMessage(plugin.getConfigManager().getPrefix() + 
                "§cArena '" + arenaName + "' does not exist!");
            return;
        }
        
        Location target = getTargetBlock(player);
        if (target == null) {
            sender.sendMessage(plugin.getConfigManager().getPrefix() + 
                "§cCannot find target block! Look at a block.");
            return;
        }
        
        plugin.getArenaManager().removeArenaSpawnPoint(arenaName, target);
        sender.sendMessage(plugin.getConfigManager().getPrefix() + 
            "§aRemoved spawn point from arena '" + arena.getName() + "'");
    }

    private void handleCreateArena(CommandSender sender, String[] args) {
        if (!sender.hasPermission("zombiewaves.admin")) {
            sender.sendMessage(plugin.getConfigManager().getPrefix() + 
                plugin.getConfigManager().getMessage("no-permission"));
            return;
        }
        if (args.length < 2) {
            sender.sendMessage(plugin.getConfigManager().getPrefix() + 
                "§cUsage: /zwave createarena <name>");
            return;
        }
        
        String arenaName = args[1];
        if (plugin.getArenaManager().arenaExists(arenaName)) {
            sender.sendMessage(plugin.getConfigManager().getPrefix() + 
                "§cArena '" + arenaName + "' already exists!");
            return;
        }
        
        Arena arena = plugin.getArenaManager().createArena(arenaName);
        sender.sendMessage(plugin.getConfigManager().getPrefix() + 
            "§aArena '" + arena.getName() + "' created! Use:");
        sender.sendMessage("§e  /zwave setpos1 " + arenaName + " §7- Set first corner");
        sender.sendMessage("§e  /zwave setpos2 " + arenaName + " §7- Set second corner");
        sender.sendMessage("§e  /zwave addspawn " + arenaName + " §7- Add spawn points");
    }

    private void handleDeleteArena(CommandSender sender, String[] args) {
        if (!sender.hasPermission("zombiewaves.admin")) {
            sender.sendMessage(plugin.getConfigManager().getPrefix() + 
                plugin.getConfigManager().getMessage("no-permission"));
            return;
        }
        if (args.length < 2) {
            sender.sendMessage(plugin.getConfigManager().getPrefix() + 
                "§cUsage: /zwave deletearena <name>");
            return;
        }
        
        String arenaName = args[1];
        if (!plugin.getArenaManager().arenaExists(arenaName)) {
            sender.sendMessage(plugin.getConfigManager().getPrefix() + 
                "§cArena '" + arenaName + "' does not exist!");
            return;
        }
        
        plugin.getArenaManager().deleteArena(arenaName);
        sender.sendMessage(plugin.getConfigManager().getPrefix() + 
            "§cArena '" + arenaName + "' deleted!");
    }

    private void handleListArenas(CommandSender sender) {
        var arenas = plugin.getArenaManager().getAllArenas();
        if (arenas.isEmpty()) {
            sender.sendMessage(plugin.getConfigManager().getPrefix() + 
                "§cNo arenas exist. Create one with §e/zwave createarena <name>");
            return;
        }
        
        sender.sendMessage("§6§l=== Available Arenas ===");
        for (Arena arena : arenas) {
            String status = arena.isComplete() ? "§a✓" : "§c✗";
            String active = arena.isActive() ? " §e[ACTIVE]" : "";
            sender.sendMessage(status + " §f" + arena.getName() + active + 
                " §7(" + arena.getSpawnPoints().size() + " spawns)");
        }
    }

    private void handleSelectArena(CommandSender sender, String[] args) {
        if (!sender.hasPermission("zombiewaves.admin")) {
            sender.sendMessage(plugin.getConfigManager().getPrefix() + 
                plugin.getConfigManager().getMessage("no-permission"));
            return;
        }
        if (args.length < 2) {
            sender.sendMessage(plugin.getConfigManager().getPrefix() + 
                "§cUsage: /zwave selectarena <name>");
            return;
        }
        
        String arenaName = args[1].toLowerCase();
        Arena arena = plugin.getArenaManager().getArena(arenaName);
        if (arena == null) {
            sender.sendMessage(plugin.getConfigManager().getPrefix() + 
                "§cArena '" + arenaName + "' does not exist!");
            return;
        }
        if (!arena.isComplete()) {
            sender.sendMessage(plugin.getConfigManager().getPrefix() + 
                "§cArena '" + arenaName + "' is not complete! Set pos1, pos2, and spawn points.");
            return;
        }
        
        plugin.getArenaManager().setActiveArena(arenaName);
        plugin.getGameManager().setSelectedArena(arenaName);
        sender.sendMessage(plugin.getConfigManager().getPrefix() + 
            "§aArena '" + arena.getName() + "' selected! Mobs will spawn here.");
    }

    private void handleInfoArena(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(plugin.getConfigManager().getPrefix() + 
                "§cUsage: /zwave infoarena <name>");
            return;
        }
        
        String arenaName = args[1].toLowerCase();
        Arena arena = plugin.getArenaManager().getArena(arenaName);
        if (arena == null) {
            sender.sendMessage(plugin.getConfigManager().getPrefix() + 
                "§cArena '" + arenaName + "' does not exist!");
            return;
        }
        
        sender.sendMessage("§6§l=== Arena: " + arena.getName() + " ===");
        sender.sendMessage("§eStatus: §f" + (arena.isComplete() ? "§aComplete" : "§cIncomplete"));
        
        if (arena.getPos1() != null) {
            sender.sendMessage("§ePos1: §f" + formatLocation(arena.getPos1()));
        } else {
            sender.sendMessage("§ePos1: §cNot set");
        }
        
        if (arena.getPos2() != null) {
            sender.sendMessage("§ePos2: §f" + formatLocation(arena.getPos2()));
        } else {
            sender.sendMessage("§ePos2: §cNot set");
        }
        
        sender.sendMessage("§eSpawn Points: §f" + arena.getSpawnPoints().size());
        for (int i = 0; i < arena.getSpawnPoints().size(); i++) {
            sender.sendMessage("§e  " + (i + 1) + ". §f" + formatLocation(arena.getSpawnPoints().get(i)));
        }
    }

    private Location getTargetBlock(Player player) {
        RayTraceResult result = player.rayTraceBlocks(100);
        if (result != null && result.getHitBlock() != null) {
            return result.getHitBlock().getLocation();
        }
        return null;
    }

    private String formatLocation(Location loc) {
        if (loc == null) return "null";
        return String.format("§e%s §f[§e%d, %d, %d§f]",
            loc.getWorld().getName(),
            loc.getBlockX(),
            loc.getBlockY(),
            loc.getBlockZ());
    }

    private void sendHelp(CommandSender sender) {
        sender.sendMessage(plugin.getConfigManager().getPrefix() + "§eZombie Waves Commands:");
        sender.sendMessage("§e/zwave start §7- Start the game");
        sender.sendMessage("§e/zwave stop §7- Stop the game");
        sender.sendMessage("§e/zwave status §7- Show game status");
        sender.sendMessage("§e/zwave shop §7- Open the shop");
        sender.sendMessage("§e/zwave gold §7- Check your gold");
        sender.sendMessage("§6§l=== Arena Commands ===");
        sender.sendMessage("§e/zwave arenas §7- List all arenas");
        sender.sendMessage("§e/zwave createarena <name> §7- Create new arena");
        sender.sendMessage("§e/zwave selectarena <name> §7- Select arena for game");
        sender.sendMessage("§e/zwave infoarena <name> §7- Show arena info");
        sender.sendMessage("§e/zwave deletearena <name> §7- Delete arena");
        sender.sendMessage("§e/zwave setpos1 <arena> §7- Set corner 1 (look at block)");
        sender.sendMessage("§e/zwave setpos2 <arena> §7- Set corner 2 (look at block)");
        sender.sendMessage("§e/zwave addspawn <arena> §7- Add spawn point (look at block)");
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
            completions.add("start");
            completions.add("stop");
            completions.add("status");
            completions.add("shop");
            completions.add("gold");
            completions.add("arenas");
            completions.add("createarena");
            completions.add("selectarena");
            completions.add("infoarena");
            completions.add("deletearena");
            completions.add("setpos1");
            completions.add("setpos2");
            completions.add("addspawn");
            completions.add("removespawn");
            
            return completions.stream()
                .filter(s -> s.toLowerCase().startsWith(args[0].toLowerCase()))
                .toList();
        }
        
        if (args.length == 2) {
            String subCmd = args[0].toLowerCase();
            if (subCmd.equals("setpos1") || subCmd.equals("setpos2") || 
                subCmd.equals("addspawn") || subCmd.equals("removespawn") ||
                subCmd.equals("selectarena") || subCmd.equals("infoarena") ||
                subCmd.equals("deletearena")) {
                return plugin.getArenaManager().getAllArenas().stream()
                    .map(Arena::getName)
                    .filter(s -> s.toLowerCase().startsWith(args[1].toLowerCase()))
                    .collect(Collectors.toList());
            }
        }
        
        return completions;
    }
}
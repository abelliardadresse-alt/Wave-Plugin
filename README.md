# 🧟 ZombieWaves - Minecraft Plugin

A **Call of Duty Zombies** inspired wave survival plugin for Minecraft 1.21.

## ✨ Features

| Feature | Description |
|---------|-------------|
| 🌊 **Wave System** | Survive increasingly difficult waves of mobs |
| 🗺️ **Arena System** | Create custom arenas with spawn points and boundaries |
| 🎮 **Lobby System** | Join arenas with countdown, max 20 players |
| 🚪 **Auto Teleport** | Lobby before game, exit after |
| 🎲 **Random Spawning** | Mobs spawn at random locations with random types |
| 📈 **Difficulty Scaling** | Health, damage, and speed increase each wave |
| 🪙 **Gold System** | Earn gold by killing mobs |
| 🛒 **Shop** | Buy weapons, armor, and upgrades |
| 📋 **Scoreboard** | Side panel with wave, kills, gold, remaining mobs |

## 🎮 Supported Mobs
- **Zombie** (60% spawn rate) - Base enemy
- **Skeleton** (25% spawn rate) - Ranged attacker  
- **Husk** (15% spawn rate) - Tough variant

## 📥 Installation

1. Download JAR from [Releases](../../releases)
2. Place in server's `plugins/` folder
3. Restart server

## 🎯 Quick Setup (Admin)

1. **Set lobby and exit locations** (where players spawn/return):
   ```
   /zwave setlobby         # At your location (global lobby)
   /zwave setexit          # At your location (return point)
   ```

2. **Create an arena:**
   ```
   /zwave createarena mymap
   ```

3. **Set arena lobby** (optional - overrides global):
   ```
   /zwave setlobby mymap   # Set arena-specific lobby
   ```

4. **Set arena spawn** (where players go when game starts):
   ```
   /zwave setspawn mymap    # At your location
   ```

5. **Set arena boundaries** (optional):
   ```
   /zwave setpos1 mymap    # Look at corner 1
   /zwave setpos2 mymap    # Look at corner 2
   ```

6. **Add spawn points** (where mobs appear):
   ```
   /zwave addspawn mymap   # Look at spawn location, repeat
   ```

## 🎮 Player Commands

| Command | Description |
|---------|-------------|
| `/zwave join <arena>` | Join an arena (teleport to lobby) |
| `/zwave leave` | Leave the arena (return to exit) |
| `/zwave arenas` | List all available arenas |
| `/zwave status` | Show game/lobby status |
| `/zwave shop` | Open the shop |
| `/zwave gold` | Check your gold balance |

## 🛠 Admin Commands

| Command | Description |
|---------|-------------|
| `/zwave createarena <name>` | Create a new arena |
| `/zwave deletearena <name>` | Delete an arena |
| `/zwave infoarena <name>` | Show arena details |
| `/zwave setlobby [arena]` | Set lobby location |
| `/zwave setspawn <arena>` | Set game spawn point |
| `/zwave setexit` | Set exit location |
| `/zwave setpos1 <arena>` | Set boundary corner 1 |
| `/zwave setpos2 <arena>` | Set boundary corner 2 |
| `/zwave addspawn <arena>` | Add mob spawn point |
| `/zwave removespawn <arena>` | Remove spawn point |
| `/zwave stop` | Force stop the game |

## 🔑 Permissions

- `zombiewaves.admin` - All admin commands
- `zombiewaves.shop` - Access to the shop

## 🎯 How It Works

1. **Players join:** `/zwave join mymap`
2. **Teleport to lobby** at the arena's lobby location
3. **Wait for players** (need 2+ to start, max 20)
4. **Countdown** starts automatically when 2+ players
5. **Game starts:** Players teleport to game spawn
6. **Survive waves** of zombies, skeletons, and husks
7. **Leave anytime:** `/zwave leave` returns you to exit

## 🔨 Building

```bash
mvn clean package
```

JAR in `target/ZombieWaves-1.0.0.jar`
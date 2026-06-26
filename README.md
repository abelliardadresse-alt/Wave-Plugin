# 🧟 ZombieWaves - Minecraft Plugin

A **Call of Duty Zombies** inspired wave survival plugin for Minecraft 1.21.

## ✨ Features

| Feature | Description |
|---------|-------------|
| 🌊 **Wave System** | Survive increasingly difficult waves of mobs |
| 🗺️ **Arena System** | Create custom arenas with spawn points and boundaries |
| 🎲 **Random Spawning** | Mobs spawn at random locations with random types |
| 📈 **Difficulty Scaling** | Health, damage, and speed increase each wave |
| 🪙 **Gold System** | Earn gold by killing mobs |
| 🛒 **Shop** | Buy weapons, armor, and upgrades |
| 📋 **Scoreboard** | Side panel with wave, kills, gold, remaining mobs |
| ⚙️ **Fully Configurable** | Customize everything in `config.yml` |

## 🎮 Supported Mobs
- **Zombie** (60% spawn rate) - Base enemy
- **Skeleton** (25% spawn rate) - Ranged attacker  
- **Husk** (15% spawn rate) - Tough variant

## 📥 Installation

1. Download `ZombieWaves-1.0.0.jar` from [Releases](../../releases)
2. Place in server's `plugins/` folder
3. Restart server

## 🎯 Quick Setup

1. **Create an arena:**
   ```
   /zwave createarena mymap
   ```

2. **Set arena boundaries** (look at blocks):
   ```
   /zwave setpos1 mymap    # Look at corner 1
   /zwave setpos2 mymap   # Look at corner 2
   ```

3. **Add spawn points** (where mobs appear):
   ```
   /zwave addspawn mymap   # Look at spawn location, repeat for multiple
   ```

4. **Select the arena and start:**
   ```
   /zwave selectarena mymap
   /zwave start
   ```

## 📜 Commands

### Player Commands
| Command | Description |
|---------|-------------|
| `/zwave start` | Start the game |
| `/zwave stop` | Stop the game |
| `/zwave status` | Show game status |
| `/zwave shop` | Open the shop (or right-click with a compass) |
| `/zwave gold` | Check your gold balance |

### Arena Commands (Admin)
| Command | Description |
|---------|-------------|
| `/zwave createarena <name>` | Create a new arena |
| `/zwave deletearena <name>` | Delete an arena |
| `/zwave arenas` | List all arenas |
| `/zwave selectarena <name>` | Select arena for the game |
| `/zwave infoarena <name>` | Show arena details |
| `/zwave setpos1 <arena>` | Set corner 1 (look at block) |
| `/zwave setpos2 <arena>` | Set corner 2 (look at block) |
| `/zwave addspawn <arena>` | Add spawn point (look at block) |
| `/zwave removespawn <arena>` | Remove spawn point (look at block) |

### Admin Commands
| Command | Description |
|---------|-------------|
| `/zwaveadmin reload` | Reload configuration |
| `/zwaveadmin forcewave` | Force start next wave |

## 🔑 Permissions

- `zombiewaves.start` - Start the game
- `zombiewaves.stop` - Stop the game
- `zombiewaves.admin` - Arena commands and admin
- `zombiewaves.shop` - Access to the shop

## ⚙️ Configuration

### General Settings (config.yml)
```yaml
general:
  total-waves: 10        # Number of waves in a game
  wave-delay: 30        # Seconds between waves
  grace-period: 60       # Seconds before first wave
```

### Mob Types (config.yml)
Configure mob types in `mob-types` section:
- `zombie` - Basic zombie (60% spawn weight)
- `skeleton` - Skeleton with bow (25% spawn weight)
- `husk` - Husk zombie (15% spawn weight)

### Shop Items (config.yml)
```yaml
shop:
  items:
    diamond-sword:
      type: DIAMOND_SWORD
      name: "&bDiamond Sword"
      price: 150
      enchantments:
        - "DAMAGE_ALL:2"
```

### Scoreboard (config.yml)
```yaml
scoreboard:
  title: "&6&lZOMBIE WAVES"
  lines:
    - "&eWave: &f{wave}/{max-wave}"
    - "&6Kills: &f{kills}"
    - "&6Gold: &f{gold}"
    - "&aMobs: &f{remaining}"
```

## 📊 Placeholders

Scoreboard supports these placeholders:
- `{wave}` / `{manche}` - Current wave number
- `{max-wave}` - Maximum waves
- `{kills}` - Player's kill count
- `{gold}` - Player's gold amount
- `{remaining}` - Mobs remaining in wave
- `{next-wave}` - Countdown to next wave

## 🔨 Building

```bash
mvn clean package
```

The compiled JAR will be in `target/ZombieWaves-1.0.0.jar`

## 📁 Files

- `plugins/ZombieWaves/config.yml` - Main configuration
- `plugins/ZombieWaves/arenas.yml` - Arena data (auto-saved)
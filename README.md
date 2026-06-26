# ZombieWaves - Minecraft Plugin

A Call of Duty Zombies inspired wave survival plugin for Minecraft 1.21.

## Features

- **Wave System**: Survive increasingly difficult waves of zombies, skeletons, and husks
- **Randomized Spawning**: Mobs spawn at random configured locations with random types
- **Difficulty Scaling**: Health, damage, and speed increase with each wave
- **Gold System**: Earn gold by killing mobs to purchase weapons and armor
- **Shop**: Buy weapons, armor, and upgrades with your gold
- **Scoreboard**: Side panel showing wave, kills, gold, and remaining mobs
- **Fully Configurable**: Customize everything in config.yml

## Installation

1. Build the plugin with Maven: `mvn clean package`
2. Copy the JAR file from `target/ZombieWaves-1.0.0.jar` to your server's `plugins` folder
3. Restart your server
4. Configure the plugin in `plugins/ZombieWaves/config.yml`

## Commands

### Player Commands (`/zwave`)
- `/zwave start` - Start the game
- `/zwave stop` - Stop the game
- `/zwave status` - Show current game status
- `/zwave shop` - Open the shop (or right-click with a compass)
- `/zwave gold` - Check your gold balance

### Admin Commands (`/zwaveadmin`)
- `/zwaveadmin reload` - Reload configuration
- `/zwaveadmin forcewave` - Force start the next wave

## Permissions

- `zombiewaves.start` - Start the game
- `zombiewaves.stop` - Stop the game
- `zombiewaves.admin` - Admin commands
- `zombiewaves.shop` - Access to the shop

## Configuration

### General Settings
```yaml
general:
  total-waves: 10        # Number of waves in a game
  wave-delay: 30         # Seconds between waves
  min-players: 1         # Minimum players required
  grace-period: 60       # Seconds before first wave
```

### Mob Types
Configure mob types in `mob-types` section:
- `zombie` - Basic zombie (60% spawn weight)
- `skeleton` - Skeleton with bow (25% spawn weight)
- `husk` - Husk zombie (15% spawn weight)

### Spawn Points
Add spawn points for different maps:
```yaml
spawn-points:
  mymap:
    - world: "world"
      x: 100
      y: 64
      z: 100
    - world: "world"
      x: -50
      y: 64
      z: 200
```

### Shop Items
Configure items in the shop:
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

### Scoreboard
Customize the sidebar scoreboard display:
```yaml
scoreboard:
  title: "&6&lZOMBIE WAVES"
  lines:
    - "&7&m---------------"
    - "&eWave: &f{wave}/{max-wave}"
    - "&6&lKills: &f{kills}"
    - "&6&lGold: &f{gold}"
    - "&aMobs remaining: &f{remaining}"
```

## Placeholders

Scoreboard supports these placeholders:
- `{wave}` / `{manche}` - Current wave number
- `{max-wave}` - Maximum waves
- `{kills}` - Player's kill count
- `{gold}` - Player's gold amount
- `{remaining}` - Mobs remaining in wave
- `{next-wave}` - Countdown to next wave

## Gameplay

1. Start the game with `/zwave start`
2. Wait for the grace period
3. Kill mobs to earn gold
4. Use `/zwave shop` (or compass) to buy weapons
5. Survive all waves to win!

## Building

```bash
mvn clean package
```

The compiled JAR will be in `target/ZombieWaves-1.0.0.jar`
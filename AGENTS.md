# ZombieWaves Minecraft Plugin

## Project Overview
A Minecraft plugin for 1.21 that simulates zombie wave gameplay like Call of Duty Zombies. Players survive waves of mobs (Zombies, Skeletons, Husks) across multiple configurable arenas.

## Key Features
- Multi-arena support (each arena has independent games)
- Wave-based gameplay with progressive difficulty
- Gold system for kills and purchasing upgrades
- Scoreboard displaying wave, kills, gold, and remaining mobs
- Fully configurable via config.yml

## Architecture

### Managers
- **GameManager**: Tracks active arenas, game state per arena, wave progression
- **WaveManager**: Handles mob spawning, difficulty scaling, arena-specific mob tracking
- **ArenaManager**: Arena CRUD, player arena assignments
- **LobbyManager**: Player join/leave, countdown, game start
- **ScoreboardManager**: Sidebar scoreboard updates with placeholders

### Arena Configuration
Each arena requires:
- pos1, pos2 (boundary corners)
- spawnPoints (mob apparition points - numbered 1, 2, 3...)
- lobbyLocation (wait room where players join)
- gameSpawnLocation (where players teleport when game starts)

Arena is complete when all 5 requirements are set.

### Commands
- `/zwave join <arena>` - Join arena lobby
- `/zwave leave` - Leave arena
- `/zwave setspawn <arena>` - Set player game spawn
- `/zwave setlobby <arena>` - Set lobby location
- `/zwave addspawn <arena>` - Add mob apparition point
- `/zwave removespawn <arena> <number>` - Remove apparition point
- `/zwave infoarena <name>` - View arena config

### Placeholders
Scoreboard supports: `{wave}`, `{manche}`, `{kills}`, `{gold}`, `{remaining}`, `{next-wave}`

## Important Notes
- Arena names are case-insensitive (stored lowercase)
- Game start requires players to be in lobby countdown
- Prevent joining when game is already in progress
- mobSpawnLocation and lobbyLocation are distinct

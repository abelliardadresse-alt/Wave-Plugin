package com.zombiewaves.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerData {

    private int gold;
    private int kills;
    private final Map<String, Long> cooldowns;

    public PlayerData() {
        this.gold = 0;
        this.kills = 0;
        this.cooldowns = new HashMap<>();
    }

    public int getGold() {
        return gold;
    }

    public void setGold(int gold) {
        this.gold = gold;
    }

    public void addGold(int amount) {
        this.gold += amount;
    }

    public void removeGold(int amount) {
        this.gold -= amount;
        if (this.gold < 0) this.gold = 0;
    }

    public int getKills() {
        return kills;
    }

    public void setKills(int kills) {
        this.kills = kills;
    }

    public void addKill() {
        this.kills++;
    }

    public void addKills(int amount) {
        this.kills += amount;
    }

    public boolean hasCooldown(String key) {
        Long lastUse = cooldowns.get(key);
        if (lastUse == null) return false;
        return System.currentTimeMillis() < lastUse;
    }

    public void setCooldown(String key, long durationMs) {
        cooldowns.put(key, System.currentTimeMillis() + durationMs);
    }

    public long getCooldownRemaining(String key) {
        Long lastUse = cooldowns.get(key);
        if (lastUse == null) return 0;
        long remaining = lastUse - System.currentTimeMillis();
        return remaining > 0 ? remaining : 0;
    }

    public void clearCooldowns() {
        cooldowns.clear();
    }
}
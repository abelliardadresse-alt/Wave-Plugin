package com.zombiewaves.utils;

import org.bukkit.Bukkit;
import org.bukkit.World;

public class BukkitUtil {

    public static World getWorld(String name) {
        if (name == null) return null;
        return Bukkit.getWorld(name);
    }
}
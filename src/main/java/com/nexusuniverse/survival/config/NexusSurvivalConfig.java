package com.nexusuniverse.survival.config;

import org.bukkit.plugin.java.JavaPlugin;

public class NexusSurvivalConfig {

    private final JavaPlugin plugin;

    public NexusSurvivalConfig(JavaPlugin plugin) {
        this.plugin = plugin;
        plugin.saveDefaultConfig();
    }

    public double getDouble(String path, double fallback) {
        return plugin.getConfig().getDouble(path, fallback);
    }

    public int getInt(String path, int fallback) {
        return plugin.getConfig().getInt(path, fallback);
    }

    public void reload() {
        plugin.reloadConfig();
    }
}

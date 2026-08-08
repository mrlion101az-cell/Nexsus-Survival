package com.nexusuniverse.survival.config;

import org.bukkit.plugin.java.JavaPlugin;

public class NexusSurvivalConfig {

    private final JavaPlugin plugin;

    public NexusSurvivalConfig(JavaPlugin plugin) {
        this.plugin = plugin;
        plugin.saveDefaultConfig();
        // saveDefaultConfig() only writes config.yml the very first time this plugin is
        // installed -- copyDefaults(true) + saveConfig() merges in anything a later update adds
        // to an already-existing config.yml, instead of it silently never showing up.
        plugin.getConfig().options().copyDefaults(true);
        plugin.saveConfig();
    }

    public double getDouble(String path, double fallback) {
        return plugin.getConfig().getDouble(path, fallback);
    }

    public int getInt(String path, int fallback) {
        return plugin.getConfig().getInt(path, fallback);
    }

    public boolean getBoolean(String path, boolean fallback) {
        return plugin.getConfig().getBoolean(path, fallback);
    }

    public String getString(String path, String fallback) {
        return plugin.getConfig().getString(path, fallback);
    }

    public void reload() {
        plugin.reloadConfig();
    }
}

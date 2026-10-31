package dev.hauchdev.hholograms.config;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;

public final class PluginConfig {
    private static final int CONFIG_VERSION = 1;

    private final Plugin plugin;
    private FileConfiguration config;

    private boolean debug;
    private String language;
    private int refreshIntervalTicks;
    private int animationIntervalTicks;
    private double defaultSpacing;
    private int defaultViewRange;
    private String defaultBillboard;

    public PluginConfig(Plugin plugin) {
        this.plugin = plugin;
        loadConfig();
    }

    public void loadConfig() {
        plugin.saveDefaultConfig();
        plugin.reloadConfig();
        this.config = plugin.getConfig();
        migrateConfig();
        this.language = config.getString("language", "en");
        this.debug = config.getBoolean("debug", false);
        this.refreshIntervalTicks = Math.max(1, config.getInt("refresh-interval-ticks", 20));
        this.animationIntervalTicks = Math.max(1, config.getInt("animation-interval-ticks", 2));
        this.defaultSpacing = config.getDouble("defaults.line-spacing", 0.3);
        this.defaultViewRange = config.getInt("defaults.view-range", 48);
        this.defaultBillboard = config.getString("defaults.billboard", "CENTER");
    }

    private void migrateConfig() {
        int stored = config.getInt("config-version", 0);
        if (stored >= CONFIG_VERSION) return;
        // add missing keys from default resource if present
        try (InputStream in = plugin.getResource("config.yml")) {
            if (in == null) return;
            FileConfiguration defaults = YamlConfiguration.loadConfiguration(new InputStreamReader(in, StandardCharsets.UTF_8));
            boolean changed = false;
            for (String key : defaults.getKeys(true)) {
                if (defaults.isConfigurationSection(key)) continue;
                if (!config.contains(key)) {
                    config.set(key, defaults.get(key));
                    changed = true;
                }
            }
            config.set("config-version", CONFIG_VERSION);
            if (changed) {
                File f = new File(plugin.getDataFolder(), "config.yml");
                try { config.save(f); } catch (IOException ignored) {}
            }
        } catch (Exception ignored) {}
    }

    public void reloadConfig() { loadConfig(); }

    public Plugin plugin() { return plugin; }
    public FileConfiguration raw() { return config; }
    public String language() { return language; }
    public boolean debug() { return debug; }
    public int refreshIntervalTicks() { return refreshIntervalTicks; }
    public int animationIntervalTicks() { return animationIntervalTicks; }
    public double defaultSpacing() { return defaultSpacing; }
    public int defaultViewRange() { return defaultViewRange; }
    public String defaultBillboard() { return defaultBillboard; }

    public String getString(String path, String def) { return config.getString(path, def); }
    public int getInt(String path, int def) { return config.getInt(path, def); }
    public double getDouble(String path, double def) { return config.getDouble(path, def); }
    public boolean getBoolean(String path, boolean def) { return config.getBoolean(path, def); }
    public List<String> getStringList(String path) { return config.getStringList(path); }
}
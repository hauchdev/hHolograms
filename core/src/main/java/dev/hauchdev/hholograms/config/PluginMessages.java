package dev.hauchdev.hholograms.config;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public final class PluginMessages {
    private final Plugin plugin;
    private FileConfiguration lang;
    private final MiniMessage mm = MiniMessage.miniMessage();

    public PluginMessages(Plugin plugin) {
        this.plugin = plugin;
        plugin.saveResource("lang/lang_en.yml", false);
        load();
    }

    private void load() {
        String langName = plugin.getConfig().getString("language", "en");
        File f = new File(plugin.getDataFolder(), "lang/lang_" + langName + ".yml");
        if (!f.exists()) {
            f = new File(plugin.getDataFolder(), "lang/lang_en.yml");
        }
        this.lang = YamlConfiguration.loadConfiguration(f);
    }

    public void reload() {
        plugin.reloadConfig();
        load();
    }

    public String getString(String path) {
        if (lang == null) return path;
        String s = lang.getString(path);
        return s == null ? path : s;
    }

    public String getString(String path, Map<String, String> placeholders) {
        String raw = getString(path);
        if (raw == null || placeholders == null) return raw;
        String result = raw;
        for (Map.Entry<String, String> e : placeholders.entrySet()) {
            result = result.replace("{" + e.getKey() + "}", String.valueOf(e.getValue()));
        }
        return result;
    }

    public Component getComponent(String path) {
        return mm.deserialize(getString(path));
    }

    public Component getComponent(String path, Map<String, String> placeholders) {
        return mm.deserialize(getString(path, placeholders));
    }
}
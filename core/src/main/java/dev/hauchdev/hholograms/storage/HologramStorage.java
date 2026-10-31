package dev.hauchdev.hholograms.storage;

import dev.hauchdev.hholograms.model.Hologram;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class HologramStorage {
    private final Plugin plugin;
    private final File file;

    public HologramStorage(Plugin plugin) {
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), "holograms.yml");
        if (!file.exists()) {
            try { file.getParentFile().mkdirs(); file.createNewFile(); } catch (IOException ignored) {}
        }
    }

    public List<Hologram> load() {
        List<Hologram> out = new ArrayList<>();
        FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        ConfigurationSection root = cfg.getConfigurationSection("holograms");
        if (root == null) return out;
        for (String name : root.getKeys(false)) {
            ConfigurationSection s = root.getConfigurationSection(name);
            if (s == null) continue;
            String worldName = s.getString("world");
            World world = Bukkit.getWorld(worldName);
            if (world == null) {
                plugin.getLogger().warning("[hHolograms] World '" + worldName + "' not found for hologram '" + name + "' - skipped.");
                continue;
            }
            Location loc = new Location(world,
                    s.getDouble("x"), s.getDouble("y"), s.getDouble("z"),
                    (float) s.getDouble("yaw", 0), (float) s.getDouble("pitch", 0));
            Hologram h = new Hologram(name, loc);
            Object rawLines = s.get("lines");
            if (rawLines instanceof java.util.List<?> list) {
                for (Object o : list) {
                    if (o instanceof String str) {
                        h.lines().add(new dev.hauchdev.hholograms.model.HologramLine(str));
                    } else if (o instanceof java.util.Map<?, ?> m) {
                        Object to = m.get("text");
                        String text = (to == null) ? "" : String.valueOf(to);
                        dev.hauchdev.hholograms.model.HologramLine hl = new dev.hauchdev.hholograms.model.HologramLine(text);
                        if (m.containsKey("scale")) hl.setScale(((Number) (Object) m.get("scale")).floatValue());
                        if (m.containsKey("animation")) hl.setAnimation(String.valueOf(m.get("animation")));
                        if (m.containsKey("animation-speed")) hl.setAnimationSpeed(((Number) (Object) m.get("animation-speed")).intValue());
                        if (m.containsKey("paused")) hl.setPaused(Boolean.parseBoolean(String.valueOf((Object) m.get("paused"))));
                        h.lines().add(hl);
                    }
                }
            } else {
                for (String str : s.getStringList("lines")) {
                    h.lines().add(new dev.hauchdev.hholograms.model.HologramLine(str));
                }
            }
            h.setLineSpacing(s.getDouble("line-spacing", 0.3));
            h.setViewRange(s.getInt("view-range", 48));
            h.setBillboard(s.getString("billboard", "CENTER"));
            h.setSeeThrough(s.getBoolean("see-through", true));
            h.setShadowed(s.getBoolean("shadowed", true));
            h.setBackgroundColor(s.getString("background", "transparent"));
            h.setAnimation(s.getString("animation", "none"));
            h.setAnimationSpeed(s.getInt("animation-speed", 1));
            h.setPaused(s.getBoolean("paused", false));
            h.setScale((float) s.getDouble("scale", 1.0));
            h.setAlignment(s.getString("alignment", "CENTER"));
            h.setType(s.getString("type", "TEXT"));
            h.setContent(s.getString("content", ""));
            out.add(h);
        }
        return out;
    }

    public void save(List<Hologram> holograms) {
        FileConfiguration cfg = new YamlConfiguration();
        for (Hologram h : holograms) {
            String base = "holograms." + h.name();
            Location l = h.location();
            cfg.set(base + ".world", l.getWorld().getName());
            cfg.set(base + ".x", l.getX());
            cfg.set(base + ".y", l.getY());
            cfg.set(base + ".z", l.getZ());
            cfg.set(base + ".yaw", l.getYaw());
            cfg.set(base + ".pitch", l.getPitch());
            java.util.List<java.util.Map<String, Object>> lineMaps = new java.util.ArrayList<>();
            for (dev.hauchdev.hholograms.model.HologramLine hl : h.lines()) {
                java.util.Map<String, Object> m = new java.util.LinkedHashMap<>();
                m.put("text", hl.text());
                m.put("scale", hl.scale());
                m.put("animation", hl.animation());
                m.put("animation-speed", hl.animationSpeed());
                m.put("paused", hl.paused());
                lineMaps.add(m);
            }
            cfg.set(base + ".lines", lineMaps);
            cfg.set(base + ".line-spacing", h.lineSpacing());
            cfg.set(base + ".view-range", h.viewRange());
            cfg.set(base + ".billboard", h.billboard());
            cfg.set(base + ".see-through", h.seeThrough());
            cfg.set(base + ".shadowed", h.shadowed());
            cfg.set(base + ".background", h.backgroundColor());
            cfg.set(base + ".animation", h.animation());
            cfg.set(base + ".animation-speed", h.animationSpeed());
            cfg.set(base + ".paused", h.paused());
            cfg.set(base + ".scale", h.scale());
            cfg.set(base + ".alignment", h.alignment());
            cfg.set(base + ".type", h.type());
            cfg.set(base + ".content", h.content());
        }
        try { cfg.save(file); } catch (IOException e) {
            plugin.getLogger().warning("Failed to save holograms: " + e.getMessage());
        }
    }
}
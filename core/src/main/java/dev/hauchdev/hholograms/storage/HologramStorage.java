package dev.hauchdev.hholograms.storage;

import dev.hauchdev.hholograms.model.Hologram;
import dev.hauchdev.hholograms.model.HologramLine;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

public final class HologramStorage {
    private final Plugin plugin;
    private final File file;

    public HologramStorage(Plugin plugin) {
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), "holograms.yml");
        if (!file.exists()) {
            try {
                Files.createDirectories(file.toPath().getParent());
                file.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().severe("Could not create hologram storage at " + file + ": " + e.getMessage());
            }
        }
    }

    public List<Hologram> load() {
        List<Hologram> out = new ArrayList<>();
        FileConfiguration cfg = new YamlConfiguration();
        try {
            cfg.load(file);
        } catch (IOException | org.bukkit.configuration.InvalidConfigurationException e) {
            throw new IllegalStateException("Could not load hologram storage at " + file + ". The file was left untouched.", e);
        }
        ConfigurationSection root = cfg.getConfigurationSection("holograms");
        if (root == null) return out;
        for (String name : root.getKeys(false)) {
            ConfigurationSection s = root.getConfigurationSection(name);
            if (s == null) continue;
            if (!name.matches("[A-Za-z0-9_-]{1,32}")) {
                plugin.getLogger().warning("Invalid hologram name '" + name + "' in storage; skipped.");
                continue;
            }
            try {
                String worldName = s.getString("world", "");
                World world = Bukkit.getWorld(worldName);
                if (world == null) {
                    plugin.getLogger().warning("World '" + worldName + "' not found for hologram '" + name + "'; skipped.");
                    continue;
                }
                Location loc = new Location(world,
                        s.getDouble("x"), s.getDouble("y"), s.getDouble("z"),
                        (float) s.getDouble("yaw", 0), (float) s.getDouble("pitch", 0));
                Hologram h = new Hologram(name, loc);
                Object rawPages = s.get("pages");
                if (rawPages instanceof List<?> pageValues) {
                    List<List<HologramLine>> pages = new ArrayList<>();
                    for (Object pageValue : pageValues) {
                        if (pageValue instanceof List<?> pageLines) pages.add(readLines(pageLines));
                    }
                    h.setPages(pages, s.getInt("current-page", 0));
                }
                Object rawLines = s.get("lines");
                if (!(rawPages instanceof List<?>) && rawLines instanceof List<?> list) {
                    h.lines().addAll(readLines(list));
                } else {
                    if (!(rawPages instanceof List<?>)) for (String text : s.getStringList("lines")) h.lines().add(new HologramLine(text));
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
                h.setBrightness(s.getInt("brightness", -1));
                h.setLineWidth(s.getInt("line-width", 1000));
                h.setTranslationX(s.getDouble("translation-x", 0));
                h.setTranslationY(s.getDouble("translation-y", 0));
                h.setTranslationZ(s.getDouble("translation-z", 0));
                h.setRotationYaw((float) s.getDouble("rotation-yaw", 0));
                h.setRotationPitch((float) s.getDouble("rotation-pitch", 0));
                h.setVisibilityCondition(s.getString("visibility-condition", ""));
                h.setOnClickCommand(s.getString("on-click", ""));
                out.add(h);
            } catch (RuntimeException e) {
                plugin.getLogger().warning("Invalid data for hologram '" + name + "'; skipped: " + e.getMessage());
            }
        }
        return out;
    }

    private List<HologramLine> readLines(List<?> values) {
        List<HologramLine> lines = new ArrayList<>();
        for (Object value : values) {
            if (value instanceof String text) {
                lines.add(new HologramLine(text));
            } else if (value instanceof java.util.Map<?, ?> lineData) {
                Object textValue = lineData.get("text");
                HologramLine line = new HologramLine(textValue == null ? "" : String.valueOf(textValue));
                if (lineData.get("scale") instanceof Number scale) line.setScale(scale.floatValue());
                if (lineData.containsKey("animation")) line.setAnimation(String.valueOf(lineData.get("animation")));
                if (lineData.get("animation-speed") instanceof Number speed) line.setAnimationSpeed(speed.intValue());
                if (lineData.containsKey("paused")) line.setPaused(Boolean.parseBoolean(String.valueOf(lineData.get("paused"))));
                if (lineData.containsKey("on-click")) line.setOnClickCommand(String.valueOf(lineData.get("on-click")));
                lines.add(line);
            }
        }
        return lines;
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
            List<List<java.util.Map<String, Object>>> pageMaps = new ArrayList<>();
            for (List<HologramLine> page : h.pages()) pageMaps.add(writeLines(page));
            cfg.set(base + ".pages", pageMaps);
            cfg.set(base + ".current-page", h.currentPage());
            cfg.set(base + ".lines", writeLines(h.lines())); // legacy readers display the active page
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
            cfg.set(base + ".brightness", h.brightness());
            cfg.set(base + ".line-width", h.lineWidth());
            cfg.set(base + ".translation-x", h.translationX());
            cfg.set(base + ".translation-y", h.translationY());
            cfg.set(base + ".translation-z", h.translationZ());
            cfg.set(base + ".rotation-yaw", h.rotationYaw());
            cfg.set(base + ".rotation-pitch", h.rotationPitch());
            cfg.set(base + ".visibility-condition", h.visibilityCondition());
            cfg.set(base + ".on-click", h.onClickCommand());
        }
        File temporary = new File(file.getParentFile(), file.getName() + ".tmp");
        try {
            Files.createDirectories(file.toPath().getParent());
            cfg.save(temporary);
            try {
                Files.move(temporary.toPath(), file.toPath(), StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
            } catch (AtomicMoveNotSupportedException ignored) {
                Files.move(temporary.toPath(), file.toPath(), StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to save holograms to " + file + ": " + e.getMessage());
        }
    }

    private List<java.util.Map<String, Object>> writeLines(List<HologramLine> lines) {
        List<java.util.Map<String, Object>> maps = new ArrayList<>();
        for (HologramLine line : lines) {
            java.util.Map<String, Object> data = new java.util.LinkedHashMap<>();
            data.put("text", line.text());
            data.put("scale", line.scale());
            data.put("animation", line.animation());
            data.put("animation-speed", line.animationSpeed());
            data.put("paused", line.paused());
            data.put("on-click", line.onClickCommand());
            maps.add(data);
        }
        return maps;
    }
}

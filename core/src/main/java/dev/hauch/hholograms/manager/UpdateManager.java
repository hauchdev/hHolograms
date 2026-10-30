package dev.hauch.hholograms.manager;

import dev.hauch.hholograms.config.PluginConfig;
import dev.hauch.hholograms.model.Hologram;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;

public final class UpdateManager {
    private final Plugin plugin;
    private final PluginConfig config;
    private final HologramManager holograms;
    private BukkitTask task;
    private final Map<String, Integer> ticks = new HashMap<>();

    public UpdateManager(Plugin plugin, PluginConfig config, HologramManager holograms) {
        this.plugin = plugin;
        this.config = config;
        this.holograms = holograms;
    }

    public void start() {
        stop();
        task = Bukkit.getScheduler().runTaskTimer(plugin, this::tick, 20L, config.animationIntervalTicks());
    }

    public void stop() {
        if (task != null) { task.cancel(); task = null; }
    }

    private void tick() {
        for (Hologram h : new java.util.ArrayList<>(holograms.all())) {
            try {
                int t = ticks.getOrDefault(h.name().toLowerCase(), 0) + 1;
                ticks.put(h.name().toLowerCase(), t);
                if (h.paused()) continue;
                boolean doRefresh = (t % Math.max(1, config.refreshIntervalTicks() / Math.max(1, config.animationIntervalTicks())) == 0);
                java.util.List<String> base = new java.util.ArrayList<>(h.lines());
                if (doRefresh) base = applyPlaceholders(base);
                java.util.List<String> rendered = animate(h, base, t);
                holograms.renderer().updateText(h, rendered);
            } catch (Exception ignored) {}
        }
    }

    private java.util.List<String> applyPlaceholders(java.util.List<String> lines) {
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") == null) return lines;
        return lines.parallelStream().map(s -> {
            try {
                Class<?> cls = Class.forName("me.clip.placeholderapi.PlaceholderAPI");
                java.lang.reflect.Method m = cls.getMethod("setPlaceholders", org.bukkit.OfflinePlayer.class, String.class);
                return (String) m.invoke(null, null, s);
            } catch (Exception e) {
                return s;
            }
        }).collect(java.util.stream.Collectors.toList());
    }

    private java.util.List<String> animate(Hologram h, java.util.List<String> lines, int t) {
        String anim = h.animation().toLowerCase();
        java.util.List<String> out = new java.util.ArrayList<>();
        int speed = h.animationSpeed();
        for (String line : lines) {
            switch (anim) {
                case "scroll" -> {
                    if (line.length() <= 1) { out.add(line); break; }
                    int shift = (t / speed) % line.length();
                    out.add(line.substring(shift) + line.substring(0, shift));
                }
                case "typewriter" -> {
                    int len = Math.min(line.length(), (t / speed) % (line.length() + 5) + 1);
                    // avoid zero
                    if (len < 1) len = 1;
                    out.add(line.substring(0, Math.min(len, line.length())));
                }
                case "rainbow" -> out.add("<rainbow>" + line + "</rainbow>");
                case "pulse" -> {
                    if ((t / speed) % 2 == 0) out.add("<bold>" + line + "</bold>");
                    else out.add(line);
                }
                default -> out.add(line);
            }
        }
        return out;
    }
}
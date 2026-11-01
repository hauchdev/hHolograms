package dev.hauchdev.hholograms.manager;

import dev.hauchdev.hholograms.config.PluginConfig;
import dev.hauchdev.hholograms.model.Hologram;
import dev.hauchdev.hholograms.model.HologramLine;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;

/** Central, synchronous updater for hologram animations, placeholders, and per-player visibility. */
public final class UpdateManager {
    private final Plugin plugin;
    private final PluginConfig config;
    private final HologramManager holograms;
    private final Map<UUID, Map<UUID, Boolean>> visibility = new HashMap<>();
    private final Set<String> loggedFailures = new HashSet<>();
    private BukkitTask task;
    private int tick;
    private Method placeholderMethod;

    public UpdateManager(Plugin plugin, PluginConfig config, HologramManager holograms) {
        this.plugin = plugin;
        this.config = config;
        this.holograms = holograms;
    }

    public void start() {
        stop();
        tick = 0;
        task = Bukkit.getScheduler().runTaskTimer(plugin, this::tick, 20L, config.animationIntervalTicks());
    }

    public void stop() {
        if (task != null) {
            task.cancel();
            task = null;
        }
        visibility.clear();
        loggedFailures.clear();
    }

    private void tick() {
        tick += Math.max(1, config.animationIntervalTicks());
        boolean refreshPlaceholders = tick % Math.max(1, config.refreshIntervalTicks()) < config.animationIntervalTicks();
        Set<UUID> liveEntities = new HashSet<>();
        Set<UUID> onlinePlayers = Bukkit.getOnlinePlayers().stream().map(Player::getUniqueId).collect(java.util.stream.Collectors.toSet());
        for (Hologram hologram : holograms.all()) {
            try {
                List<HologramLine> lines = hologram.lines();
                boolean animated = !hologram.paused() && (hasAnimation(hologram, lines));
                if (refreshPlaceholders || animated) {
                    List<String> text = new ArrayList<>(lines.size());
                    for (HologramLine line : lines) text.add(line.text());
                    if (refreshPlaceholders) text = applyPlaceholders(text);
                    holograms.renderer().updateText(hologram, animateLines(hologram, lines, text));
                }
                liveEntities.addAll(hologram.entities());
                if (refreshPlaceholders) updateVisibility(hologram);
                loggedFailures.remove(hologram.name());
            } catch (RuntimeException e) {
                if (loggedFailures.add(hologram.name())) {
                    plugin.getLogger().log(Level.WARNING, "Could not update hologram '" + hologram.name() + "'. Further errors for this hologram are suppressed until it updates successfully.", e);
                }
            }
        }
        visibility.keySet().retainAll(liveEntities);
        for (Map<UUID, Boolean> viewers : visibility.values()) viewers.keySet().retainAll(onlinePlayers);
    }

    private boolean hasAnimation(Hologram hologram, List<HologramLine> lines) {
        if (!"none".equalsIgnoreCase(hologram.animation())) return true;
        return lines.stream().anyMatch(line -> !"none".equalsIgnoreCase(line.animation()));
    }

    private void updateVisibility(Hologram hologram) {
        String condition = hologram.visibilityCondition();
        for (Player player : Bukkit.getOnlinePlayers()) {
            boolean visible = evaluateVisibility(condition, player);
            UUID playerId = player.getUniqueId();
            for (UUID entityId : hologram.entities()) {
                Entity entity = Bukkit.getEntity(entityId);
                if (entity == null) continue;
                Map<UUID, Boolean> viewers = visibility.computeIfAbsent(entityId, ignored -> new HashMap<>());
                Boolean previous = viewers.put(playerId, visible);
                if (previous == null || previous != visible) {
                    if (visible) player.showEntity(plugin, entity);
                    else player.hideEntity(plugin, entity);
                }
            }
        }
    }

    private boolean evaluateVisibility(String condition, Player player) {
        if (condition == null || condition.isBlank()) return true;
        String result = condition;
        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            try {
                if (placeholderMethod == null) {
                    Class<?> api = Class.forName("me.clip.placeholderapi.PlaceholderAPI");
                    placeholderMethod = api.getMethod("setPlaceholders", org.bukkit.OfflinePlayer.class, String.class);
                }
                result = (String) placeholderMethod.invoke(null, player, condition);
            } catch (ReflectiveOperationException | LinkageError e) {
                if (loggedFailures.add("visibility-placeholder-api")) {
                    plugin.getLogger().log(Level.WARNING, "Could not resolve PlaceholderAPI visibility conditions; treating them as visible.", e);
                }
                return true;
            }
        }
        String normalized = result == null ? "" : result.trim().toLowerCase(Locale.ROOT);
        return !(normalized.isEmpty() || normalized.equals("false") || normalized.equals("no") || normalized.equals("0"));
    }

    private List<String> applyPlaceholders(List<String> lines) {
        if (!Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) return lines;
        try {
            if (placeholderMethod == null) {
                Class<?> api = Class.forName("me.clip.placeholderapi.PlaceholderAPI");
                placeholderMethod = api.getMethod("setPlaceholders", org.bukkit.OfflinePlayer.class, String.class);
            }
            List<String> resolved = new ArrayList<>(lines.size());
            for (String line : lines) {
                Object result = placeholderMethod.invoke(null, null, line);
                resolved.add(result instanceof String value ? value : line);
            }
            return resolved;
        } catch (ReflectiveOperationException | LinkageError e) {
            if (loggedFailures.add("text-placeholder-api")) {
                plugin.getLogger().log(Level.WARNING, "Could not resolve hologram text placeholders; displaying the configured text.", e);
            }
            return lines;
        }
    }

    private List<String> animateLines(Hologram hologram, List<HologramLine> lines, List<String> base) {
        List<String> output = new ArrayList<>(lines.size());
        for (int i = 0; i < lines.size(); i++) {
            HologramLine line = lines.get(i);
            String text = i < base.size() ? base.get(i) : "";
            String animation = "none".equalsIgnoreCase(line.animation()) ? hologram.animation() : line.animation();
            int speed = "none".equalsIgnoreCase(line.animation()) ? hologram.animationSpeed() : line.animationSpeed();
            if (hologram.paused() || line.paused() || text.indexOf('<') >= 0) {
                output.add(text);
                continue;
            }
            int frame = tick / Math.max(1, speed);
            switch (animation.toLowerCase(Locale.ROOT)) {
                case "scroll" -> {
                    if (text.isEmpty()) output.add(text);
                    else {
                        int shift = frame % text.length();
                        output.add(text.substring(shift) + text.substring(0, shift));
                    }
                }
                case "typewriter" -> {
                    if (text.isEmpty()) output.add(text);
                    else {
                        int length = Math.min(text.length(), frame % (text.length() + 1));
                        output.add(text.substring(0, length));
                    }
                }
                case "rainbow" -> output.add("<rainbow>" + text + "</rainbow>");
                case "pulse" -> output.add(frame % 2 == 0 ? "<bold>" + text + "</bold>" : text);
                case "wave" -> output.add(wave(text, frame));
                case "bounce" -> output.add(" ".repeat(frame % 6) + text);
                case "glitch" -> output.add(frame % 3 == 0 ? text.replace("a", "@").replace("e", "3").replace("i", "1") : text);
                case "fade" -> output.add(frame % 2 == 0 ? "<gray>" + text + "</gray>" : text);
                case "slide" -> {
                    if (text.isEmpty()) output.add(text);
                    else {
                        int shift = frame % text.length();
                        output.add(text.substring(text.length() - shift) + text.substring(0, text.length() - shift));
                    }
                }
                case "shake" -> output.add(frame % 2 == 0 ? " " + text : text + " ");
                default -> output.add(text);
            }
        }
        return output;
    }

    private String wave(String text, int frame) {
        StringBuilder output = new StringBuilder(text.length());
        for (int index = 0; index < text.length(); index++) {
            char character = text.charAt(index);
            if (Character.isLetter(character)) {
                output.append((frame + index) % 2 == 0 ? Character.toUpperCase(character) : Character.toLowerCase(character));
            } else {
                output.append(character);
            }
        }
        return output.toString();
    }
}

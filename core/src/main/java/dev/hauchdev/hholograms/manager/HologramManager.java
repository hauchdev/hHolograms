package dev.hauchdev.hholograms.manager;

import dev.hauchdev.hholograms.config.PluginConfig;
import dev.hauchdev.hholograms.model.Hologram;
import dev.hauchdev.hholograms.render.HologramRenderer;
import dev.hauchdev.hholograms.storage.HologramStorage;
import org.bukkit.Location;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class HologramManager {
    private final Plugin plugin;
    private final PluginConfig config;
    private final HologramStorage storage;
    private final HologramRenderer renderer = new HologramRenderer();
    public HologramRenderer renderer() { return renderer; }
    private final Map<String, Hologram> holograms = new LinkedHashMap<>();

    public HologramManager(Plugin plugin, PluginConfig config, HologramStorage storage) {
        this.plugin = plugin;
        this.config = config;
        this.storage = storage;
        for (Hologram h : storage.load()) {
            holograms.put(key(h.name()), h);
        }
        for (Hologram h : holograms.values()) renderer.render(h);
    }

    public Collection<Hologram> all() { return new ArrayList<>(holograms.values()); }
    public Hologram get(String name) { return name == null ? null : holograms.get(key(name)); }
    public boolean exists(String name) { return get(name) != null; }

    public Hologram create(String name, Location loc) {
        if (!validName(name) || !validLocation(loc) || exists(name)) return null;
        Hologram h = new Hologram(name, loc);
        h.setLineSpacing(config.defaultSpacing());
        h.setViewRange(config.defaultViewRange());
        h.setBillboard(config.defaultBillboard());
        holograms.put(key(name), h);
        renderer.render(h);
        save();
        return h;
    }

    public boolean delete(String name) {
        Hologram h = get(name);
        if (h == null) return false;
        renderer.remove(h);
        holograms.remove(key(name));
        save();
        return true;
    }

    public boolean rename(String oldName, String newName) {
        Hologram h = get(oldName);
        if (h == null || !validName(newName) || exists(newName)) return false;
        // The name is immutable, so create a replacement and copy its configuration.
        Hologram n = new Hologram(newName, h.location());
        copy(h, n);
        renderer.remove(h);
        holograms.remove(key(oldName));
        holograms.put(key(newName), n);
        renderer.render(n);
        save();
        return true;
    }

    public Hologram duplicate(String sourceName, String newName, Location loc) {
        Hologram src = get(sourceName);
        Location destination = loc == null && src != null ? src.location() : loc;
        if (src == null || !validName(newName) || !validLocation(destination) || exists(newName)) return null;
        Hologram n = new Hologram(newName, destination);
        copy(src, n);
        holograms.put(key(newName), n);
        renderer.render(n);
        save();
        return n;
    }

    public boolean move(String name, Location loc) {
        Hologram h = get(name);
        if (h == null || !validLocation(loc)) return false;
        h.setLocation(loc);
        renderer.render(h);
        save();
        return true;
    }

    private void copy(Hologram a, Hologram b) {
        b.setLines(a.lines());
        b.setLineSpacing(a.lineSpacing());
        b.setViewRange(a.viewRange());
        b.setBillboard(a.billboard());
        b.setSeeThrough(a.seeThrough());
        b.setShadowed(a.shadowed());
        b.setBackgroundColor(a.backgroundColor());
        b.setAnimation(a.animation());
        b.setAnimationSpeed(a.animationSpeed());
        b.setPaused(a.paused());
        b.setScale(a.scale());
        b.setAlignment(a.alignment());
        b.setType(a.type());
        b.setContent(a.content());
        b.setBrightness(a.brightness());
        b.setLineWidth(a.lineWidth());
        b.setTranslationX(a.translationX());
        b.setTranslationY(a.translationY());
        b.setTranslationZ(a.translationZ());
        b.setRotationYaw(a.rotationYaw());
        b.setRotationPitch(a.rotationPitch());
        b.setVisibilityCondition(a.visibilityCondition());
        b.setOnClickCommand(a.onClickCommand());
    }

    public void save() { storage.save(new ArrayList<>(holograms.values())); }

    public void reloadAll() {
        // remove current
        for (Hologram h : new ArrayList<>(holograms.values())) renderer.remove(h);
        holograms.clear();
        for (Hologram h : storage.load()) holograms.put(key(h.name()), h);
        for (Hologram h : holograms.values()) renderer.render(h);
    }

    public void shutdown() {
        for (Hologram h : new ArrayList<>(holograms.values())) renderer.remove(h);
        save();
    }

    private static String key(String name) {
        return name.toLowerCase(java.util.Locale.ROOT);
    }

    private static boolean validName(String name) {
        return name != null && name.matches("[A-Za-z0-9_-]{1,32}");
    }

    private static boolean validLocation(Location location) {
        return location != null && location.getWorld() != null
                && Double.isFinite(location.getX())
                && Double.isFinite(location.getY())
                && Double.isFinite(location.getZ())
                && Float.isFinite(location.getYaw())
                && Float.isFinite(location.getPitch());
    }
}

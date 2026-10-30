package dev.hauch.hholograms.manager;

import dev.hauch.hholograms.config.PluginConfig;
import dev.hauch.hholograms.model.Hologram;
import dev.hauch.hholograms.render.HologramRenderer;
import dev.hauch.hholograms.storage.HologramStorage;
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
            holograms.put(h.name().toLowerCase(), h);
        }
        for (Hologram h : holograms.values()) renderer.render(h);
    }

    public Collection<Hologram> all() { return new ArrayList<>(holograms.values()); }
    public Hologram get(String name) { return name == null ? null : holograms.get(name.toLowerCase()); }
    public boolean exists(String name) { return get(name) != null; }

    public Hologram create(String name, Location loc) {
        if (exists(name)) return null;
        Hologram h = new Hologram(name, loc);
        h.setLineSpacing(config.defaultSpacing());
        h.setViewRange(config.defaultViewRange());
        h.setBillboard(config.defaultBillboard());
        holograms.put(name.toLowerCase(), h);
        renderer.render(h);
        save();
        return h;
    }

    public boolean delete(String name) {
        Hologram h = get(name);
        if (h == null) return false;
        renderer.remove(h);
        holograms.remove(name.toLowerCase());
        save();
        return true;
    }

    public boolean rename(String oldName, String newName) {
        Hologram h = get(oldName);
        if (h == null || exists(newName)) return false;
        // recreate with new name by reflection-ish: create new and copy
        Hologram n = new Hologram(newName, h.location());
        copy(h, n);
        renderer.remove(h);
        holograms.remove(oldName.toLowerCase());
        holograms.put(newName.toLowerCase(), n);
        renderer.render(n);
        save();
        return true;
    }

    public Hologram duplicate(String sourceName, String newName, Location loc) {
        Hologram src = get(sourceName);
        if (src == null || exists(newName)) return null;
        Hologram n = new Hologram(newName, loc == null ? src.location() : loc);
        copy(src, n);
        holograms.put(newName.toLowerCase(), n);
        renderer.render(n);
        save();
        return n;
    }

    public boolean move(String name, Location loc) {
        Hologram h = get(name);
        if (h == null) return false;
        h.setLocation(loc);
        renderer.render(h);
        save();
        return true;
    }

    private void copy(Hologram a, Hologram b) {
        b.setLines(new ArrayList<>(a.lines()));
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
    }

    public void save() { storage.save(new ArrayList<>(holograms.values())); }

    public void reloadAll() {
        // remove current
        for (Hologram h : new ArrayList<>(holograms.values())) renderer.remove(h);
        holograms.clear();
        for (Hologram h : storage.load()) holograms.put(h.name().toLowerCase(), h);
        for (Hologram h : holograms.values()) renderer.render(h);
    }

    public void shutdown() {
        for (Hologram h : new ArrayList<>(holograms.values())) renderer.remove(h);
        save();
    }
}
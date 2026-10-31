package dev.hauchdev.hholograms.api;

import dev.hauchdev.hholograms.bootstrap.Bootstrap;
import dev.hauchdev.hholograms.model.Hologram;

import java.util.Collection;
import java.util.Locale;

/**
 * Public API for hHolograms.
 * All methods must be called on the main server thread.
 */
public final class HologramsAPI {
    private HologramsAPI() {}

    public static Collection<Hologram> all() {
        return Bootstrap.context().holograms.all();
    }

    public static Hologram get(String name) {
        return name == null ? null : Bootstrap.context().holograms.get(name);
    }

    public static boolean exists(String name) {
        return name != null && Bootstrap.context().holograms.exists(name);
    }

    public static Hologram create(String name, org.bukkit.Location loc) {
        return Bootstrap.context().holograms.create(name, loc);
    }

    public static boolean delete(String name) {
        return Bootstrap.context().holograms.delete(name);
    }

    public static void save() {
        Bootstrap.context().holograms.save();
    }

    public static void reload() {
        Bootstrap.reload();
    }
}
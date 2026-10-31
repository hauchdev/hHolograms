package dev.hauchdev.hholograms.bootstrap;

import dev.hauchdev.hholograms.api.platform.Platform;
import dev.hauchdev.hholograms.api.platform.PlatformAdapter;
import dev.hauchdev.hholograms.exception.PlatformLoadException;
import org.bukkit.Bukkit;

import java.util.Map;
import java.util.logging.Logger;

public final class VersionLoader {
    public static final String MODERN_ALIAS = "v1_21_R3";
    private static final Map<String, String> REVISION_TABLE = Map.ofEntries(
        Map.entry("1.18.2", "v1_18_R2"),
        Map.entry("1.19.1", "v1_19_R1"), Map.entry("1.19.2", "v1_19_R1"),
        Map.entry("1.19.3", "v1_19_R2"),
        Map.entry("1.19.4", "v1_19_R3"),
        Map.entry("1.20.1", "v1_20_R1"), Map.entry("1.20.2", "v1_20_R2"),
        Map.entry("1.20.3", "v1_20_R3"), Map.entry("1.20.4", "v1_20_R3"),
        Map.entry("1.20.5", "v1_20_R4"), Map.entry("1.20.6", "v1_20_R4"),
        Map.entry("1.21.1", "v1_21_R1"), Map.entry("1.21.2", "v1_21_R1"),
        Map.entry("1.21.3", "v1_21_R1"), Map.entry("1.21.4", "v1_21_R1"),
        Map.entry("1.21.5", "v1_21_R2"),
        Map.entry("1.21.6", "v1_21_R3"), Map.entry("1.21.7", "v1_21_R3"),
        Map.entry("1.21.8", "v1_21_R3")
    );
    private static final Map<String, String> LATEST_FOR_PREFIX = Map.of(
        "1.18", "v1_18_R2", "1.19", "v1_19_R3",
        "1.20", "v1_20_R4", "1.21", "v1_21_R3"
    );

    public static PlatformAdapter bootstrap(Logger logger) {
        String revision = detectViaPackage();
        if (revision == null) revision = detectViaMinecraftVersion(logger);
        if (revision == null) throw new PlatformLoadException("Could not determine Minecraft version.");
        String fqn = "dev.hauchdev.hholograms." + revision + ".PlatformAdapter_" + revision;
        logger.info("[VersionLoader] Loading PlatformAdapter: " + fqn);
        PlatformAdapter adapter = instantiate(fqn, logger);
        Platform.set(adapter);
        return adapter;
    }

    private static String detectViaPackage() {
        try {
            String pkg = Bukkit.getServer().getClass().getPackage().getName();
            int idx = pkg.lastIndexOf(".v");
            if (idx < 0) return null;
            return pkg.substring(idx + 1).trim();
        } catch (Exception ignored) { return null; }
    }

    private static String detectViaMinecraftVersion(Logger logger) {
        try {
            String mc = Bukkit.getMinecraftVersion();
            if (mc == null || mc.isBlank()) return null;
            String exact = REVISION_TABLE.get(mc);
            if (exact != null) return exact;
            String majorMinor = mc.length() >= 4 ? mc.substring(0, 4) : mc;
            String byPrefix = REVISION_TABLE.get(majorMinor);
            if (byPrefix != null) return byPrefix;
            String latest = LATEST_FOR_PREFIX.get(majorMinor);
            if (latest != null) return latest;
            if (mc.startsWith("2") || mc.startsWith("3")) return MODERN_ALIAS;
            return null;
        } catch (RuntimeException ignored) { return null; }
    }

    private static PlatformAdapter instantiate(String fqn, Logger logger) {
        try {
            Class<?> clazz = Class.forName(fqn);
            Object instance = clazz.getDeclaredConstructor().newInstance();
            if (!(instance instanceof PlatformAdapter adapter)) {
                throw new PlatformLoadException("Class " + fqn + " is not a PlatformAdapter");
            }
            return adapter;
        } catch (PlatformLoadException e) { throw e; }
        catch (ClassNotFoundException e) { throw new PlatformLoadException("No PlatformAdapter shipped: " + fqn, e); }
        catch (Throwable t) { throw new PlatformLoadException("Failed to load PlatformAdapter: " + fqn, t); }
    }

    private VersionLoader() {}
}
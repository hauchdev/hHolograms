package dev.hauch.hholograms.api.platform;

public final class Platform {
    private static volatile PlatformAdapter adapter;
    private Platform() {}
    public static PlatformAdapter get() {
        if (adapter == null) throw new IllegalStateException("Platform adapter not loaded");
        return adapter;
    }
    public static void set(PlatformAdapter a) { adapter = a; }
    public static String id() { return get().getId(); }
    public static void reset() { adapter = null; }
}
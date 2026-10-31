package dev.hauchdev.hholograms;

import dev.hauchdev.hholograms.bootstrap.Bootstrap;
import org.bukkit.plugin.java.JavaPlugin;

public final class HHolograms extends JavaPlugin {

    @Override
    public void onEnable() {
        Bootstrap.initialize(this);
        getLogger().info("hHolograms enabled.");
    }

    @Override
    public void onDisable() {
        Bootstrap.shutdown();
        getLogger().info("hHolograms disabled.");
    }

    public void reloadPlugin() {
        Bootstrap.reload();
    }
}
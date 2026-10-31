package dev.hauchdev.hholograms.bootstrap;

import dev.hauchdev.hholograms.command.HHologramsCommand;
import dev.hauchdev.hholograms.api.platform.PlatformAdapter;
import dev.hauchdev.hholograms.config.PluginConfig;
import dev.hauchdev.hholograms.config.PluginMessages;
import dev.hauchdev.hholograms.manager.HologramManager;
import dev.hauchdev.hholograms.manager.UpdateManager;
import dev.hauchdev.hholograms.storage.HologramStorage;
import org.bukkit.plugin.Plugin;

public final class PluginContext {
    public final Plugin plugin;
    public final PlatformAdapter platformAdapter;
    public final PluginConfig config;
    public final PluginMessages messages;
    public final HologramStorage storage;
    public final HologramManager holograms;
    public final UpdateManager updateManager;
    public final HHologramsCommand command;

    public PluginContext(Plugin plugin, PlatformAdapter platformAdapter, PluginConfig config, PluginMessages messages,
                         HologramStorage storage, HologramManager holograms,
                         UpdateManager updateManager, HHologramsCommand command) {
        this.plugin = plugin;
        this.platformAdapter = platformAdapter;
        this.config = config;
        this.messages = messages;
        this.storage = storage;
        this.holograms = holograms;
        this.updateManager = updateManager;
        this.command = command;
    }
}
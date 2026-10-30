package dev.hauch.hholograms.bootstrap;

import dev.hauch.hholograms.command.HHologramsCommand;
import dev.hauch.hholograms.config.PluginConfig;
import dev.hauch.hholograms.config.PluginMessages;
import dev.hauch.hholograms.manager.HologramManager;
import dev.hauch.hholograms.manager.UpdateManager;
import dev.hauch.hholograms.storage.HologramStorage;
import org.bukkit.plugin.Plugin;

public final class PluginContext {
    public final Plugin plugin;
    public final PluginConfig config;
    public final PluginMessages messages;
    public final HologramStorage storage;
    public final HologramManager holograms;
    public final UpdateManager updateManager;
    public final HHologramsCommand command;

    public PluginContext(Plugin plugin, PluginConfig config, PluginMessages messages,
                         HologramStorage storage, HologramManager holograms,
                         UpdateManager updateManager, HHologramsCommand command) {
        this.plugin = plugin;
        this.config = config;
        this.messages = messages;
        this.storage = storage;
        this.holograms = holograms;
        this.updateManager = updateManager;
        this.command = command;
    }
}
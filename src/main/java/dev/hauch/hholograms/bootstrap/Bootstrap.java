package dev.hauch.hholograms.bootstrap;

import dev.hauch.hholograms.HHolograms;
import dev.hauch.hholograms.command.HHologramsCommand;
import dev.hauch.hholograms.config.PluginConfig;
import dev.hauch.hholograms.config.PluginMessages;
import dev.hauch.hholograms.manager.HologramManager;
import dev.hauch.hholograms.manager.UpdateManager;
import dev.hauch.hholograms.storage.HologramStorage;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.util.logging.Logger;

public final class Bootstrap {
    private static volatile PluginContext context;
    private static final Logger LOG = Bukkit.getLogger();

    private Bootstrap() {}

    public static synchronized void initialize(Plugin plugin) {
        if (context != null) { LOG.warning("initialize twice"); return; }
        plugin.saveDefaultConfig();
        plugin.saveResource("lang/lang_en.yml", false);
        PluginConfig config = new PluginConfig(plugin);
        PluginMessages messages = new PluginMessages(plugin);
        HologramStorage storage = new HologramStorage(plugin);
        HologramManager manager = new HologramManager(plugin, config, storage);
        UpdateManager updateManager = new UpdateManager(plugin, config, manager);
        HHologramsCommand command = new HHologramsCommand(plugin, config, messages, manager, updateManager);
        context = new PluginContext(plugin, config, messages, storage, manager, updateManager, command);
        updateManager.start();
        // bind command
        org.bukkit.command.PluginCommand cmd = ((org.bukkit.plugin.java.JavaPlugin) plugin).getCommand("hholograms");
        if (cmd != null) {
            cmd.setExecutor(command);
            cmd.setTabCompleter(command);
        }
        org.bukkit.command.PluginCommand alias = ((org.bukkit.plugin.java.JavaPlugin) plugin).getCommand("hh");
        if (alias != null) {
            alias.setExecutor(command);
            alias.setTabCompleter(command);
        }
        LOG.info("hHolograms ready.");
    }

    public static synchronized void reload() {
        PluginContext ctx = context;
        if (ctx == null) return;
        ctx.config.reloadConfig();
        ctx.messages.reload();
        ctx.updateManager.stop();
        ctx.holograms.reloadAll();
        ctx.updateManager.start();
        LOG.info("hHolograms reloaded.");
    }

    public static synchronized void shutdown() {
        PluginContext ctx = context;
        if (ctx == null) return;
        try {
            ctx.updateManager.stop();
            ctx.holograms.shutdown();
        } catch (Exception e) {
            LOG.warning("shutdown: " + e.getMessage());
        } finally {
            context = null;
        }
    }

    public static PluginContext context() {
        PluginContext c = context;
        if (c == null) throw new IllegalStateException("not initialized");
        return c;
    }
}
package dev.hauchdev.hholograms.api.platform;

import org.bukkit.command.CommandSender;

public interface PlatformAdapter {
    String getId();
    String getDisplayName();
    void sendMessage(CommandSender sender, String message);
}
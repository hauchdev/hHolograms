package dev.hauchdev.hholograms.v1_19_R3;

import dev.hauchdev.hholograms.api.platform.PlatformAdapter;
import org.bukkit.command.CommandSender;

public final class PlatformAdapter_v1_19_R3 implements PlatformAdapter {
    @Override
    public String getId() { return "v1_19_R3"; }

    @Override
    public String getDisplayName() { return "v1_19_R3"; }

    @Override
    public void sendMessage(CommandSender sender, String message) {
        sender.sendMessage(message);
    }
}

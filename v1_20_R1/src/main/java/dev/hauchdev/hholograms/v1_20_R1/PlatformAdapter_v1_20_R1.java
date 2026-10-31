package dev.hauchdev.hholograms.v1_20_R1;

import dev.hauchdev.hholograms.api.platform.PlatformAdapter;
import org.bukkit.command.CommandSender;

public final class PlatformAdapter_v1_20_R1 implements PlatformAdapter {
    @Override
    public String getId() { return "v1_20_R1"; }

    @Override
    public String getDisplayName() { return "v1_20_R1"; }

    @Override
    public void sendMessage(CommandSender sender, String message) {
        sender.sendMessage(message);
    }
}

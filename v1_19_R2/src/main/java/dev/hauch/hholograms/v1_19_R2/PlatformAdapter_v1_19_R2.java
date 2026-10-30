package dev.hauch.hholograms.v1_19_R2;

import dev.hauch.hholograms.api.platform.PlatformAdapter;
import org.bukkit.command.CommandSender;

public final class PlatformAdapter_v1_19_R2 implements PlatformAdapter {
    @Override
    public String getId() { return "v1_19_R2"; }

    @Override
    public String getDisplayName() { return "v1_19_R2"; }

    @Override
    public void sendMessage(CommandSender sender, String message) {
        sender.sendMessage(message);
    }
}

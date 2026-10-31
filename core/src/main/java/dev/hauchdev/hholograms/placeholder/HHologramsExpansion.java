package dev.hauchdev.hholograms.placeholder;

import dev.hauchdev.hholograms.bootstrap.Bootstrap;
import dev.hauchdev.hholograms.manager.HologramManager;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;

public final class HHologramsExpansion extends PlaceholderExpansion {
    @Override
    public String getIdentifier() { return "hholograms"; }

    @Override
    public String getAuthor() { return "Hauchdev"; }

    @Override
    public String getVersion() { return "1.0.0"; }

    @Override
    public boolean persist() { return true; }

    @Override
    public String onRequest(OfflinePlayer player, String params) {
        HologramManager m = Bootstrap.context().holograms;
        if (params.equalsIgnoreCase("count")) return String.valueOf(m.all().size());
        if (params.toLowerCase().startsWith("exists_")) {
            String name = params.substring(7);
            return m.exists(name) ? "true" : "false";
        }
        return null;
    }
}
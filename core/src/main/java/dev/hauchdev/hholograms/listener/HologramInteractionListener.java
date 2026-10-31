package dev.hauchdev.hholograms.listener;

import dev.hauchdev.hholograms.bootstrap.Bootstrap;
import dev.hauchdev.hholograms.model.Hologram;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;

public final class HologramInteractionListener implements Listener {
    @EventHandler
    public void onInteract(PlayerInteractEntityEvent e) {
        Player p = e.getPlayer();
        for (Hologram h : Bootstrap.context().holograms.all()) {
            if (!h.entities().contains(e.getRightClicked().getUniqueId())) continue;
            String cmd = h.onClickCommand();
            if (cmd == null || cmd.isBlank()) return;
            if (!p.hasPermission("hholograms.interact")) return;
            String toRun = cmd.replace("{player}", p.getName());
            if (toRun.startsWith("/")) toRun = toRun.substring(1);
            Bukkit.dispatchCommand(p, toRun);
            e.setCancelled(true);
            return;
        }
    }
}
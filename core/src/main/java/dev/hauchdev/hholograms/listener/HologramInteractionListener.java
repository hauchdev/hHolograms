package dev.hauchdev.hholograms.listener;

import dev.hauchdev.hholograms.bootstrap.Bootstrap;
import dev.hauchdev.hholograms.model.Hologram;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class HologramInteractionListener implements Listener {
    private static final long CLICK_COOLDOWN_MILLIS = 500L;
    private final Map<UUID, Long> lastClick = new HashMap<>();

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        lastClick.remove(e.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onInteract(PlayerInteractEntityEvent e) {
        if (e.getHand() != EquipmentSlot.HAND) return;
        Player p = e.getPlayer();
        for (Hologram h : Bootstrap.context().holograms.all()) {
            if (!h.entities().contains(e.getRightClicked().getUniqueId())) continue;
            String cmd = h.onClickCommand();
            if (cmd == null || cmd.isBlank()) return;
            if (!p.hasPermission("hholograms.interact")) return;
            long now = System.currentTimeMillis();
            Long previous = lastClick.put(p.getUniqueId(), now);
            if (previous != null && now - previous < CLICK_COOLDOWN_MILLIS) return;
            String toRun = cmd.replace("{player}", p.getName());
            if (toRun.startsWith("/")) toRun = toRun.substring(1);
            Bukkit.dispatchCommand(p, toRun);
            e.setCancelled(true);
            return;
        }
    }
}

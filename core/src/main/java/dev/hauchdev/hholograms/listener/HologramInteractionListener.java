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
            Integer lineIndex = h.interactionLine(e.getRightClicked().getUniqueId());
            String cmd = lineIndex == null ? h.onClickCommand()
                    : lineIndex < h.lines().size() ? h.lines().get(lineIndex).onClickCommand() : "";
            if (cmd == null || cmd.isBlank()) return;
            e.setCancelled(true);
            if (!p.hasPermission("hholograms.interact")) return;
            String toRun = cmd.replace("{player}", p.getName())
                    .replace("{uuid}", p.getUniqueId().toString())
                    .replace("{world}", p.getWorld().getName())
                    .replace("{x}", String.format(java.util.Locale.ROOT, "%.2f", p.getLocation().getX()))
                    .replace("{y}", String.format(java.util.Locale.ROOT, "%.2f", p.getLocation().getY()))
                    .replace("{z}", String.format(java.util.Locale.ROOT, "%.2f", p.getLocation().getZ()));
            if (toRun.regionMatches(true, 0, "console:", 0, "console:".length())) {
                if (!p.hasPermission("hholograms.interact.console")) return;
                if (isCoolingDown(p)) return;
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), toRun.substring("console:".length()).trim());
                return;
            }
            if (isCoolingDown(p)) return;
            if (toRun.startsWith("/")) toRun = toRun.substring(1);
            Bukkit.dispatchCommand(p, toRun);
            return;
        }
    }

    private boolean isCoolingDown(Player player) {
        long now = System.currentTimeMillis();
        Long previous = lastClick.get(player.getUniqueId());
        if (previous != null && now - previous < CLICK_COOLDOWN_MILLIS) return true;
        lastClick.put(player.getUniqueId(), now);
        return false;
    }
}

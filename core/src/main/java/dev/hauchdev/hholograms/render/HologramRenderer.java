package dev.hauchdev.hholograms.render;

import dev.hauchdev.hholograms.model.Hologram;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.entity.Display;
import org.bukkit.entity.Entity;
import org.bukkit.entity.TextDisplay;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.util.Transformation;
import org.joml.AxisAngle4f;
import org.joml.Vector3f;
import java.util.List;

public final class HologramRenderer {
    private static final MiniMessage MM = MiniMessage.miniMessage();

    public void render(Hologram h) {
        remove(h);
        Location base = h.location();
        String type = h.type();
        if ("ITEM".equalsIgnoreCase(type)) {
            Location loc = base.clone();
            ItemDisplay id = base.getWorld().spawn(loc, ItemDisplay.class);
            try {
                org.bukkit.Material m = org.bukkit.Material.valueOf(h.content().toUpperCase());
                id.setItemStack(new org.bukkit.inventory.ItemStack(m));
            } catch (Exception ignored) {}
            id.setBillboard(parseBillboard(h.billboard()));
            id.setViewRange((float) h.viewRange());
            try { id.setTransformation(new Transformation(new Vector3f(0,0,0), new AxisAngle4f(), new Vector3f(h.scale(), h.scale(), h.scale()), new AxisAngle4f())); } catch (Throwable ignored) {}
            h.entities().add(id.getUniqueId());
            return;
        }
        if ("BLOCK".equalsIgnoreCase(type)) {
            Location loc = base.clone();
            BlockDisplay bd = base.getWorld().spawn(loc, BlockDisplay.class);
            try { bd.setBlock(org.bukkit.Bukkit.createBlockData(h.content().isEmpty() ? "STONE" : h.content())); } catch (Exception ignored) {}
            bd.setBillboard(parseBillboard(h.billboard()));
            bd.setViewRange((float) h.viewRange());
            try { bd.setTransformation(new Transformation(new Vector3f(0,0,0), new AxisAngle4f(), new Vector3f(h.scale(), h.scale(), h.scale()), new AxisAngle4f())); } catch (Throwable ignored) {}
            h.entities().add(bd.getUniqueId());
            return;
        }
        int n = h.lines().size();
        for (int i = 0; i < n; i++) {
            String raw = h.lines().get(i);
            Location loc = base.clone().add(0, -i * h.lineSpacing(), 0);
            TextDisplay td = base.getWorld().spawn(loc, TextDisplay.class);
            td.text(MM.deserialize(raw));
            td.setBillboard(parseBillboard(h.billboard()));
            td.setSeeThrough(h.seeThrough());
            td.setShadowed(h.shadowed());
            td.setViewRange((float) h.viewRange());
            try { td.setAlignment(TextDisplay.TextAlignment.valueOf(h.alignment())); } catch (Exception ignored) { td.setAlignment(TextDisplay.TextAlignment.CENTER); }
            try { td.setTransformation(new Transformation(new Vector3f(0,0,0), new AxisAngle4f(), new Vector3f(h.scale(), h.scale(), h.scale()), new AxisAngle4f())); } catch (Throwable ignored) {}
            try { td.setLineWidth(1000); } catch (Throwable ignored) {}
            applyBackground(td, h.backgroundColor());
            h.entities().add(td.getUniqueId());
        }
    }

    public void updateText(Hologram h, List<String> rendered) {
        int i = 0;
        for (java.util.UUID id : h.entities()) {
            org.bukkit.entity.Entity e = org.bukkit.Bukkit.getEntity(id);
            if (e instanceof TextDisplay td) {
                String r = (i < rendered.size()) ? rendered.get(i) : "";
                td.text(MiniMessage.miniMessage().deserialize(r));
            }
            i++;
        }
    }

    public void remove(Hologram h) {
        for (java.util.UUID id : h.entities()) {
            Entity e = Bukkit.getEntity(id);
            if (e != null) e.remove();
        }
        h.clearEntities();
    }

    private Display.Billboard parseBillboard(String s) {
        if (s == null) return Display.Billboard.CENTER;
        try { return Display.Billboard.valueOf(s.toUpperCase()); }
        catch (Exception e) { return Display.Billboard.CENTER; }
    }

    private void applyBackground(TextDisplay td, String bg) {
        if (bg == null || bg.equalsIgnoreCase("transparent") || bg.equalsIgnoreCase("none")) {
            td.setDefaultBackground(false);
            try { td.setBackgroundColor(null); } catch (Throwable ignored) {}
            return;
        }
        Color c = parseColor(bg);
        if (c == null) {
            td.setDefaultBackground(false);
            return;
        }
        td.setDefaultBackground(false);
        td.setBackgroundColor(c);
    }

    private Color parseColor(String s) {
        s = s.trim();
        if (s.startsWith("#")) s = s.substring(1);
        // support argb (8 hex) or rgb (6 hex)
        try {
            if (s.length() == 8) {
                int a = Integer.parseInt(s.substring(0, 2), 16);
                int r = Integer.parseInt(s.substring(2, 4), 16);
                int g = Integer.parseInt(s.substring(4, 6), 16);
                int b = Integer.parseInt(s.substring(6, 8), 16);
                return Color.fromARGB(a, r, g, b);
            }
            if (s.length() == 6) {
                int r = Integer.parseInt(s.substring(0, 2), 16);
                int g = Integer.parseInt(s.substring(2, 4), 16);
                int b = Integer.parseInt(s.substring(4, 6), 16);
                return Color.fromRGB(r, g, b);
            }
        } catch (Exception ignored) {}
        return null;
    }
}
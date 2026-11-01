package dev.hauchdev.hholograms.render;

import dev.hauchdev.hholograms.model.Hologram;
import net.kyori.adventure.text.Component;
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
        if ("ITEM".equalsIgnoreCase(type) || "HEAD".equalsIgnoreCase(type)) {
            Location loc = base.clone();
            org.bukkit.Material material = "HEAD".equalsIgnoreCase(type)
                    ? org.bukkit.Material.PLAYER_HEAD : org.bukkit.Material.matchMaterial(h.content());
            if (material == null || material.isAir()) {
                Bukkit.getLogger().warning("[hHolograms] Hologram '" + h.name() + "' has an invalid item material '" + h.content() + "'; it was not rendered.");
                return;
            }
            ItemDisplay id = base.getWorld().spawn(loc, ItemDisplay.class);
            org.bukkit.inventory.ItemStack stack = new org.bukkit.inventory.ItemStack(material);
            if ("HEAD".equalsIgnoreCase(type) && !h.content().isBlank()) {
                org.bukkit.inventory.meta.SkullMeta skull = (org.bukkit.inventory.meta.SkullMeta) stack.getItemMeta();
                skull.setOwningPlayer(Bukkit.getOfflinePlayer(h.content()));
                stack.setItemMeta(skull);
            }
            id.setItemStack(stack);
            id.setBillboard(parseBillboard(h.billboard()));
            id.setViewRange((float) h.viewRange());
            applyBrightness(id, h.brightness());
            Transformation itr = new Transformation(
                new Vector3f((float)h.translationX(), (float)h.translationY(), (float)h.translationZ()),
                new AxisAngle4f((float)Math.toRadians(h.rotationYaw()), 0,1,0),
                new Vector3f(h.scale(),h.scale(),h.scale()),
                new AxisAngle4f((float)Math.toRadians(h.rotationPitch()), 1,0,0));
            id.setTransformation(itr);
            h.entities().add(id.getUniqueId());
            return;
        }
        if ("BLOCK".equalsIgnoreCase(type)) {
            Location loc = base.clone();
            org.bukkit.block.data.BlockData blockData;
            try {
                blockData = Bukkit.createBlockData(h.content().isEmpty() ? "STONE" : h.content());
            } catch (IllegalArgumentException e) {
                Bukkit.getLogger().warning("[hHolograms] Hologram '" + h.name() + "' has invalid block data '" + h.content() + "'; it was not rendered.");
                return;
            }
            BlockDisplay bd = base.getWorld().spawn(loc, BlockDisplay.class);
            bd.setBlock(blockData);
            bd.setBillboard(parseBillboard(h.billboard()));
            bd.setViewRange((float) h.viewRange());
            applyBrightness(bd, h.brightness());
            Transformation btr = new Transformation(
                new Vector3f((float)h.translationX(), (float)h.translationY(), (float)h.translationZ()),
                new AxisAngle4f((float)Math.toRadians(h.rotationYaw()), 0,1,0),
                new Vector3f(h.scale(),h.scale(),h.scale()),
                new AxisAngle4f((float)Math.toRadians(h.rotationPitch()), 1,0,0));
            bd.setTransformation(btr);
            h.entities().add(bd.getUniqueId());
            return;
        }
        int n = h.lines().size();
        for (int i = 0; i < n; i++) {
            dev.hauchdev.hholograms.model.HologramLine hl = h.lines().get(i);
            String raw = hl.text();
            Location loc = base.clone().add(0, -i * h.lineSpacing(), 0);
            TextDisplay td = base.getWorld().spawn(loc, TextDisplay.class);
            td.text(deserialize(h.name(), raw));
            td.setBillboard(parseBillboard(h.billboard()));
            td.setSeeThrough(h.seeThrough());
            td.setShadowed(h.shadowed());
            td.setViewRange((float) h.viewRange());
            try { td.setAlignment(TextDisplay.TextAlignment.valueOf(h.alignment())); }
            catch (IllegalArgumentException e) {
                Bukkit.getLogger().warning("[hHolograms] Invalid alignment '" + h.alignment() + "' on hologram '" + h.name() + "'; using CENTER.");
                td.setAlignment(TextDisplay.TextAlignment.CENTER);
            }
            float ls = hl.scale();
            applyBrightness(td, h.brightness());
            float s = h.scale()*ls;
            Transformation tr = new Transformation(
                new Vector3f((float)h.translationX(), (float)h.translationY(), (float)h.translationZ()),
                new AxisAngle4f((float)Math.toRadians(h.rotationYaw()), 0,1,0),
                new Vector3f(s,s,s),
                new AxisAngle4f((float)Math.toRadians(h.rotationPitch()), 1,0,0));
            td.setTransformation(tr);
            td.setLineWidth(h.lineWidth());
            applyBackground(td, h.backgroundColor());
            h.entities().add(td.getUniqueId());
            if (!hl.onClickCommand().isBlank() || !h.onClickCommand().isBlank()) {
                float width = Math.max(0.6f, Math.min(8.0f, raw.codePointCount(0, raw.length()) * 0.12f * s));
                float height = Math.max(0.35f, Math.min(2.0f, 0.32f * s));
                Location hitboxLocation = loc.clone();
                org.bukkit.entity.Interaction interaction = base.getWorld().spawn(hitboxLocation, org.bukkit.entity.Interaction.class);
                interaction.setInteractionWidth(width);
                interaction.setInteractionHeight(height);
                interaction.setResponsive(true);
                interaction.setGravity(false);
                interaction.setInvulnerable(true);
                interaction.setPersistent(false);
                h.entities().add(interaction.getUniqueId());
                if (!hl.onClickCommand().isBlank()) h.registerLineInteraction(interaction.getUniqueId(), i);
            }
        }
    }

    public void updateText(Hologram h, List<String> rendered) {
        int i = 0;
        for (java.util.UUID id : h.entities()) {
            org.bukkit.entity.Entity e = org.bukkit.Bukkit.getEntity(id);
            if (e instanceof TextDisplay td) {
                String r = (i < rendered.size()) ? rendered.get(i) : "";
                td.text(deserialize(h.name(), r));
                i++;
            }
        }
    }

    public void remove(Hologram h) {
        for (java.util.UUID id : h.entities()) {
            Entity e = Bukkit.getEntity(id);
            if (e != null) e.remove();
        }
        h.clearEntities();
    }

    private void applyBrightness(org.bukkit.entity.Display d, int b) {
        if (b < 0) return;
        int v = Math.max(0, Math.min(15, b));
        d.setBrightness(new org.bukkit.entity.Display.Brightness(v, v));
    }

    private Display.Billboard parseBillboard(String s) {
        if (s == null) return Display.Billboard.CENTER;
        try { return Display.Billboard.valueOf(s.toUpperCase(java.util.Locale.ROOT)); }
        catch (IllegalArgumentException e) {
            Bukkit.getLogger().warning("[hHolograms] Invalid billboard mode '" + s + "'; using CENTER.");
            return Display.Billboard.CENTER;
        }
    }

    private void applyBackground(TextDisplay td, String bg) {
        if (bg == null || bg.equalsIgnoreCase("transparent") || bg.equalsIgnoreCase("none") || bg.equalsIgnoreCase("remove")) {
            td.setDefaultBackground(false);
            td.setBackgroundColor(org.bukkit.Color.fromARGB(0, 0, 0, 0));
            return;
        }
        Color c = parseColor(bg);
        if (c == null) {
            td.setDefaultBackground(false);
            td.setBackgroundColor(Color.fromARGB(0, 0, 0, 0));
            Bukkit.getLogger().warning("[hHolograms] Invalid text background '" + bg + "'; using transparent background.");
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

    private Component deserialize(String hologramName, String text) {
        try {
            return MM.deserialize(text);
        } catch (RuntimeException e) {
            Bukkit.getLogger().warning("[hHolograms] Invalid MiniMessage in hologram '" + hologramName + "'; displaying it as plain text.");
            return Component.text(text);
        }
    }
}

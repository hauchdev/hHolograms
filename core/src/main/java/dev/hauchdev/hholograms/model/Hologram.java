package dev.hauchdev.hholograms.model;

import org.bukkit.Location;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class Hologram {
    private final String name;
    private Location location;
    private final List<List<HologramLine>> pages = new ArrayList<>();
    private int currentPage;
    private double lineSpacing;
    private int viewRange;
    private String billboard; // CENTER, VERTICAL, HORIZONTAL, FIXED
    private boolean seeThrough;
    private boolean shadowed;
    private String backgroundColor; // hex/argb or "transparent"
    private String animation; // none, scroll, typewriter, rainbow
    private int animationSpeed;
    private boolean paused;
    private float scale;
    private String alignment; // LEFT, CENTER, RIGHT
    private String type; // TEXT, ITEM, BLOCK
    private String content; // material name or item
    private int brightness; // 0-15 (block & sky)
    private int lineWidth;
    private double translationX;
    private double translationY;
    private double translationZ;
    private float rotationYaw;
    private float rotationPitch;
    private String visibilityCondition; // placeholder expression, empty = always
    private String onClickCommand; // command to run on click
    private final List<UUID> entities = new ArrayList<>();
    private final java.util.Map<UUID, Integer> interactionLines = new java.util.HashMap<>();

    public Hologram(String name, Location location) {
        this.name = name;
        this.location = location.clone();
        this.pages.add(new ArrayList<>());
        this.currentPage = 0;
        this.lineSpacing = 0.3;
        this.viewRange = 48;
        this.billboard = "CENTER";
        this.seeThrough = true;
        this.shadowed = true;
        this.backgroundColor = "transparent";
        this.animation = "none";
        this.animationSpeed = 1;
        this.paused = false;
        this.scale = 1.0f;
        this.alignment = "CENTER";
        this.type = "TEXT";
        this.content = "";
        this.brightness = -1; // -1 = default
        this.lineWidth = 1000;
        this.translationX = 0;
        this.translationY = 0;
        this.translationZ = 0;
        this.rotationYaw = 0;
        this.rotationPitch = 0;
        this.visibilityCondition = "";
        this.onClickCommand = "";
    }

    public String name() { return name; }
    public Location location() { return location.clone(); }
    public void setLocation(Location l) { this.location = l.clone(); }
    public List<HologramLine> lines() { return pages.get(currentPage); }
    public void setLines(List<HologramLine> ls) {
        List<HologramLine> target = lines();
        target.clear();
        if (ls != null) ls.stream().filter(java.util.Objects::nonNull).map(HologramLine::copy).forEach(target::add);
    }
    public int pageCount() { return pages.size(); }
    public int currentPage() { return currentPage; }
    public boolean setCurrentPage(int page) {
        if (page < 0 || page >= pages.size()) return false;
        currentPage = page;
        return true;
    }
    public void addPage() { pages.add(new ArrayList<>()); currentPage = pages.size() - 1; }
    public boolean removeCurrentPage() {
        if (pages.size() <= 1) return false;
        pages.remove(currentPage);
        currentPage = Math.min(currentPage, pages.size() - 1);
        return true;
    }
    public List<List<HologramLine>> pages() {
        List<List<HologramLine>> copy = new ArrayList<>(pages.size());
        for (List<HologramLine> page : pages) copy.add(page.stream().map(HologramLine::copy).toList());
        return copy;
    }
    public void setPages(List<List<HologramLine>> source, int selectedPage) {
        pages.clear();
        if (source != null) {
            for (List<HologramLine> page : source) {
                List<HologramLine> copied = new ArrayList<>();
                if (page != null) page.stream().filter(java.util.Objects::nonNull).map(HologramLine::copy).forEach(copied::add);
                pages.add(copied);
            }
        }
        if (pages.isEmpty()) pages.add(new ArrayList<>());
        currentPage = Math.max(0, Math.min(selectedPage, pages.size() - 1));
    }
    public double lineSpacing() { return lineSpacing; }
    public void setLineSpacing(double v) { this.lineSpacing = Double.isFinite(v) ? Math.max(0.0, Math.min(10.0, v)) : 0.3; }
    public int viewRange() { return viewRange; }
    public void setViewRange(int v) { this.viewRange = Math.max(1, Math.min(128, v)); }
    public String billboard() { return billboard; }
    public void setBillboard(String v) { this.billboard = v == null ? "CENTER" : v.toUpperCase(java.util.Locale.ROOT); }
    public boolean seeThrough() { return seeThrough; }
    public void setSeeThrough(boolean v) { this.seeThrough = v; }
    public boolean shadowed() { return shadowed; }
    public void setShadowed(boolean v) { this.shadowed = v; }
    public String backgroundColor() { return backgroundColor; }
    public void setBackgroundColor(String v) { this.backgroundColor = v == null ? "transparent" : v; }
    public String animation() { return animation; }
    public void setAnimation(String v) { this.animation = v == null ? "none" : v.toLowerCase(java.util.Locale.ROOT); }
    public int animationSpeed() { return animationSpeed; }
    public void setAnimationSpeed(int v) { this.animationSpeed = Math.max(1, Math.min(100, v)); }
    public boolean paused() { return paused; }
    public void setPaused(boolean v) { this.paused = v; }
    public float scale() { return scale; }
    public void setScale(float v) { this.scale = !Float.isFinite(v) || v <= 0 ? 1.0f : Math.min(16.0f, v); }
    public String alignment() { return alignment; }
    public void setAlignment(String v) { this.alignment = v == null ? "CENTER" : v.toUpperCase(java.util.Locale.ROOT); }
    public String type() { return type; }
    public void setType(String v) { this.type = v == null ? "TEXT" : v.toUpperCase(); }
    public String content() { return content; }
    public void setContent(String v) { this.content = v == null ? "" : v; }
    public int brightness() { return brightness; }
    public void setBrightness(int v) { this.brightness = v < 0 ? -1 : Math.min(15, v); }
    public int lineWidth() { return lineWidth; }
    public void setLineWidth(int v) { this.lineWidth = Math.max(1, Math.min(4096, v)); }
    public double translationX() { return translationX; }
    public void setTranslationX(double v) { this.translationX = boundedTranslation(v); }
    public double translationY() { return translationY; }
    public void setTranslationY(double v) { this.translationY = boundedTranslation(v); }
    public double translationZ() { return translationZ; }
    public void setTranslationZ(double v) { this.translationZ = boundedTranslation(v); }
    public float rotationYaw() { return rotationYaw; }
    public void setRotationYaw(float v) { this.rotationYaw = Float.isFinite(v) ? v : 0; }
    public float rotationPitch() { return rotationPitch; }
    public void setRotationPitch(float v) { this.rotationPitch = Float.isFinite(v) ? Math.max(-90, Math.min(90, v)) : 0; }
    public String visibilityCondition() { return visibilityCondition; }
    public void setVisibilityCondition(String v) { this.visibilityCondition = v == null ? "" : v; }
    public String onClickCommand() { return onClickCommand; }
    public void setOnClickCommand(String v) { this.onClickCommand = v == null ? "" : v; }

    public List<UUID> entities() { return entities; }
    public void clearEntities() { entities.clear(); interactionLines.clear(); }
    public void registerLineInteraction(UUID entityId, int lineIndex) { interactionLines.put(entityId, lineIndex); }
    public Integer interactionLine(UUID entityId) { return interactionLines.get(entityId); }

    private static double boundedTranslation(double value) {
        return Double.isFinite(value) ? Math.max(-128.0, Math.min(128.0, value)) : 0.0;
    }
}

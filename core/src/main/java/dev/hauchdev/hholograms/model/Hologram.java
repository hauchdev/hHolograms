package dev.hauchdev.hholograms.model;

import org.bukkit.Location;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class Hologram {
    private final String name;
    private Location location;
    private final List<HologramLine> lines = new ArrayList<>();
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
    private double translationX;
    private double translationY;
    private double translationZ;
    private float rotationYaw;
    private float rotationPitch;
    private String visibilityCondition; // placeholder expression, empty = always
    private String onClickCommand; // command to run on click
    private final List<UUID> entities = new ArrayList<>();

    public Hologram(String name, Location location) {
        this.name = name;
        this.location = location.clone();
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
    public List<HologramLine> lines() { return lines; }
    public void setLines(List<HologramLine> ls) { lines.clear(); lines.addAll(ls); }
    public double lineSpacing() { return lineSpacing; }
    public void setLineSpacing(double v) { this.lineSpacing = v; }
    public int viewRange() { return viewRange; }
    public void setViewRange(int v) { this.viewRange = v; }
    public String billboard() { return billboard; }
    public void setBillboard(String v) { this.billboard = v; }
    public boolean seeThrough() { return seeThrough; }
    public void setSeeThrough(boolean v) { this.seeThrough = v; }
    public boolean shadowed() { return shadowed; }
    public void setShadowed(boolean v) { this.shadowed = v; }
    public String backgroundColor() { return backgroundColor; }
    public void setBackgroundColor(String v) { this.backgroundColor = v; }
    public String animation() { return animation; }
    public void setAnimation(String v) { this.animation = v; }
    public int animationSpeed() { return animationSpeed; }
    public void setAnimationSpeed(int v) { this.animationSpeed = Math.max(1, v); }
    public boolean paused() { return paused; }
    public void setPaused(boolean v) { this.paused = v; }
    public float scale() { return scale; }
    public void setScale(float v) { this.scale = v <= 0 ? 1.0f : v; }
    public String alignment() { return alignment; }
    public void setAlignment(String v) { this.alignment = v == null ? "CENTER" : v.toUpperCase(); }
    public String type() { return type; }
    public void setType(String v) { this.type = v == null ? "TEXT" : v.toUpperCase(); }
    public String content() { return content; }
    public void setContent(String v) { this.content = v == null ? "" : v; }
    public int brightness() { return brightness; }
    public void setBrightness(int v) { this.brightness = v; }
    public double translationX() { return translationX; }
    public void setTranslationX(double v) { this.translationX = v; }
    public double translationY() { return translationY; }
    public void setTranslationY(double v) { this.translationY = v; }
    public double translationZ() { return translationZ; }
    public void setTranslationZ(double v) { this.translationZ = v; }
    public float rotationYaw() { return rotationYaw; }
    public void setRotationYaw(float v) { this.rotationYaw = v; }
    public float rotationPitch() { return rotationPitch; }
    public void setRotationPitch(float v) { this.rotationPitch = v; }
    public String visibilityCondition() { return visibilityCondition; }
    public void setVisibilityCondition(String v) { this.visibilityCondition = v == null ? "" : v; }
    public String onClickCommand() { return onClickCommand; }
    public void setOnClickCommand(String v) { this.onClickCommand = v == null ? "" : v; }

    public List<UUID> entities() { return entities; }
    public void clearEntities() { entities.clear(); }
}
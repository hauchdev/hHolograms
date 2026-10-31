package dev.hauchdev.hholograms.model;

public final class HologramLine {
    private String text;
    private float scale;
    private String animation;
    private int animationSpeed;
    private boolean paused;

    public HologramLine(String text) {
        this.text = text == null ? "" : text;
        this.scale = 1.0f;
        this.animation = "none";
        this.animationSpeed = 1;
        this.paused = false;
    }

    public String text() { return text; }
    public void setText(String t) { this.text = t == null ? "" : t; }
    public float scale() { return scale; }
    public void setScale(float v) { this.scale = v <= 0 ? 1.0f : v; }
    public String animation() { return animation; }
    public void setAnimation(String v) { this.animation = v == null ? "none" : v; }
    public int animationSpeed() { return animationSpeed; }
    public void setAnimationSpeed(int v) { this.animationSpeed = Math.max(1, v); }
    public boolean paused() { return paused; }
    public void setPaused(boolean v) { this.paused = v; }
}
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

    /** Returns an independent copy suitable for duplicating a hologram. */
    public HologramLine copy() {
        HologramLine copy = new HologramLine(text);
        copy.setScale(scale);
        copy.setAnimation(animation);
        copy.setAnimationSpeed(animationSpeed);
        copy.setPaused(paused);
        return copy;
    }

    public String text() { return text; }
    public void setText(String t) { this.text = t == null ? "" : t; }
    public float scale() { return scale; }
    public void setScale(float v) { this.scale = !Float.isFinite(v) || v <= 0 ? 1.0f : Math.min(16.0f, v); }
    public String animation() { return animation; }
    public void setAnimation(String v) { this.animation = v == null ? "none" : v.toLowerCase(java.util.Locale.ROOT); }
    public int animationSpeed() { return animationSpeed; }
    public void setAnimationSpeed(int v) { this.animationSpeed = Math.max(1, Math.min(100, v)); }
    public boolean paused() { return paused; }
    public void setPaused(boolean v) { this.paused = v; }
}

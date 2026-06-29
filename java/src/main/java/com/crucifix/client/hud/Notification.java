package com.crucifix.client.hud;

/**
 * HUD notification popup
 */
public class Notification {
    private String message;
    private long duration;
    private long startTime;
    private float alpha;
    
    public Notification(String message, long duration) {
        this.message = message;
        this.duration = duration;
        this.startTime = System.currentTimeMillis();
        this.alpha = 1.0f;
    }
    
    public void update() {
        long elapsed = System.currentTimeMillis() - startTime;
        if (elapsed > duration - 500) {
            alpha = Math.max(0, (duration - elapsed) / 500.0f);
        }
    }
    
    public boolean isExpired() {
        return System.currentTimeMillis() - startTime > duration;
    }
    
    public void render(int yOffset) {
        // Simplified rendering - actual rendering would be done in RenderEvent
        // This is handled by the HUD component system
    }
    
    public String getMessage() {
        return message;
    }
    
    public float getAlpha() {
        return alpha;
    }
}

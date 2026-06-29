package com.crucifix.client.hud;

/**
 * Base class for HUD components
 */
public abstract class HUDComponent {
    protected int x;
    protected int y;
    protected boolean enabled;
    protected boolean dragging;
    
    public HUDComponent(int x, int y) {
        this.x = x;
        this.y = y;
        this.enabled = true;
        this.dragging = false;
    }
    
    public abstract void update();
    public abstract void render(float partialTicks);
    
    public boolean isMouseOver(int mouseX, int mouseY) {
        return mouseX >= x && mouseX <= x + getWidth() && mouseY >= y && mouseY <= y + getHeight();
    }
    
    public abstract int getWidth();
    public abstract int getHeight();
    
    // Getters and setters
    public int getX() {
        return x;
    }
    
    public void setX(int x) {
        this.x = x;
    }
    
    public int getY() {
        return y;
    }
    
    public void setY(int y) {
        this.y = y;
    }
    
    public boolean isEnabled() {
        return enabled;
    }
    
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    
    public boolean isDragging() {
        return dragging;
    }
    
    public void setDragging(boolean dragging) {
        this.dragging = dragging;
    }
}


package com.crucifix.client.events;

/**
 * Event fired during rendering
 */
public class RenderEvent implements Event {
    private final float partialTicks;
    private boolean cancelled;
    
    public RenderEvent(float partialTicks) {
        this.partialTicks = partialTicks;
        this.cancelled = false;
    }
    
    public float getPartialTicks() {
        return partialTicks;
    }
    
    @Override
    public boolean isCancelled() {
        return cancelled;
    }
    
    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}


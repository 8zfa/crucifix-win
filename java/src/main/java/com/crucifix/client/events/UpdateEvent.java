package com.crucifix.client.events;

/**
 * Event fired every game tick
 */
public class UpdateEvent implements Event {
    private final boolean pre;
    private boolean cancelled;
    
    public UpdateEvent(boolean pre) {
        this.pre = pre;
        this.cancelled = false;
    }
    
    public boolean isPre() {
        return pre;
    }
    
    public boolean isPost() {
        return !pre;
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


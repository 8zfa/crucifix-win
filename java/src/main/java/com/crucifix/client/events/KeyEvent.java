package com.crucifix.client.events;

/**
 * Event fired when a key is pressed
 */
public class KeyEvent implements Event {
    private final int keyCode;
    private boolean cancelled;
    
    public KeyEvent(int keyCode) {
        this.keyCode = keyCode;
        this.cancelled = false;
    }
    
    public int getKeyCode() {
        return keyCode;
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


package com.crucifix.client.events;

/**
 * Base interface for all events
 */
public interface Event {
    boolean isCancelled();
    void setCancelled(boolean cancelled);
}


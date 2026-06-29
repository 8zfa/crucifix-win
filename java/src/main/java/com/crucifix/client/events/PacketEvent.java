package com.crucifix.client.events;

/**
 * Event fired when a packet is sent or received
 */
public class PacketEvent implements Event {
    private final Object packet;
    private final boolean outgoing;
    private boolean cancelled;
    
    public PacketEvent(Object packet, boolean outgoing) {
        this.packet = packet;
        this.outgoing = outgoing;
        this.cancelled = false;
    }
    
    public Object getPacket() {
        return packet;
    }
    
    public boolean isOutgoing() {
        return outgoing;
    }
    
    public boolean isIncoming() {
        return !outgoing;
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


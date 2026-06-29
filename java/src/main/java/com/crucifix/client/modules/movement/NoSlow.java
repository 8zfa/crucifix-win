package com.crucifix.client.modules.movement;

import com.crucifix.client.events.UpdateEvent;
import com.crucifix.client.events.SubscribeEvent;
import com.crucifix.client.modules.Category;
import com.crucifix.client.modules.Module;
import com.crucifix.client.modules.Setting;

/**
 * Prevents slowdown from items
 */
public class NoSlow extends Module {
    
    public NoSlow(String name, Category category, Object mc) {
        super(name, category, mc);
        ; // N key
        
        addSetting(Setting.createToggle("Items", true));
        addSetting(Setting.createToggle("SoulSand", true));
    }
    
    @SubscribeEvent
    public void onUpdateEvent(UpdateEvent event) {
        if (!isEnabled()) return;
        
        try {
            Object player = getPlayer();
            if (player == null) return;
            
            boolean items = getSetting("Items").getBooleanValue();
            boolean soulSand = getSetting("SoulSand").getBooleanValue();
            
            if (items) {
                // Prevent item slowdown
                setField(player, "usingItem", false);
            }
            
            if (soulSand) {
                // Prevent soulsand slowdown
                Boolean onGround = (Boolean) getField(player, "onGround");
                if (onGround != null && onGround) {
                    Float speed = (Float) getField(player, "speed");
                    if (speed != null && speed < 0.1f) {
                        setField(player, "speed", 0.1f);
                    }
                }
            }
        } catch (Exception e) {
            // Silent fail
        }
    }
}



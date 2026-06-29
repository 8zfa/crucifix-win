package com.crucifix.client.modules.player;

import com.crucifix.client.events.UpdateEvent;
import com.crucifix.client.events.SubscribeEvent;
import com.crucifix.client.modules.Category;
import com.crucifix.client.modules.Module;

/**
 * Prevents fire damage
 */
public class AntiFire extends Module {
    
    public AntiFire(String name, Category category, Object mc) {
        super(name, category, mc);
        ;
    }
    
    @SubscribeEvent
    public void onUpdateEvent(UpdateEvent event) {
        if (!isEnabled()) return;
        
        try {
            Object player = getPlayer();
            if (player == null) return;
            
            // Prevent fire damage by extinguishing
            Boolean isBurning = (Boolean) getField(player, "isBurning");
            if (isBurning != null && isBurning) {
                setField(player, "fire", 0);
            }
        } catch (Exception e) {
            // Silent fail
        }
    }
}



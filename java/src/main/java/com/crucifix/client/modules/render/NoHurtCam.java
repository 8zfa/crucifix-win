package com.crucifix.client.modules.render;

import com.crucifix.client.events.UpdateEvent;
import com.crucifix.client.events.SubscribeEvent;
import com.crucifix.client.modules.Category;
import com.crucifix.client.modules.Module;

/**
 * Disables hurt camera effect
 */
public class NoHurtCam extends Module {
    
    public NoHurtCam(String name, Category category, Object mc) {
        super(name, category, mc);
        ;
    }
    
    @SubscribeEvent
    public void onUpdateEvent(UpdateEvent event) {
        if (!isEnabled()) return;
        
        try {
            Object player = getPlayer();
            if (player == null) return;
            
            // Disable hurt camera effect
            setField(player, "hurtTime", 0);
        } catch (Exception e) {
            // Silent fail
        }
    }
}



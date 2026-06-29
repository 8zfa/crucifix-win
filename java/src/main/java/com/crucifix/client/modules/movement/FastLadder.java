package com.crucifix.client.modules.movement;

import com.crucifix.client.events.UpdateEvent;
import com.crucifix.client.events.SubscribeEvent;
import com.crucifix.client.modules.Category;
import com.crucifix.client.modules.Module;
import com.crucifix.client.modules.Setting;

/**
 * Climbs ladders faster
 */
public class FastLadder extends Module {
    
    public FastLadder(String name, Category category, Object mc) {
        super(name, category, mc);
        ;
        
        addSetting(Setting.createSlider("Speed", 2.0, 1.0, 5.0, 0.1));
    }
    
    @SubscribeEvent
    public void onUpdateEvent(UpdateEvent event) {
        if (!isEnabled()) return;
        
        try {
            Object player = getPlayer();
            if (player == null) return;
            
            double speed = getSetting("Speed").getDoubleValue();
            
            Boolean onGround = (Boolean) getField(player, "onGround");
            Boolean isOnLadder = (Boolean) getField(player, "isOnLadder");
            
            if (isOnLadder != null && isOnLadder) {
                // Increase ladder climb speed
                setField(player, "motionY", (float) speed * 0.1f);
            }
        } catch (Exception e) {
            // Silent fail
        }
    }
}



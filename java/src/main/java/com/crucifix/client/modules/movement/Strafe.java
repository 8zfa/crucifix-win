package com.crucifix.client.modules.movement;

import com.crucifix.client.events.UpdateEvent;
import com.crucifix.client.events.SubscribeEvent;
import com.crucifix.client.modules.Category;
import com.crucifix.client.modules.Module;
import com.crucifix.client.modules.Setting;

/**
 * Improves strafing in air
 */
public class Strafe extends Module {
    
    public Strafe() {
        super("Strafe", "Improves air strafing", Category.MOVEMENT, 0);
        
        addSetting(Setting.createSlider("Speed", 0.5, 0.1, 1.0, 0.05));
    }
    
    @SubscribeEvent
    public void onUpdateEvent(UpdateEvent event) {
        if (!isEnabled()) return;
        
        try {
            Object player = getPlayer();
            if (player == null) return;
            
            double speed = getSetting("Speed").getDoubleValue();
            
            Boolean onGround = (Boolean) getField(player, "onGround");
            if (onGround != null && !onGround) {
                // Improve air strafing by modifying speed
                Float currentSpeed = (Float) getField(player, "speed");
                if (currentSpeed != null) {
                    setField(player, "speed", currentSpeed * (float) speed);
                }
            }
        } catch (Exception e) {
            // Silent fail
        }
    }
}


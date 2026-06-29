package com.crucifix.client.modules.combat;

import com.crucifix.client.events.UpdateEvent;
import com.crucifix.client.events.SubscribeEvent;
import com.crucifix.client.modules.Category;
import com.crucifix.client.modules.Module;
import com.crucifix.client.modules.Setting;

/**
 * Extends attack reach distance
 */
public class Reach extends Module {
    
    public Reach() {
        super("Reach", "Extends attack reach distance", Category.COMBAT, 0);
        
        addSetting(Setting.createSlider("Distance", 3.5, 3.0, 6.0, 0.1));
    }
    
    @SubscribeEvent
    public void onUpdateEvent(UpdateEvent event) {
        if (!isEnabled()) return;
        
        try {
            Object player = getPlayer();
            if (player == null) return;
            
            double distance = getSetting("Distance").getDoubleValue();
            
            // Set reach distance via player capabilities
            Object capabilities = getField(player, "capabilities");
            if (capabilities != null) {
                setField(capabilities, "playerReachDistance", (float) distance);
            }
        } catch (Exception e) {
            // Silent fail
        }
    }
    
    @Override
    public void onDisable() {
        try {
            Object player = getPlayer();
            if (player != null) {
                Object capabilities = getField(player, "capabilities");
                if (capabilities != null) {
                    setField(capabilities, "playerReachDistance", 3.0f); // Reset to default
                }
            }
        } catch (Exception e) {
            // Silent fail
        }
    }
}


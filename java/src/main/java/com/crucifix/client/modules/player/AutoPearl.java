package com.crucifix.client.modules.player;

import com.crucifix.client.events.UpdateEvent;
import com.crucifix.client.events.SubscribeEvent;
import com.crucifix.client.modules.Category;
import com.crucifix.client.modules.Module;
import com.crucifix.client.modules.Setting;

/**
 * Automatically throws ender pearls
 */
public class AutoPearl extends Module {
    
    public AutoPearl(String name, Category category, Object mc) {
        super(name, category, mc);
        ;
        
        addSetting(Setting.createSlider("Health", 5.0, 1.0, 10.0, 0.5));
    }
    
    @SubscribeEvent
    public void onUpdateEvent(UpdateEvent event) {
        if (!isEnabled()) return;
        
        try {
            Object player = getPlayer();
            if (player == null) return;
            
            double health = getSetting("Health").getDoubleValue();
            
            Float playerHealth = (Float) getField(player, "health");
            if (playerHealth != null && playerHealth < (float) health) {
                // Auto throw pearl would trigger here
                // This is a simplified implementation
            }
        } catch (Exception e) {
            // Silent fail
        }
    }
}



package com.crucifix.client.modules.player;

import com.crucifix.client.events.UpdateEvent;
import com.crucifix.client.events.SubscribeEvent;
import com.crucifix.client.modules.Category;
import com.crucifix.client.modules.Module;
import com.crucifix.client.modules.Setting;

/**
 * Automatically eats soup
 */
public class AutoSoup extends Module {
    
    public AutoSoup(String name, Category category, Object mc) {
        super(name, category, mc);
        ;
        
        addSetting(Setting.createSlider("Health", 15.0, 1.0, 20.0, 1.0));
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
                // Auto eat soup would trigger here
                callMethod(player, "setItemInUse", new Class<?>[]{Object.class, int.class}, null, 0);
            }
        } catch (Exception e) {
            // Silent fail
        }
    }
}



package com.crucifix.client.modules.movement;

import com.crucifix.client.events.UpdateEvent;
import com.crucifix.client.events.SubscribeEvent;
import com.crucifix.client.modules.Category;
import com.crucifix.client.modules.Module;
import com.crucifix.client.modules.Setting;

/**
 * Steps up blocks automatically
 */
public class Step extends Module {
    
    public Step(String name, Category category, Object mc) {
        super(name, category, mc);
        ;
        
        addSetting(Setting.createSlider("Height", 1.0, 0.5, 2.5, 0.5));
    }
    
    @SubscribeEvent
    public void onUpdateEvent(UpdateEvent event) {
        if (!isEnabled()) return;
        
        try {
            Object player = getPlayer();
            if (player == null) return;
            
            double height = getSetting("Height").getDoubleValue();
            
            // Set step height
            setField(player, "stepHeight", (float) height);
        } catch (Exception e) {
            // Silent fail
        }
    }
    
    @Override
    public void onDisable() {
        try {
            Object player = getPlayer();
            if (player != null) {
                setField(player, "stepHeight", 0.5f); // Reset to default
            }
        } catch (Exception e) {
            // Silent fail
        }
    }
}



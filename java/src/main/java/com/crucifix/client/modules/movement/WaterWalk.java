package com.crucifix.client.modules.movement;

import com.crucifix.client.events.UpdateEvent;
import com.crucifix.client.events.SubscribeEvent;
import com.crucifix.client.modules.Category;
import com.crucifix.client.modules.Module;
import com.crucifix.client.modules.Setting;

/**
 * Walks on water
 */
public class WaterWalk extends Module {
    
    public WaterWalk(String name, Category category, Object mc) {
        super(name, category, mc);
        ;
        
        addSetting(Setting.createDropdown("Mode", new String[]{"Vanilla", "Dolphin", "NCP"}));
    }
    
    @SubscribeEvent
    public void onUpdateEvent(UpdateEvent event) {
        if (!isEnabled()) return;
        
        try {
            Object player = getPlayer();
            if (player == null) return;
            
            String mode = getSetting("Mode").getStringValue();
            
            Boolean inWater = (Boolean) getField(player, "inWater");
            if (inWater != null && inWater) {
                // Prevent sinking in water
                setField(player, "motionY", 0.0);
                
                if (mode.equals("Dolphin")) {
                    // Dolphin mode - jump periodically
                    setField(player, "jump", 0.1f);
                }
            }
        } catch (Exception e) {
            // Silent fail
        }
    }
}



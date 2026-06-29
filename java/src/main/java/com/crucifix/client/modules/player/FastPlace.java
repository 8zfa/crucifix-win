package com.crucifix.client.modules.player;

import com.crucifix.client.events.UpdateEvent;
import com.crucifix.client.events.SubscribeEvent;
import com.crucifix.client.modules.Category;
import com.crucifix.client.modules.Module;
import com.crucifix.client.modules.Setting;

/**
 * Places blocks faster
 */
public class FastPlace extends Module {
    
    public FastPlace() {
        super("FastPlace", "Places blocks faster", Category.PLAYER, 0);
        
        addSetting(Setting.createSlider("Speed", 0.0, 0.0, 4.0, 1.0));
    }
    
    @SubscribeEvent
    public void onUpdateEvent(UpdateEvent event) {
        if (!isEnabled()) return;
        
        try {
            Object mc = getMinecraft();
            if (mc == null) return;
            
            double speed = getSetting("Speed").getDoubleValue();
            
            Object rightClickDelayTimer = getField(mc, "rightClickDelayTimer");
            if (rightClickDelayTimer != null) {
                setField(rightClickDelayTimer, "field_73719_d", 0); // Reset delay
            }
        } catch (Exception e) {
            // Silent fail
        }
    }
}


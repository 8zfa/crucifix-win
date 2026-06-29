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
    
    public FastLadder() {
        super("FastLadder", "Climbs ladders faster", Category.MOVEMENT, 0);
        
        addSetting(Setting.createSlider("Speed", 2.0, 1.0, 5.0, 0.1));
    }
    
    @Override
    public void onUpdate() {
        // Implementation would increase ladder climb speed
    }
    
    @SubscribeEvent
    public void onUpdateEvent(UpdateEvent event) {
        if (!isEnabled()) return;
        
        double speed = getSetting("Speed").getDoubleValue();
        
        // Fast ladder logic would go here
    }
}


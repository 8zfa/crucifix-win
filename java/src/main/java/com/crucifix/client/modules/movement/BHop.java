package com.crucifix.client.modules.movement;

import com.crucifix.client.events.UpdateEvent;
import com.crucifix.client.events.SubscribeEvent;
import com.crucifix.client.modules.Category;
import com.crucifix.client.modules.Module;
import com.crucifix.client.modules.Setting;

/**
 * Bunny hop for increased speed
 */
public class BHop extends Module {
    
    public BHop() {
        super("BHop", "Bunny hop for increased speed", Category.MOVEMENT, 0);
        
        addSetting(Setting.createSlider("Speed", 0.3, 0.1, 1.0, 0.05));
    }
    
    @Override
    public void onUpdate() {
        // Implementation would auto-jump
    }
    
    @SubscribeEvent
    public void onUpdateEvent(UpdateEvent event) {
        if (!isEnabled()) return;
        
        double speed = getSetting("Speed").getDoubleValue();
        
        // BHop logic would go here
    }
}


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
    
    @Override
    public void onUpdate() {
        // Implementation would modify reach checks
    }
    
    @SubscribeEvent
    public void onUpdateEvent(UpdateEvent event) {
        if (!isEnabled()) return;
        
        double distance = getSetting("Distance").getDoubleValue();
        
        // Reach modification logic would go here
    }
}


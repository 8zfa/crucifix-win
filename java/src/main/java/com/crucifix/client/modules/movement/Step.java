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
    
    public Step() {
        super("Step", "Steps up blocks automatically", Category.MOVEMENT, 0);
        
        addSetting(Setting.createSlider("Height", 1.0, 0.5, 2.5, 0.5));
    }
    
    @Override
    public void onUpdate() {
        // Implementation would auto-step up blocks
    }
    
    @SubscribeEvent
    public void onUpdateEvent(UpdateEvent event) {
        if (!isEnabled()) return;
        
        double height = getSetting("Height").getDoubleValue();
        
        // Step logic would go here
    }
}


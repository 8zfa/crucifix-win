package com.crucifix.client.modules.movement;

import com.crucifix.client.events.UpdateEvent;
import com.crucifix.client.events.SubscribeEvent;
import com.crucifix.client.modules.Category;
import com.crucifix.client.modules.Module;
import com.crucifix.client.modules.Setting;

/**
 * Increases jump distance
 */
public class LongJump extends Module {
    
    public LongJump() {
        super("LongJump", "Increases jump distance", Category.MOVEMENT, 0);
        
        addSetting(Setting.createSlider("Boost", 2.0, 1.0, 5.0, 0.1));
    }
    
    @Override
    public void onUpdate() {
        // Implementation would boost jump distance
    }
    
    @SubscribeEvent
    public void onUpdateEvent(UpdateEvent event) {
        if (!isEnabled()) return;
        
        double boost = getSetting("Boost").getDoubleValue();
        
        // Long jump logic would go here
    }
}


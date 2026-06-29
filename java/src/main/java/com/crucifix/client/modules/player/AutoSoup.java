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
    
    public AutoSoup() {
        super("AutoSoup", "Automatically eats soup", Category.PLAYER, 0);
        
        addSetting(Setting.createSlider("Health", 15.0, 1.0, 20.0, 1.0));
    }
    
    @Override
    public void onUpdate() {
        // Implementation would auto-eat soup
    }
    
    @SubscribeEvent
    public void onUpdateEvent(UpdateEvent event) {
        if (!isEnabled()) return;
        
        double health = getSetting("Health").getDoubleValue();
        
        // Auto soup logic would go here
    }
}


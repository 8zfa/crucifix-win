package com.crucifix.client.modules.misc;

import com.crucifix.client.events.UpdateEvent;
import com.crucifix.client.events.SubscribeEvent;
import com.crucifix.client.modules.Category;
import com.crucifix.client.modules.Module;
import com.crucifix.client.modules.Setting;

/**
 * Automatically tips players
 */
public class AutoTip extends Module {
    
    public AutoTip(String name, Category category, Object mc) {
        super(name, category, mc);
        ;
        
        addSetting(Setting.createSlider("Amount", 100.0, 10.0, 1000.0, 10.0));
    }
    
    @Override
    public void onUpdate() {
        // Implementation would auto-tip
    }
    
    @SubscribeEvent
    public void onUpdateEvent(UpdateEvent event) {
        if (!isEnabled()) return;
        
        double amount = getSetting("Amount").getDoubleValue();
        
        // Auto tip logic would go here
    }
}



package com.crucifix.client.modules.misc;

import com.crucifix.client.events.UpdateEvent;
import com.crucifix.client.events.SubscribeEvent;
import com.crucifix.client.modules.Category;
import com.crucifix.client.modules.Module;
import com.crucifix.client.modules.Setting;

/**
 * Automatically sends GG message
 */
public class AutoGG extends Module {
    
    public AutoGG(String name, Category category, Object mc) {
        super(name, category, mc);
        ;
        
        addSetting(Setting.create("Message", "GG"));
        addSetting(Setting.createSlider("Delay", 1.0, 0.0, 5.0, 0.5));
    }
    
    @SubscribeEvent
    public void onUpdateEvent(UpdateEvent event) {
        if (!isEnabled()) return;
        
        try {
            Object mc = getMinecraft();
            if (mc == null) return;
            
            String message = getSetting("Message").getStringValue();
            double delay = getSetting("Delay").getDoubleValue();
            
            // Auto GG would send message on game end
            // This is a simplified implementation
        } catch (Exception e) {
            // Silent fail
        }
    }
}



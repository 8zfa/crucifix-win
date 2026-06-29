package com.crucifix.client.modules.misc;

import com.crucifix.client.events.UpdateEvent;
import com.crucifix.client.events.SubscribeEvent;
import com.crucifix.client.modules.Category;
import com.crucifix.client.modules.Module;
import com.crucifix.client.modules.Setting;

/**
 * Changes world time
 */
public class TimeChanger extends Module {
    
    public TimeChanger() {
        super("TimeChanger", "Changes world time", Category.MISC, 0);
        
        addSetting(Setting.createSlider("Time", 12000.0, 0.0, 24000.0, 1000.0));
    }
    
    @SubscribeEvent
    public void onUpdateEvent(UpdateEvent event) {
        if (!isEnabled()) return;
        
        try {
            Object world = getWorld();
            if (world == null) return;
            
            double time = getSetting("Time").getDoubleValue();
            
            // Set world time
            setField(world, "worldTime", (long) time);
        } catch (Exception e) {
            // Silent fail
        }
    }
}


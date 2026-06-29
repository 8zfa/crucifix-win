package com.crucifix.client.modules.render;

import com.crucifix.client.events.UpdateEvent;
import com.crucifix.client.events.SubscribeEvent;
import com.crucifix.client.modules.Category;
import com.crucifix.client.modules.Module;
import com.crucifix.client.modules.Setting;

/**
 * Increases brightness
 */
public class FullBright extends Module {
    
    public FullBright() {
        super("FullBright", "Increases brightness", Category.RENDER, 0);
        
        addSetting(Setting.createSlider("Gamma", 15.0, 1.0, 20.0, 0.5));
    }
    
    @Override
    public void onEnable() {
        // Implementation would set gamma
    }
    
    @Override
    public void onDisable() {
        // Implementation would reset gamma
    }
    
    @SubscribeEvent
    public void onUpdateEvent(UpdateEvent event) {
        if (!isEnabled()) return;
        
        double gamma = getSetting("Gamma").getDoubleValue();
        
        // Full bright logic would go here
    }
}


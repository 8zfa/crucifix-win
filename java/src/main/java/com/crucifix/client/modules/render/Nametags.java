package com.crucifix.client.modules.render;

import com.crucifix.client.events.RenderEvent;
import com.crucifix.client.events.SubscribeEvent;
import com.crucifix.client.modules.Category;
import com.crucifix.client.modules.Module;
import com.crucifix.client.modules.Setting;

/**
 * Shows nametags through walls
 */
public class Nametags extends Module {
    
    public Nametags() {
        super("Nametags", "Shows nametags through walls", Category.RENDER, 0);
        
        addSetting(Setting.createToggle("Health", true));
        addSetting(Setting.createToggle("Distance", true));
        addSetting(Setting.createSlider("Scale", 1.0, 0.5, 2.0, 0.1));
    }
    
    @Override
    public void onRender(float partialTicks) {
        // Implementation would render nametags
    }
    
    @SubscribeEvent
    public void onRenderEvent(RenderEvent event) {
        if (!isEnabled()) return;
        
        boolean health = getSetting("Health").getBooleanValue();
        boolean distance = getSetting("Distance").getBooleanValue();
        double scale = getSetting("Scale").getDoubleValue();
        
        // Nametags rendering logic would go here
    }
}


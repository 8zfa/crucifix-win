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
    
    @SubscribeEvent
    public void onRenderEvent(RenderEvent event) {
        if (!isEnabled()) return;
        
        try {
            Object world = getWorld();
            if (world == null) return;
            
            boolean health = getSetting("Health").getBooleanValue();
            boolean distance = getSetting("Distance").getBooleanValue();
            double scale = getSetting("Scale").getDoubleValue();
            
            // Nametags would render entity names through walls
            // This is a simplified implementation - would need OpenGL hooks
        } catch (Exception e) {
            // Silent fail
        }
    }
}


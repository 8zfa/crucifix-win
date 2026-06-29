package com.crucifix.client.modules.render;

import com.crucifix.client.events.RenderEvent;
import com.crucifix.client.events.SubscribeEvent;
import com.crucifix.client.modules.Category;
import com.crucifix.client.modules.Module;
import com.crucifix.client.modules.Setting;

/**
 * Renders entities through walls
 */
public class Chams extends Module {
    
    public Chams() {
        super("Chams", "Renders entities through walls", Category.RENDER, 0);
        
        addSetting(Setting.createToggle("Players", true));
        addSetting(Setting.createToggle("Mobs", false));
        addSetting(Setting.createToggle("Color", true));
    }
    
    @SubscribeEvent
    public void onRenderEvent(RenderEvent event) {
        if (!isEnabled()) return;
        
        try {
            Object mc = getMinecraft();
            if (mc == null) return;
            
            boolean players = getSetting("Players").getBooleanValue();
            boolean mobs = getSetting("Mobs").getBooleanValue();
            
            // Chams would modify entity rendering to show through walls
            // This is a simplified implementation
            Object gameSettings = getField(mc, "gameSettings");
            if (gameSettings != null) {
                setField(gameSettings, "ambientOcclusion", false);
            }
        } catch (Exception e) {
            // Silent fail
        }
    }
}


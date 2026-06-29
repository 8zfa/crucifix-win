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
        try {
            Object mc = getMinecraft();
            if (mc != null) {
                Object gameSettings = getField(mc, "gameSettings");
                if (gameSettings != null) {
                    setField(gameSettings, "gammaSetting", 15.0f);
                }
            }
        } catch (Exception e) {
            // Silent fail
        }
    }
    
    @Override
    public void onDisable() {
        try {
            Object mc = getMinecraft();
            if (mc != null) {
                Object gameSettings = getField(mc, "gameSettings");
                if (gameSettings != null) {
                    setField(gameSettings, "gammaSetting", 1.0f); // Reset to default
                }
            }
        } catch (Exception e) {
            // Silent fail
        }
    }
    
    @SubscribeEvent
    public void onUpdateEvent(UpdateEvent event) {
        if (!isEnabled()) return;
        
        try {
            Object mc = getMinecraft();
            if (mc == null) return;
            
            double gamma = getSetting("Gamma").getDoubleValue();
            
            Object gameSettings = getField(mc, "gameSettings");
            if (gameSettings != null) {
                setField(gameSettings, "gammaSetting", (float) gamma);
            }
        } catch (Exception e) {
            // Silent fail
        }
    }
}


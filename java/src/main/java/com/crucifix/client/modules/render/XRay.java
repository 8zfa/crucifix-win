package com.crucifix.client.modules.render;

import com.crucifix.client.events.UpdateEvent;
import com.crucifix.client.events.SubscribeEvent;
import com.crucifix.client.modules.Category;
import com.crucifix.client.modules.Module;
import com.crucifix.client.modules.Setting;

/**
 * Makes blocks transparent
 */
public class XRay extends Module {
    
    public XRay(String name, Category category, Object mc) {
        super(name, category, mc);
        ;
        
        addSetting(Setting.createSlider("Opacity", 0.5, 0.0, 1.0, 0.05));
    }
    
    @SubscribeEvent
    public void onUpdateEvent(UpdateEvent event) {
        if (!isEnabled()) return;
        
        try {
            Object mc = getMinecraft();
            if (mc == null) return;
            
            double opacity = getSetting("Opacity").getDoubleValue();
            
            Object gameSettings = getField(mc, "gameSettings");
            if (gameSettings != null) {
                setField(gameSettings, "ambientOcclusion", false);
            }
            
            // XRay would modify block rendering
            // This is a simplified implementation
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
                    setField(gameSettings, "ambientOcclusion", true);
                }
            }
        } catch (Exception e) {
            // Silent fail
        }
    }
}



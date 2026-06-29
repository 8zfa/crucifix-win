package com.crucifix.client.modules.render;

import com.crucifix.client.events.UpdateEvent;
import com.crucifix.client.events.SubscribeEvent;
import com.crucifix.client.modules.Category;
import com.crucifix.client.modules.Module;

/**
 * Allows camera to clip through blocks
 */
public class CameraClip extends Module {
    
    public CameraClip() {
        super("CameraClip", "Allows camera to clip through blocks", Category.RENDER, 0);
    }
    
    @SubscribeEvent
    public void onUpdateEvent(UpdateEvent event) {
        if (!isEnabled()) return;
        
        try {
            Object mc = getMinecraft();
            if (mc == null) return;
            
            Object entityRenderer = getField(mc, "entityRenderer");
            if (entityRenderer != null) {
                setField(entityRenderer, "thirdPersonDistance", 4.0f);
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
                Object entityRenderer = getField(mc, "entityRenderer");
                if (entityRenderer != null) {
                    setField(entityRenderer, "thirdPersonDistance", 4.0f);
                }
            }
        } catch (Exception e) {
            // Silent fail
        }
    }
}


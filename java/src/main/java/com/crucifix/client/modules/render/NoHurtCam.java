package com.crucifix.client.modules.render;

import com.crucifix.client.events.UpdateEvent;
import com.crucifix.client.events.SubscribeEvent;
import com.crucifix.client.modules.Category;
import com.crucifix.client.modules.Module;

/**
 * Disables hurt camera effect
 */
public class NoHurtCam extends Module {
    
    public NoHurtCam() {
        super("NoHurtCam", "Disables hurt camera effect", Category.RENDER, 0);
    }
    
    @Override
    public void onUpdate() {
        // Implementation would disable hurt camera
    }
    
    @SubscribeEvent
    public void onUpdateEvent(UpdateEvent event) {
        if (!isEnabled()) return;
        
        // No hurt cam logic would go here
    }
}


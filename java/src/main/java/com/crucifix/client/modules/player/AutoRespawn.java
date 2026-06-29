package com.crucifix.client.modules.player;

import com.crucifix.client.events.UpdateEvent;
import com.crucifix.client.events.SubscribeEvent;
import com.crucifix.client.modules.Category;
import com.crucifix.client.modules.Module;
import com.crucifix.client.modules.Setting;

/**
 * Automatically respawns
 */
public class AutoRespawn extends Module {
    
    public AutoRespawn() {
        super("AutoRespawn", "Automatically respawns", Category.PLAYER, 0);
        
        addSetting(Setting.createSlider("Delay", 0.0, 0.0, 5.0, 0.5));
    }
    
    @SubscribeEvent
    public void onUpdateEvent(UpdateEvent event) {
        if (!isEnabled()) return;
        
        try {
            Object player = getPlayer();
            if (player == null) return;
            
            double delay = getSetting("Delay").getDoubleValue();
            
            Float playerHealth = (Float) getField(player, "health");
            if (playerHealth != null && playerHealth <= 0) {
                // Auto respawn - trigger respawn button
                Object mc = getMinecraft();
                if (mc != null) {
                    callMethod(mc, "displayGuiScreen", new Class<?>[]{Object.class}, null);
                }
            }
        } catch (Exception e) {
            // Silent fail
        }
    }
}


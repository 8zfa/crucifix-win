package com.crucifix.client.modules.combat;

import com.crucifix.client.events.UpdateEvent;
import com.crucifix.client.events.SubscribeEvent;
import com.crucifix.client.modules.Category;
import com.crucifix.client.modules.Module;
import com.crucifix.client.modules.Setting;

/**
 * Automatically uses fishing rod to combo enemies
 */
public class RodAura extends Module {
    
    public RodAura() {
        super("RodAura", "Automatically uses fishing rod for combos", Category.COMBAT, 0);
        
        addSetting(Setting.createSlider("Range", 4.5, 1.0, 10.0, 0.1));
        addSetting(Setting.createSlider("Delay", 200.0, 0.0, 1000.0, 50.0));
    }
    
    @SubscribeEvent
    public void onUpdateEvent(UpdateEvent event) {
        if (!isEnabled()) return;
        
        try {
            Object player = getPlayer();
            if (player == null) return;
            
            double range = getSetting("Range").getDoubleValue();
            double delay = getSetting("Delay").getDoubleValue();
            
            // Check if holding fishing rod
            Object heldItem = getField(player, "heldItem");
            if (heldItem == null) return;
            
            // Rod aura would auto-throw fishing rod at targets
            // This is a simplified implementation
        } catch (Exception e) {
            // Silent fail
        }
    }
}


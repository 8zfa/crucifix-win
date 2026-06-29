package com.crucifix.client.modules.combat;

import com.crucifix.client.events.UpdateEvent;
import com.crucifix.client.events.SubscribeEvent;
import com.crucifix.client.modules.Category;
import com.crucifix.client.modules.Module;
import com.crucifix.client.modules.Setting;

/**
 * Resets sprint after hitting to deal more knockback
 */
public class WTap extends Module {
    
    private boolean hasHit;
    
    public WTap() {
        super("WTap", "Resets sprint after hitting for more knockback", Category.COMBAT, 0);
        
        addSetting(Setting.createSlider("Delay", 100.0, 0.0, 500.0, 10.0));
    }
    
    @Override
    public void onEnable() {
        hasHit = false;
    }
    
    @SubscribeEvent
    public void onUpdateEvent(UpdateEvent event) {
        if (!isEnabled()) return;
        
        try {
            Object player = getPlayer();
            if (player == null) return;
            
            double delay = getSetting("Delay").getDoubleValue();
            
            // W-tap resets sprint after hitting to deal more knockback
            Boolean isSprinting = (Boolean) getField(player, "isSprinting");
            if (isSprinting != null && isSprinting) {
                // Reset sprint briefly
                setField(player, "isSprinting", false);
                hasHit = true;
            } else if (hasHit) {
                // Re-enable sprint after delay
                setField(player, "isSprinting", true);
                hasHit = false;
            }
        } catch (Exception e) {
            // Silent fail
        }
    }
}


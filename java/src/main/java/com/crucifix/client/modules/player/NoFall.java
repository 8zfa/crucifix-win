package com.crucifix.client.modules.player;

import com.crucifix.client.events.UpdateEvent;
import com.crucifix.client.events.SubscribeEvent;
import com.crucifix.client.modules.Category;
import com.crucifix.client.modules.Module;
import com.crucifix.client.modules.Setting;

/**
 * Prevents fall damage
 */
public class NoFall extends Module {
    
    public NoFall() {
        super("NoFall", "Prevents fall damage", Category.PLAYER, 78); // N key
        
        addSetting(Setting.createDropdown("Mode", new String[]{"Packet", "Spoof", "AAC"}));
    }
    
    @SubscribeEvent
    public void onUpdateEvent(UpdateEvent event) {
        if (!isEnabled()) return;
        
        try {
            Object player = getPlayer();
            if (player == null) return;
            
            String mode = getSetting("Mode").getStringValue();
            
            Boolean onGround = (Boolean) getField(player, "onGround");
            if (onGround != null && !onGround) {
                // Prevent fall damage by spoofing onGround
                setField(player, "onGround", true);
            }
        } catch (Exception e) {
            // Silent fail
        }
    }
}


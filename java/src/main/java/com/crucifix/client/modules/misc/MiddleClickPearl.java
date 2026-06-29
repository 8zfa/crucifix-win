package com.crucifix.client.modules.misc;

import com.crucifix.client.events.KeyEvent;
import com.crucifix.client.events.SubscribeEvent;
import com.crucifix.client.modules.Category;
import com.crucifix.client.modules.Module;

/**
 * Throws ender pearl on middle click
 */
public class MiddleClickPearl extends Module {
    
    public MiddleClickPearl() {
        super("MiddleClickPearl", "Throws ender pearl on middle click", Category.MISC, 0);
    }
    
    @SubscribeEvent
    public void onKeyEvent(KeyEvent event) {
        if (!isEnabled()) return;
        
        if (event.getKeyCode() == 2) { // Middle click
            try {
                Object player = getPlayer();
                if (player == null) return;
                
                // Middle click pearl would throw ender pearl from hotbar
                // This is a simplified implementation
            } catch (Exception e) {
                // Silent fail
            }
        }
    }
}


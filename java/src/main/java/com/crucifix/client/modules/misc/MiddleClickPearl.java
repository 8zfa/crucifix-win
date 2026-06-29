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
    
    @Override
    public void onKeyPress(int keyCode) {
        // Implementation would throw pearl on middle click
    }
    
    @SubscribeEvent
    public void onKeyEvent(KeyEvent event) {
        if (!isEnabled()) return;
        
        if (event.getKeyCode() == 2) { // Middle click
            // Middle click pearl logic would go here
        }
    }
}


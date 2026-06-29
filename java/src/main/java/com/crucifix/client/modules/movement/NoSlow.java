package com.crucifix.client.modules.movement;

import com.crucifix.client.events.UpdateEvent;
import com.crucifix.client.events.SubscribeEvent;
import com.crucifix.client.modules.Category;
import com.crucifix.client.modules.Module;
import com.crucifix.client.modules.Setting;

/**
 * Prevents slowdown from items
 */
public class NoSlow extends Module {
    
    public NoSlow() {
        super("NoSlow", "Prevents slowdown from items", Category.MOVEMENT, 78); // N key
        
        addSetting(Setting.createToggle("Items", true));
        addSetting(Setting.createToggle("SoulSand", true));
    }
    
    @Override
    public void onUpdate() {
        // Implementation would prevent slowdown
    }
    
    @SubscribeEvent
    public void onUpdateEvent(UpdateEvent event) {
        if (!isEnabled()) return;
        
        boolean items = getSetting("Items").getBooleanValue();
        boolean soulSand = getSetting("SoulSand").getBooleanValue();
        
        // No slow logic would go here
    }
}


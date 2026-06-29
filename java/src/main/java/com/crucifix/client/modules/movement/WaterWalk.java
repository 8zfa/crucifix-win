package com.crucifix.client.modules.movement;

import com.crucifix.client.events.UpdateEvent;
import com.crucifix.client.events.SubscribeEvent;
import com.crucifix.client.modules.Category;
import com.crucifix.client.modules.Module;
import com.crucifix.client.modules.Setting;

/**
 * Walks on water
 */
public class WaterWalk extends Module {
    
    public WaterWalk() {
        super("WaterWalk", "Walks on water", Category.MOVEMENT, 0);
        
        addSetting(Setting.createDropdown("Mode", new String[]{"Vanilla", "Dolphin", "NCP"}));
    }
    
    @Override
    public void onUpdate() {
        // Implementation would enable walking on water
    }
    
    @SubscribeEvent
    public void onUpdateEvent(UpdateEvent event) {
        if (!isEnabled()) return;
        
        String mode = getSetting("Mode").getStringValue();
        
        // Water walk logic would go here
    }
}


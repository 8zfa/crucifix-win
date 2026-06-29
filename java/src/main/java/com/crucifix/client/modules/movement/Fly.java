package com.crucifix.client.modules.movement;

import com.crucifix.client.events.UpdateEvent;
import com.crucifix.client.events.SubscribeEvent;
import com.crucifix.client.modules.Category;
import com.crucifix.client.modules.Module;
import com.crucifix.client.modules.Setting;

/**
 * Enables flight mode
 */
public class Fly extends Module {
    
    public Fly() {
        super("Fly", "Enables flight mode", Category.MOVEMENT, 70); // F key
        
        addSetting(Setting.createSlider("Speed", 1.0, 0.1, 5.0, 0.1));
        addSetting(Setting.createDropdown("Mode", new String[]{"Vanilla", "Smooth", "Jetpack"}));
    }
    
    @Override
    public void onUpdate() {
        // Implementation would enable flight
    }
    
    @SubscribeEvent
    public void onUpdateEvent(UpdateEvent event) {
        if (!isEnabled()) return;
        
        double speed = getSetting("Speed").getDoubleValue();
        String mode = getSetting("Mode").getStringValue();
        
        // Fly logic would go here
    }
}


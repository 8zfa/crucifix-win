package com.crucifix.client.modules.player;

import com.crucifix.client.events.UpdateEvent;
import com.crucifix.client.events.SubscribeEvent;
import com.crucifix.client.modules.Category;
import com.crucifix.client.modules.Module;
import com.crucifix.client.modules.Setting;

/**
 * Automatically eats food
 */
public class AutoEat extends Module {
    
    public AutoEat() {
        super("AutoEat", "Automatically eats food", Category.PLAYER, 0);
        
        addSetting(Setting.createSlider("Hunger", 18.0, 1.0, 20.0, 1.0));
        addSetting(Setting.createDropdown("Priority", new String[]{"Saturation", "Hunger"}));
    }
    
    @Override
    public void onUpdate() {
        // Implementation would auto-eat food
    }
    
    @SubscribeEvent
    public void onUpdateEvent(UpdateEvent event) {
        if (!isEnabled()) return;
        
        double hunger = getSetting("Hunger").getDoubleValue();
        String priority = getSetting("Priority").getStringValue();
        
        // Auto eat logic would go here
    }
}


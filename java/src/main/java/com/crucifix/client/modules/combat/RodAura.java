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
    
    @Override
    public void onUpdate() {
        // Implementation would auto-throw fishing rod
    }
    
    @SubscribeEvent
    public void onUpdateEvent(UpdateEvent event) {
        if (!isEnabled()) return;
        
        double range = getSetting("Range").getDoubleValue();
        double delay = getSetting("Delay").getDoubleValue();
        
        // Rod aura logic would go here
    }
}


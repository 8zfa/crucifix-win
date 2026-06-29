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
    
    public AutoEat(String name, Category category, Object mc) {
        super(name, category, mc);
        
        addSetting(Setting.createSlider("Hunger", 18.0, 1.0, 20.0, 1.0));
        addSetting(Setting.createDropdown("Priority", new String[]{"Saturation", "Hunger"}));
    }
    
    @SubscribeEvent
    public void onUpdateEvent(UpdateEvent event) {
        if (!isEnabled()) return;
        
        try {
            Object player = getPlayer();
            if (player == null) return;
            
            double hunger = getSetting("Hunger").getDoubleValue();
            String priority = getSetting("Priority").getStringValue();
            
            Object foodStats = getField(player, "foodStats");
            if (foodStats != null) {
                Integer foodLevel = (Integer) getField(foodStats, "foodLevel");
                if (foodLevel != null && foodLevel < (int) hunger) {
                    // Auto eat would trigger here
                    // callMethod(player, "setItemInUse", new Class<?>[]{Object.class, int.class}, null, 0);
                }
            }
        } catch (Exception e) {
            // Silent fail
        }
    }
}


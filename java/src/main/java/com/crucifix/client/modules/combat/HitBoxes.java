package com.crucifix.client.modules.combat;

import com.crucifix.client.events.UpdateEvent;
import com.crucifix.client.events.SubscribeEvent;
import com.crucifix.client.modules.Category;
import com.crucifix.client.modules.Module;
import com.crucifix.client.modules.Setting;

/**
 * Expands entity hitboxes
 */
public class HitBoxes extends Module {
    
    public HitBoxes() {
        super("HitBoxes", "Expands entity hitboxes", Category.COMBAT, 0);
        
        addSetting(Setting.createSlider("Expand", 0.1, 0.0, 0.5, 0.01));
    }
    
    @SubscribeEvent
    public void onUpdateEvent(UpdateEvent event) {
        if (!isEnabled()) return;
        
        try {
            Object player = getPlayer();
            if (player == null) return;
            
            double expand = getSetting("Expand").getDoubleValue();
            
            // Expand player hitbox
            Float width = (Float) getField(player, "width");
            Float height = (Float) getField(player, "height");
            
            if (width != null && height != null) {
                setField(player, "width", width + (float) expand);
                setField(player, "height", height + (float) expand);
            }
        } catch (Exception e) {
            // Silent fail
        }
    }
    
    @Override
    public void onDisable() {
        try {
            Object player = getPlayer();
            if (player != null) {
                setField(player, "width", 0.6f); // Reset to default
                setField(player, "height", 1.8f); // Reset to default
            }
        } catch (Exception e) {
            // Silent fail
        }
    }
}


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
    
    public Fly(String name, Category category, Object mc) {
        super(name, category, mc);
        ; // F key
        
        addSetting(Setting.createSlider("Speed", 1.0, 0.1, 5.0, 0.1));
        addSetting(Setting.createDropdown("Mode", new String[]{"Vanilla", "Smooth", "Jetpack"}));
    }
    
    @SubscribeEvent
    public void onUpdateEvent(UpdateEvent event) {
        if (!isEnabled()) return;
        
        try {
            Object player = getPlayer();
            if (player == null) return;
            
            double speed = getSetting("Speed").getDoubleValue();
            String mode = getSetting("Mode").getStringValue();
            
            // Set player to flying
            setField(player, "capabilities", getField(player, "capabilities"));
            
            Float moveForward = (Float) getField(player, "moveForward");
            Float moveStrafe = (Float) getField(player, "moveStrafe");
            
            if (moveForward != null && moveStrafe != null) {
                // Apply fly speed based on movement
                float flySpeed = (float) speed;
                setField(player, "flySpeed", flySpeed);
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
                setField(player, "flySpeed", 0.05f); // Reset to default
            }
        } catch (Exception e) {
            // Silent fail
        }
    }
}



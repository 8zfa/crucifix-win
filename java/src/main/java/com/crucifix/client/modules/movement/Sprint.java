package com.crucifix.client.modules.movement;

import com.crucifix.client.events.UpdateEvent;
import com.crucifix.client.events.SubscribeEvent;
import com.crucifix.client.modules.Category;
import com.crucifix.client.modules.Module;
import com.crucifix.client.modules.Setting;

/**
 * Automatically sprints
 */
public class Sprint extends Module {
    
    public Sprint(String name, Category category, Object mc) {
        super(name, category, mc);
        ;
        
        addSetting(Setting.createToggle("AllDirections", false));
        addSetting(Setting.createToggle("Omnidirectional", false));
    }
    
    @SubscribeEvent
    public void onUpdateEvent(UpdateEvent event) {
        if (!isEnabled()) return;
        
        try {
            // Get player via LunarBridge
            Object player = getPlayer();
            if (player == null) return;
            
            // Check if player is moving
            Float moveForward = (Float) getField(player, "moveForward");
            Float moveStrafe = (Float) getField(player, "moveStrafe");
            
            if (moveForward == null || moveStrafe == null) return;
            
            boolean allDirections = getSetting("AllDirections").getBooleanValue();
            boolean omnidirectional = getSetting("Omnidirectional").getBooleanValue();
            
            // Check if player should be sprinting
            boolean shouldSprint = false;
            
            if (omnidirectional) {
                // Sprint in any direction if moving
                shouldSprint = Math.abs(moveForward) > 0.01f || Math.abs(moveStrafe) > 0.01f;
            } else if (allDirections) {
                // Sprint if moving forward or backward
                shouldSprint = Math.abs(moveForward) > 0.01f;
            } else {
                // Vanilla sprint - only when moving forward
                shouldSprint = moveForward > 0.01f;
            }
            
            // Check if player has enough food
            Object foodStats = getField(player, "foodStats");
            if (foodStats != null) {
                Integer foodLevel = (Integer) getField(foodStats, "foodLevel");
                if (foodLevel != null && foodLevel <= 6) {
                    shouldSprint = false;
                }
            }
            
            // Set sprinting state
            setField(player, "isSprinting", shouldSprint);
            
        } catch (Exception e) {
            // Silent fail to avoid spam
        }
    }
}



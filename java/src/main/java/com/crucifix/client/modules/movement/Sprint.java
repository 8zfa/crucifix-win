package com.crucifix.client.modules.movement;

import com.crucifix.client.events.UpdateEvent;
import com.crucifix.client.events.SubscribeEvent;
import com.crucifix.client.modules.Category;
import com.crucifix.client.modules.Module;
import com.crucifix.client.modules.Setting;

import java.lang.reflect.Field;

/**
 * Automatically sprints
 */
public class Sprint extends Module {
    
    private Field playerSprintingField;
    
    public Sprint() {
        super("Sprint", "Automatically sprints", Category.MOVEMENT, 0);
        
        addSetting(Setting.createToggle("AllDirections", false));
        addSetting(Setting.createToggle("Omnidirectional", false));
    }
    
    @Override
    public void onEnable() {
        try {
            // Cache the sprinting field for performance
            Class<?> entityPlayerClass = Class.forName("net.minecraft.entity.player.EntityPlayer");
            playerSprintingField = entityPlayerClass.getDeclaredField("isSprinting");
            playerSprintingField.setAccessible(true);
        } catch (Exception e) {
            System.out.println("[Sprint] Failed to initialize reflection: " + e.getMessage());
        }
    }
    
    @SubscribeEvent
    public void onUpdateEvent(UpdateEvent event) {
        if (!isEnabled()) return;
        
        try {
            // Get Minecraft instance
            Class<?> minecraftClass = Class.forName("net.minecraft.client.Minecraft");
            Object minecraft = minecraftClass.getMethod("getMinecraft").invoke(null);
            if (minecraft == null) return;
            
            // Get the player
            Object player = minecraftClass.getField("thePlayer").get(minecraft);
            if (player == null) return;
            
            // Check if player is moving
            Field moveForwardField = player.getClass().getDeclaredField("moveForward");
            moveForwardField.setAccessible(true);
            float moveForward = moveForwardField.getFloat(player);
            
            Field moveStrafeField = player.getClass().getDeclaredField("moveStrafe");
            moveStrafeField.setAccessible(true);
            float moveStrafe = moveStrafeField.getFloat(player);
            
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
            Field foodStatsField = player.getClass().getDeclaredField("foodStats");
            foodStatsField.setAccessible(true);
            Object foodStats = foodStatsField.get(player);
            if (foodStats != null) {
                Field foodLevelField = foodStats.getClass().getDeclaredField("foodLevel");
                foodLevelField.setAccessible(true);
                int foodLevel = foodLevelField.getInt(foodStats);
                
                if (foodLevel <= 6) {
                    shouldSprint = false;
                }
            }
            
            // Set sprinting state
            if (playerSprintingField != null) {
                playerSprintingField.setBoolean(player, shouldSprint);
            }
            
        } catch (Exception e) {
            // Silent fail to avoid spam
        }
    }
}


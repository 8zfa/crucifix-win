package com.crucifix.client.modules.movement;

import com.crucifix.client.events.UpdateEvent;
import com.crucifix.client.events.SubscribeEvent;
import com.crucifix.client.modules.Category;
import com.crucifix.client.modules.Module;
import com.crucifix.client.modules.Setting;

import java.lang.reflect.Field;

/**
 * Increases movement speed
 */
public class Speed extends Module {
    
    private Field moveForwardField;
    private Field moveStrafeField;
    private Field speedField;
    private Field jumpMovementFactorField;
    
    private int bhopTimer = 0;
    
    public Speed() {
        super("Speed", "Increases movement speed", Category.MOVEMENT, 71); // G key
        
        addSetting(Setting.createSlider("Speed", 1.3, 1.0, 3.0, 0.05));
        addSetting(Setting.createDropdown("Mode", new String[]{"BHop", "OnGround", "Hop", "Timer"}));
    }
    
    @Override
    public void onEnable() {
        bhopTimer = 0;
        try {
            // Cache fields for performance
            Class<?> entityPlayerClass = Class.forName("net.minecraft.entity.player.EntityPlayer");
            moveForwardField = entityPlayerClass.getDeclaredField("moveForward");
            moveStrafeField = entityPlayerClass.getDeclaredField("moveStrafe");
            speedField = entityPlayerClass.getDeclaredField("speed");
            jumpMovementFactorField = entityPlayerClass.getDeclaredField("jumpMovementFactor");
            
            moveForwardField.setAccessible(true);
            moveStrafeField.setAccessible(true);
            speedField.setAccessible(true);
            jumpMovementFactorField.setAccessible(true);
        } catch (Exception e) {
            System.out.println("[Speed] Failed to initialize reflection: " + e.getMessage());
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
            
            // Get settings
            double speedMultiplier = getSetting("Speed").getDoubleValue();
            String mode = getSetting("Mode").getStringValue();
            
            // Get movement values
            float moveForward = moveForwardField.getFloat(player);
            float moveStrafe = moveStrafeField.getFloat(player);
            
            // Check if player is moving
            boolean isMoving = Math.abs(moveForward) > 0.01f || Math.abs(moveStrafe) > 0.01f;
            if (!isMoving) {
                bhopTimer = 0;
                return;
            }
            
            // Get current speed
            float currentSpeed = speedField.getFloat(player);
            
            switch (mode) {
                case "BHop":
                    handleBHopMode(player, speedMultiplier, currentSpeed);
                    break;
                case "OnGround":
                    handleOnGroundMode(player, speedMultiplier, currentSpeed);
                    break;
                case "Hop":
                    handleHopMode(player, speedMultiplier, currentSpeed);
                    break;
                case "Timer":
                    handleTimerMode(player, speedMultiplier);
                    break;
            }
            
        } catch (Exception e) {
            // Silent fail to avoid spam
        }
    }
    
    private void handleBHopMode(Object player, double speedMultiplier, float currentSpeed) {
        try {
            // BHop mode - jump when on ground to maintain speed
            Field onGroundField = player.getClass().getDeclaredField("onGround");
            onGroundField.setAccessible(true);
            boolean onGround = onGroundField.getBoolean(player);
            
            if (onGround) {
                // Jump
                Field jumpField = player.getClass().getDeclaredField("jump");
                jumpField.setAccessible(true);
                jumpField.setFloat(player, 0.42f);
                
                // Apply speed boost
                float newSpeed = currentSpeed * (float) speedMultiplier;
                speedField.setFloat(player, newSpeed);
                
                bhopTimer = 0;
            } else {
                bhopTimer++;
                // Maintain speed in air
                if (bhopTimer > 2) {
                    float newSpeed = currentSpeed * 0.99f; // Slight slowdown in air
                    speedField.setFloat(player, newSpeed);
                }
            }
        } catch (Exception e) {
            // Silent fail
        }
    }
    
    private void handleOnGroundMode(Object player, double speedMultiplier, float currentSpeed) {
        try {
            // OnGround mode - just increase speed while on ground
            Field onGroundField = player.getClass().getDeclaredField("onGround");
            onGroundField.setAccessible(true);
            boolean onGround = onGroundField.getBoolean(player);
            
            if (onGround) {
                float newSpeed = currentSpeed * (float) speedMultiplier;
                speedField.setFloat(player, newSpeed);
            }
        } catch (Exception e) {
            // Silent fail
        }
    }
    
    private void handleHopMode(Object player, double speedMultiplier, float currentSpeed) {
        try {
            // Hop mode - small hops with speed boost
            Field onGroundField = player.getClass().getDeclaredField("onGround");
            onGroundField.setAccessible(true);
            boolean onGround = onGroundField.getBoolean(player);
            
            if (onGround) {
                // Small hop
                Field jumpField = player.getClass().getDeclaredField("jump");
                jumpField.setAccessible(true);
                jumpField.setFloat(player, 0.3f);
                
                // Apply speed boost
                float newSpeed = currentSpeed * (float) speedMultiplier;
                speedField.setFloat(player, newSpeed);
            }
        } catch (Exception e) {
            // Silent fail
        }
    }
    
    private void handleTimerMode(Object player, double speedMultiplier) {
        try {
            // Timer mode - modify game tick speed
            Class<?> minecraftClass = Class.forName("net.minecraft.client.Minecraft");
            Object minecraft = minecraftClass.getMethod("getMinecraft").invoke(null);
            
            Field timerField = minecraftClass.getDeclaredField("timer");
            timerField.setAccessible(true);
            Object timer = timerField.get(minecraft);
            
            Field timerSpeedField = timer.getClass().getDeclaredField("timerSpeed");
            timerSpeedField.setAccessible(true);
            timerSpeedField.setFloat(timer, (float) speedMultiplier);
        } catch (Exception e) {
            // Silent fail
        }
    }
    
    @Override
    public void onDisable() {
        // Reset timer speed if using timer mode
        try {
            Class<?> minecraftClass = Class.forName("net.minecraft.client.Minecraft");
            Object minecraft = minecraftClass.getMethod("getMinecraft").invoke(null);
            
            Field timerField = minecraftClass.getDeclaredField("timer");
            timerField.setAccessible(true);
            Object timer = timerField.get(minecraft);
            
            Field timerSpeedField = timer.getClass().getDeclaredField("timerSpeed");
            timerSpeedField.setAccessible(true);
            timerSpeedField.setFloat(timer, 1.0f);
        } catch (Exception e) {
            // Silent fail
        }
        
        bhopTimer = 0;
    }
}


package com.crucifix.client.modules.movement;

import com.crucifix.client.events.UpdateEvent;
import com.crucifix.client.events.SubscribeEvent;
import com.crucifix.client.modules.Category;
import com.crucifix.client.modules.Module;
import com.crucifix.client.modules.Setting;

/**
 * Increases movement speed
 */
public class Speed extends Module {
    
    private int bhopTimer = 0;
    
    public Speed(String name, Category category, Object mc) {
        super(name, category, mc);
        
        addSetting(Setting.createSlider("Speed", 1.3, 1.0, 3.0, 0.05));
        addSetting(Setting.createDropdown("Mode", new String[]{"BHop", "OnGround", "Hop", "Timer"}));
    }
    
    @Override
    public void onEnable() {
        bhopTimer = 0;
    }
    
    @SubscribeEvent
    public void onUpdateEvent(UpdateEvent event) {
        if (!isEnabled()) return;
        
        try {
            // Get player via LunarBridge
            Object player = getPlayer();
            if (player == null) return;
            
            // Get settings
            double speedMultiplier = getSetting("Speed").getDoubleValue();
            String mode = getSetting("Mode").getStringValue();
            
            // Get movement values
            Float moveForward = (Float) getField(player, "moveForward");
            Float moveStrafe = (Float) getField(player, "moveStrafe");
            
            if (moveForward == null || moveStrafe == null) return;
            
            // Check if player is moving
            boolean isMoving = Math.abs(moveForward) > 0.01f || Math.abs(moveStrafe) > 0.01f;
            if (!isMoving) {
                bhopTimer = 0;
                return;
            }
            
            // Get current speed
            Float currentSpeed = (Float) getField(player, "speed");
            if (currentSpeed == null) return;
            
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
            Boolean onGround = (Boolean) getField(player, "onGround");
            if (onGround == null) return;
            
            if (onGround) {
                // Jump
                setField(player, "jump", 0.42f);
                
                // Apply speed boost
                float newSpeed = currentSpeed * (float) speedMultiplier;
                setField(player, "speed", newSpeed);
                
                bhopTimer = 0;
            } else {
                bhopTimer++;
                // Maintain speed in air
                if (bhopTimer > 2) {
                    float newSpeed = currentSpeed * 0.99f; // Slight slowdown in air
                    setField(player, "speed", newSpeed);
                }
            }
        } catch (Exception e) {
            // Silent fail
        }
    }
    
    private void handleOnGroundMode(Object player, double speedMultiplier, float currentSpeed) {
        try {
            // OnGround mode - just increase speed while on ground
            Boolean onGround = (Boolean) getField(player, "onGround");
            if (onGround == null || !onGround) return;
            
            float newSpeed = currentSpeed * (float) speedMultiplier;
            setField(player, "speed", newSpeed);
        } catch (Exception e) {
            // Silent fail
        }
    }
    
    private void handleHopMode(Object player, double speedMultiplier, float currentSpeed) {
        try {
            // Hop mode - small hops with speed boost
            Boolean onGround = (Boolean) getField(player, "onGround");
            if (onGround == null) return;
            
            if (onGround) {
                // Small hop
                setField(player, "jump", 0.3f);
                
                // Apply speed boost
                float newSpeed = currentSpeed * (float) speedMultiplier;
                setField(player, "speed", newSpeed);
            }
        } catch (Exception e) {
            // Silent fail
        }
    }
    
    private void handleTimerMode(Object player, double speedMultiplier) {
        try {
            // Timer mode - modify game tick speed
            Object mc = getMinecraft();
            if (mc == null) return;
            
            Object timer = getField(mc, "timer");
            if (timer == null) return;
            
            setField(timer, "timerSpeed", (float) speedMultiplier);
        } catch (Exception e) {
            // Silent fail
        }
    }
    
    @Override
    public void onDisable() {
        // Reset timer speed if using timer mode
        try {
            Object mc = getMinecraft();
            if (mc == null) return;
            
            Object timer = getField(mc, "timer");
            if (timer == null) return;
            
            setField(timer, "timerSpeed", 1.0f);
        } catch (Exception e) {
            // Silent fail
        }
        
        bhopTimer = 0;
    }
}


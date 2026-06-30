package com.crucifix.client.modules;

import com.crucifix.client.core.EventBus;
import com.crucifix.client.Crucifix;
import com.crucifix.client.LunarBridge;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Base class for all modules - uses LunarBridge for Minecraft access
 */
public abstract class Module {
    private String name;
    private Category category;
    private int keyBind;
    private boolean enabled;
    private Object minecraftInstance;
    private Object playerInstance;
    private Object worldInstance;
    
    public Module(String name, Category category, Object mc) {
        this.name = name;
        this.category = category;
        this.minecraftInstance = mc;
        this.keyBind = -1;
        
        // Cache player and world using reflection
        if (mc != null) {
            try {
                // Get thePlayer field
                Class<?> mcClass = mc.getClass();
                java.lang.reflect.Field playerField = mcClass.getField("thePlayer");
                playerInstance = playerField.get(mc);
                
                java.lang.reflect.Field worldField = mcClass.getField("theWorld");
                worldInstance = worldField.get(mc);
                
                System.out.println("[Module] " + name + " - Got player: " + playerInstance);
            } catch (Exception e) {
                System.out.println("[Module] " + name + " - Could not get player: " + e.getMessage());
            }
        }
    }
    
    // Helper methods for subclasses
    protected Object getPlayer() {
        if (playerInstance == null && minecraftInstance != null) {
            try {
                java.lang.reflect.Field playerField = minecraftInstance.getClass().getField("thePlayer");
                playerInstance = playerField.get(minecraftInstance);
            } catch (Exception e) {
                System.out.println("[Module] " + name + " - Could not refresh player: " + e.getMessage());
            }
        }
        return playerInstance;
    }
    
    protected Object getWorld() {
        if (worldInstance == null && minecraftInstance != null) {
            try {
                java.lang.reflect.Field worldField = minecraftInstance.getClass().getField("theWorld");
                worldInstance = worldField.get(minecraftInstance);
            } catch (Exception e) {
                System.out.println("[Module] " + name + " - Could not refresh world: " + e.getMessage());
            }
        }
        return worldInstance;
    }
    
    // Reflection helpers
    protected Object getFieldValue(Object target, String fieldName) {
        try {
            java.lang.reflect.Field field = target.getClass().getField(fieldName);
            field.setAccessible(true);
            return field.get(target);
        } catch (Exception e) {
            try {
                java.lang.reflect.Field field = target.getClass().getDeclaredField(fieldName);
                field.setAccessible(true);
                return field.get(target);
            } catch (Exception ex) {
                System.out.println("[Module] Could not get field " + fieldName + ": " + ex.getMessage());
                return null;
            }
        }
    }
    
    protected void setFieldValue(Object target, String fieldName, Object value) {
        try {
            java.lang.reflect.Field field = target.getClass().getField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (Exception e) {
            try {
                java.lang.reflect.Field field = target.getClass().getDeclaredField(fieldName);
                field.setAccessible(true);
                field.set(target, value);
            } catch (Exception ex) {
                System.out.println("[Module] Could not set field " + fieldName + ": " + ex.getMessage());
            }
        }
    }
    
    protected Object callMethod(Object target, String methodName, Object... args) {
        try {
            Class<?>[] paramTypes = new Class[args.length];
            for (int i = 0; i < args.length; i++) {
                paramTypes[i] = args[i].getClass();
            }
            
            java.lang.reflect.Method method = target.getClass().getMethod(methodName, paramTypes);
            method.setAccessible(true);
            return method.invoke(target, args);
        } catch (Exception e) {
            // Try to find method without exact types
            try {
                for (java.lang.reflect.Method m : target.getClass().getMethods()) {
                    if (m.getName().equals(methodName) && m.getParameterCount() == args.length) {
                        m.setAccessible(true);
                        return m.invoke(target, args);
                    }
                }
            } catch (Exception ex) {
                System.out.println("[Module] Could not call method " + methodName + ": " + ex.getMessage());
            }
            return null;
        }
    }
    
    protected boolean getBooleanField(Object target, String fieldName) {
        Object value = getFieldValue(target, fieldName);
        return value instanceof Boolean && (Boolean) value;
    }
    
    protected float getFloatField(Object target, String fieldName) {
        Object value = getFieldValue(target, fieldName);
        return value instanceof Float ? (Float) value : 0f;
    }
    
    protected double getDoubleField(Object target, String fieldName) {
        Object value = getFieldValue(target, fieldName);
        return value instanceof Double ? (Double) value : 0.0;
    }
    
    protected int getIntField(Object target, String fieldName) {
        Object value = getFieldValue(target, fieldName);
        return value instanceof Integer ? (Integer) value : 0;
    }
    
    protected long getLongField(Object target, String fieldName) {
        Object value = getFieldValue(target, fieldName);
        return value instanceof Long ? (Long) value : 0L;
    }
    
    // Aliases for backward compatibility
    protected Object getField(Object target, String fieldName) {
        return getFieldValue(target, fieldName);
    }
    
    protected void setField(Object target, String fieldName, Object value) {
        setFieldValue(target, fieldName, value);
    }
    
    protected Object getMinecraft() {
        return minecraftInstance;
    }
    
    // Override these
    public void onEnable() {}
    public void onDisable() {}
    public void onUpdate() {}
    public void onRender() {}
    public void onKeyPress(int key) {}
    
    // Getters/Setters
    public String getName() { return name; }
    public Category getCategory() { return category; }
    public int getKeyBind() { return keyBind; }
    public void setKeyBind(int keyBind) { this.keyBind = keyBind; }
    public boolean isEnabled() { return enabled; }

    // Aliases for backward compatibility
    public void setKeybind(int key) { this.keyBind = key; }
    public int getKeybind() { return keyBind; }
    
    public void toggle() {
        if (enabled) {
            disable();
        } else {
            enable();
        }
    }
    
    public void enable() {
        enabled = true;
        onEnable();
        System.out.println("[Module] " + name + " enabled");
    }
    
    public void disable() {
        enabled = false;
        onDisable();
        System.out.println("[Module] " + name + " disabled");
    }
    
    protected boolean isInGame() {
        Object player = getPlayer();
        Object world = getWorld();
        return player != null && world != null;
    }
    
    // Legacy compatibility
    private final String description = "";
    private final Map<String, Setting<?>> settings = new HashMap<>();
    private final EventBus eventBus = Crucifix.getInstance().getEventBus();
    
    protected Setting<?> addSetting(Setting<?> setting) {
        settings.put(setting.getName(), setting);
        return setting;
    }
    
    @SuppressWarnings("unchecked")
    public <T> Setting<T> getSetting(String name) {
        return (Setting<T>) settings.get(name);
    }
    
    public List<Setting<?>> getSettings() {
        return new ArrayList<>(settings.values());
    }
    
    public String getDescription() { return description; }
    public void setEnabled(boolean enabled) {
        if (enabled && !this.enabled) {
            enable();
        } else if (!enabled && this.enabled) {
            disable();
        }
    }
}

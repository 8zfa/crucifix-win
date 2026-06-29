package com.crucifix.client.modules;

import com.crucifix.client.core.EventBus;
import com.crucifix.client.Crucifix;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Base class for all modules
 */
public abstract class Module {
    private final String name;
    private final String description;
    private final Category category;
    private int keybind;
    private boolean enabled;
    
    private final Map<String, Setting<?>> settings;
    private final EventBus eventBus;
    
    public Module(String name, String description, Category category, int defaultKeybind) {
        this.name = name;
        this.description = description;
        this.category = category;
        this.keybind = defaultKeybind;
        this.enabled = false;
        this.settings = new HashMap<>();
        this.eventBus = Crucifix.getInstance().getEventBus();
    }
    
    /**
     * Called when the module is enabled
     */
    public void onEnable() {
        eventBus.register(this);
    }
    
    /**
     * Called when the module is disabled
     */
    public void onDisable() {
        eventBus.unregister(this);
    }
    
    /**
     * Called every tick when the module is enabled
     */
    public void onUpdate() {}
    
    /**
     * Called during rendering when the module is enabled
     */
    public void onRender(float partialTicks) {}
    
    /**
     * Called when a key is pressed
     */
    public void onKeyPress(int keyCode) {}
    
    /**
     * Toggle the module
     */
    public void toggle() {
        if (enabled) {
            disable();
        } else {
            enable();
        }
    }
    
    /**
     * Enable the module
     */
    public void enable() {
        if (!enabled) {
            enabled = true;
            onEnable();
            System.out.println("[Module] Enabled: " + name);
        }
    }
    
    /**
     * Disable the module
     */
    public void disable() {
        if (enabled) {
            enabled = false;
            onDisable();
            System.out.println("[Module] Disabled: " + name);
        }
    }
    
    /**
     * Add a setting to this module
     */
    protected Setting<?> addSetting(Setting<?> setting) {
        settings.put(setting.getName(), setting);
        return setting;
    }
    
    /**
     * Get a setting by name
     */
    @SuppressWarnings("unchecked")
    public <T> Setting<T> getSetting(String name) {
        return (Setting<T>) settings.get(name);
    }
    
    /**
     * Get all settings
     */
    public List<Setting<?>> getSettings() {
        return new ArrayList<>(settings.values());
    }
    
    // Getters and setters
    public String getName() {
        return name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public Category getCategory() {
        return category;
    }
    
    public int getKeybind() {
        return keybind;
    }
    
    public void setKeybind(int keybind) {
        this.keybind = keybind;
    }
    
    public boolean isEnabled() {
        return enabled;
    }
    
    public void setEnabled(boolean enabled) {
        if (enabled && !this.enabled) {
            enable();
        } else if (!enabled && this.enabled) {
            disable();
        }
    }
}

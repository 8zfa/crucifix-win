package com.crucifix.client.hud;

import com.crucifix.client.Crucifix;
import com.crucifix.client.modules.Module;

import java.util.ArrayList;
import java.util.List;

/**
 * Manages all HUD components
 */
public class HUDManager {
    private static HUDManager instance;
    private List<HUDComponent> components;
    private List<Notification> notifications;
    
    public HUDManager() {
        this.components = new ArrayList<>();
        this.notifications = new ArrayList<>();
        initializeComponents();
    }
    
    public static HUDManager getInstance() {
        if (instance == null) {
            instance = new HUDManager();
        }
        return instance;
    }
    
    private void initializeComponents() {
        components.add(new Watermark());
        components.add(new Arraylist());
        components.add(new Keystrokes());
        components.add(new ArmorStatus());
        components.add(new PotionStatus(10, 80));
        components.add(new Direction(10, 150));
    }
    
    public void addNotification(String message, long duration) {
        notifications.add(new Notification(message, duration));
    }
    
    public void update() {
        // Update notifications
        notifications.removeIf(n -> n.isExpired());
        for (Notification notification : notifications) {
            notification.update();
        }
        
        // Update components
        for (HUDComponent component : components) {
            if (component.isEnabled()) {
                component.update();
            }
        }
    }
    
    public void render(float partialTicks) {
        // Render notifications (simplified - just log for now)
        // Actual rendering would be done via Minecraft's font renderer
        if (!notifications.isEmpty()) {
            // Notifications are tracked and will be rendered properly
        }
        
        // Render components
        for (HUDComponent component : components) {
            if (component.isEnabled()) {
                component.render(partialTicks);
            }
        }
    }
    
    public List<HUDComponent> getComponents() {
        return new ArrayList<>(components);
    }
    
    public List<Notification> getNotifications() {
        return new ArrayList<>(notifications);
    }
}


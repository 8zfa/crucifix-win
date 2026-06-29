package com.crucifix.client.gui;

import com.crucifix.client.Crucifix;
import com.crucifix.client.gui.components.Panel;
import com.crucifix.client.gui.themes.Theme;
import com.crucifix.client.gui.themes.CrucifixDark;
import com.crucifix.client.modules.Category;

import java.util.ArrayList;
import java.util.List;

public class ClickGUI {
    private static ClickGUI instance;
    private List<Panel> panels;
    private boolean open = false;
    private Theme theme;
    private float animationProgress = 0f;
    
    private ClickGUI() {
        System.out.println("[ClickGUI] Creating instance...");
        theme = new CrucifixDark();
        panels = new ArrayList<>();
        
        // Create panels for each category
        for (Category category : Category.values()) {
            Panel panel = new Panel(category.getDisplayName(), 10, 10, 140, 300, category);
            panels.add(panel);
        }
        
        System.out.println("[ClickGUI] Created " + panels.size() + " panels");
    }
    
    public static ClickGUI getInstance() {
        if (instance == null) {
            instance = new ClickGUI();
        }
        return instance;
    }
    
    public Theme getCurrentTheme() {
        return theme;
    }
    
    public boolean isOpen() {
        return open;
    }
    
    public void setOpen(boolean open) {
        this.open = open;
    }
    
    // This is called from the wglSwapBuffers hook
    public void render() {
        System.out.println("[ClickGUI] render() called, open=" + open);
        
        if (!open) {
            return;
        }
        
        try {
            System.out.println("[ClickGUI] Checking ImGui availability...");
            
            // Check if ImGui is available
            boolean imGuiAvailable = isImGuiAvailable();
            System.out.println("[ClickGUI] ImGui available: " + imGuiAvailable);
            
            if (!imGuiAvailable) {
                System.out.println("[ClickGUI] ImGui not available yet, skipping render");
                return;
            }
            
            System.out.println("[ClickGUI] Starting render...");
            
            // Animation
            if (animationProgress < 1f) {
                animationProgress = Math.min(1f, animationProgress + 0.05f);
            }
            
            float scale = 0.8f + (0.2f * animationProgress);
            float alpha = animationProgress;
            
            // Use ImGui to render panels
            for (Panel panel : panels) {
                panel.render(0, 0, 0);
            }
            
            System.out.println("[ClickGUI] Render complete");
            
        } catch (Exception e) {
            System.out.println("[ClickGUI] Render error: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    // JNI method to check if ImGui is initialized
    private native boolean isImGuiAvailable();
    
    public void toggle() {
        open = !open;
        animationProgress = 0f;
        System.out.println("[ClickGUI] Toggled: " + open);
    }
    
    public Theme getTheme() {
        return theme;
    }
    
    public void setTheme(Theme theme) {
        this.theme = theme;
        for (Panel panel : panels) {
            panel.setTheme(theme);
        }
    }
    
    public List<Panel> getPanels() {
        return panels;
    }
}


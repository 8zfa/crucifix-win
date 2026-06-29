package com.crucifix.client.gui;

import com.crucifix.client.core.ModuleManager;
import com.crucifix.client.modules.Category;
import com.crucifix.client.modules.Module;
import com.crucifix.client.gui.themes.CrucifixDark;
import com.crucifix.client.gui.themes.Theme;

import java.util.ArrayList;
import java.util.List;

public class ClickGUI {
    private static ClickGUI instance;
    private boolean open = false;
    private Theme theme;
    private float animationProgress = 0f;
    private boolean imGuiAvailable = false;
    
    private ClickGUI() {
        System.out.println("[ClickGUI] Creating instance...");
        theme = new CrucifixDark();
        
        // Check ImGui availability
        imGuiAvailable = isImGuiAvailable();
        System.out.println("[ClickGUI] ImGui available: " + imGuiAvailable);
    }
    
    public static ClickGUI getInstance() {
        if (instance == null) {
            instance = new ClickGUI();
        }
        return instance;
    }
    
    // Called from C++ wglSwapBuffers hook
    public void render() {
        if (!open) {
            // Still call this occasionally to check ImGui status
            if (!imGuiAvailable) {
                imGuiAvailable = isImGuiAvailable();
                if (imGuiAvailable) {
                    System.out.println("[ClickGUI] ImGui now available!");
                }
            }
            return;
        }
        
        try {
            // Update ImGui status
            imGuiAvailable = isImGuiAvailable();
            
            if (!imGuiAvailable) {
                System.out.println("[ClickGUI] render() called but ImGui not available");
                return;
            }
            
            // Animation
            if (animationProgress < 1f) {
                animationProgress = Math.min(1f, animationProgress + 0.05f);
            }
            
            float alpha = animationProgress;
            
            // === ACTUAL IMGUI RENDERING ===
            nSetNextWindowSize(400, 300);
            nSetNextWindowPos(50, 50);
            nSetNextWindowBgAlpha(alpha);
            
            nBegin("Crucifix Client", 0);
            
            nText("ClickGUI is open!");
            nSeparator();
            
            // Show all modules from all categories
            nText("Modules: " + ModuleManager.getInstance().getModules().size());
            nSeparator();
            
            // Draw modules by category
            for (Category category : Category.values()) {
                String categoryName = category.toString();
                if (nCollapsingHeader(categoryName)) {
                    for (Module module : ModuleManager.getInstance().getModules()) {
                        if (module.getCategory() == category) {
                            boolean enabled = module.isEnabled();
                            if (nCheckbox(module.getName(), enabled)) {
                                module.toggle();
                            }
                        }
                    }
                }
            }
            
            nEnd();
            
        } catch (Exception e) {
            System.out.println("[ClickGUI] Render error: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public void toggle() {
        open = !open;
        animationProgress = 0f;
        System.out.println("[ClickGUI] Toggled: " + open);
    }
    
    public boolean isOpen() {
        return open;
    }
    
    public Theme getCurrentTheme() {
        return theme;
    }
    
    public void setTheme(Theme theme) {
        this.theme = theme;
    }
    
    // === NATIVE IMGUI METHODS ===
    // These call the C++ ImGui implementation
    
    private native boolean isImGuiAvailable();
    
    // ImGui drawing methods
    private native void nBegin(String name, int flags);
    private native void nEnd();
    private native void nText(String text);
    private native void nSeparator();
    private native boolean nCollapsingHeader(String label);
    private native boolean nCheckbox(String label, boolean value);
    private native boolean nButton(String label);
    private native void nSameLine();
    private native void nPushStyleColor(int idx, float r, float g, float b, float a);
    private native void nPopStyleColor();
    private native void nPushStyleVar(int idx, float value);
    private native void nPopStyleVar();
    private native void nSetNextWindowSize(float w, float h);
    private native void nSetNextWindowPos(float x, float y);
    private native void nSetNextWindowBgAlpha(float alpha);
}


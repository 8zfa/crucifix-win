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
    private int renderCount = 0;
    
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
        // Always print the first 5 times so we know it's being called
        if (renderCount < 5) {
            System.out.println("[ClickGUI] render() called #" + renderCount + ", open=" + open);
            renderCount++;
        }
        
        if (!open) {
            // Still check ImGui status occasionally
            if (!imGuiAvailable) {
                imGuiAvailable = isImGuiAvailable();
                if (imGuiAvailable && renderCount < 5) {
                    System.out.println("[ClickGUI] ImGui now available!");
                }
            }
            return;
        }
        
        try {
            // Update ImGui status
            imGuiAvailable = isImGuiAvailable();
            if (renderCount < 5) {
                System.out.println("[ClickGUI] ImGui available: " + imGuiAvailable);
            }
            
            if (!imGuiAvailable) {
                if (renderCount < 5) {
                    System.out.println("[ClickGUI] ImGui not available, skipping render");
                }
                return;
            }
            
            // === RENDER A SIMPLE TEST WINDOW ===
            nSetNextWindowSize(300, 200);
            nSetNextWindowPos(50, 50);
            
            nBegin("Crucifix Client", 0);
            nText("ClickGUI is working!");
            nSeparator();
            nText("Press RSHIFT to close");
            nSeparator();
            nText("Render count: " + renderCount);
            nEnd();
            
            // === RENDER DEBUG MODULE STATUS WINDOW ===
            nSetNextWindowSize(350, 400);
            nSetNextWindowPos(400, 50);
            
            nBegin("Module Status Debug", 0);
            nText("Total Modules: " + ModuleManager.getInstance().getModules().size());
            nSeparator();
            
            int enabledCount = 0;
            for (Module module : ModuleManager.getInstance().getModules()) {
                if (module.isEnabled()) {
                    enabledCount++;
                    nText("[ENABLED] " + module.getName());
                } else {
                    nText("[DISABLED] " + module.getName());
                }
            }
            
            nSeparator();
            nText("Enabled: " + enabledCount + " / " + ModuleManager.getInstance().getModules().size());
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


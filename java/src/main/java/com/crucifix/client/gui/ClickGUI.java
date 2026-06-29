package com.crucifix.client.gui;

import com.crucifix.client.core.ModuleManager;
import com.crucifix.client.gui.themes.CrucifixDark;
import com.crucifix.client.gui.themes.Theme;

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
        System.out.println("[ClickGUI] Created instance");
    }
    
    public static ClickGUI getInstance() {
        if (instance == null) {
            instance = new ClickGUI();
        }
        return instance;
    }
    
    public void render() {
        if (renderCount < 5) {
            System.out.println("[ClickGUI] render() called #" + renderCount + ", open=" + open);
            renderCount++;
        }
        
        if (!open) return;
        
        imGuiAvailable = isImGuiAvailable();
        if (renderCount < 5) {
            System.out.println("[ClickGUI] ImGui available: " + imGuiAvailable);
        }
        
        if (!imGuiAvailable) return;
        
        try {
            if (animationProgress < 1f) {
                animationProgress = Math.min(1f, animationProgress + 0.05f);
            }
            
            nBegin("Crucifix Client", 0);
            nText("ClickGUI is working!");
            nSeparator();
            nText("Press RSHIFT to close");
            nText("Modules loaded: " + ModuleManager.getInstance().getModules().size());
            nEnd();
        } catch (Exception e) {
            System.out.println("[ClickGUI] Render error: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public void toggle() {
        open = !open;
        animationProgress = 0f;
        renderCount = 0;
        System.out.println("[ClickGUI] Toggled: " + open);
    }
    
    public boolean isOpen() {
        return open;
    }
    
    public Theme getCurrentTheme() {
        return theme;
    }
    
    private native boolean isImGuiAvailable();
    private native void nBegin(String name, int flags);
    private native void nEnd();
    private native void nText(String text);
    private native void nSeparator();
}


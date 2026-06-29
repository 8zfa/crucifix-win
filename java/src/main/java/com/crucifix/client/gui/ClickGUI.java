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
        
        // Check ImGui availability
        try {
            imGuiAvailable = isImGuiAvailable();
            System.out.println("[ClickGUI] ImGui available: " + imGuiAvailable);
        } catch (Throwable t) {
            System.out.println("[ClickGUI] ImGui check failed: " + t.getMessage());
            imGuiAvailable = false;
        }
    }
    
    public static ClickGUI getInstance() {
        if (instance == null) {
            System.out.println("[ClickGUI] Creating new instance...");
            instance = new ClickGUI();
        }
        return instance;
    }
    
    // Called from C++ wglSwapBuffers hook
    public void render() {
        renderCount++;
        if (renderCount <= 5) {
            System.out.println("[ClickGUI] render() #" + renderCount + ", open=" + open + ", imGuiAvailable=" + imGuiAvailable);
        }
        
        if (!open) return;
        
        try {
            imGuiAvailable = isImGuiAvailable();
            if (renderCount <= 5) {
                System.out.println("[ClickGUI] ImGui available: " + imGuiAvailable);
            }
            
            if (!imGuiAvailable) return;
            
            // Animation
            if (animationProgress < 1f) {
                animationProgress = Math.min(1f, animationProgress + 0.05f);
            }
            
            // === RENDER TEST WINDOW ===
            nBegin("Crucifix Client", 0);
            nText("ClickGUI is working!");
            nSeparator();
            nText("Press RSHIFT to close");
            nText("Modules: " + ModuleManager.getInstance().getModules().size());
            nEnd();
            
            if (renderCount == 1) {
                System.out.println("[ClickGUI] First successful render!");
            }
            
        } catch (Throwable t) {
            if (renderCount <= 5) {
                System.out.println("[ClickGUI] Render error: " + t.getMessage());
                t.printStackTrace();
            }
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
    
    // Native methods - these are registered in C++
    private native boolean isImGuiAvailable();
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


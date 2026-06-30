package com.crucifix.client.gui;

import com.crucifix.client.core.ModuleManager;
import com.crucifix.client.gui.themes.CrucifixDark;
import com.crucifix.client.gui.themes.Theme;

import java.io.FileWriter;

public class ClickGUI {
    private static ClickGUI instance;
    private boolean open = false;
    private Theme theme;
    private float animationProgress = 0f;
    private boolean imGuiAvailable = false;
    private int renderCount = 0;
    private boolean fallbackActive = false; // if ImGui fails, use Java rendering

    // STATIC BLOCK to confirm class load
    static {
        try {
            FileWriter fw = new FileWriter("C:\\crucifix_clickgui_loaded.txt");
            fw.write("ClickGUI class loaded at " + System.currentTimeMillis());
            fw.close();
            System.out.println("[ClickGUI] Static initializer executed, file written.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

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

        // If ImGui is unavailable, enable fallback rendering
        if (!imGuiAvailable) {
            System.out.println("[ClickGUI] ImGui not available, enabling fallback Java rendering.");
            fallbackActive = true;
        }
    }

    public static ClickGUI getInstance() {
        if (instance == null) {
            System.out.println("[ClickGUI] Creating new instance via getInstance()...");
            instance = new ClickGUI();
        }
        return instance;
    }

    // Called from C++ wglSwapBuffers hook
    public void render() {
        renderCount++;
        if (renderCount <= 10) {
            System.out.println("[ClickGUI] render() #" + renderCount + ", open=" + open + ", imGuiAvailable=" + imGuiAvailable + ", fallback=" + fallbackActive);
        }

        if (!open) return;

        // --- FALLBACK: render using console logging (for testing) ---
        if (fallbackActive || !imGuiAvailable) {
            renderFallback();
            return;
        }

        // --- NORMAL ImGui rendering ---
        try {
            // Refresh ImGui availability
            imGuiAvailable = isImGuiAvailable();
            if (!imGuiAvailable) {
                // switch to fallback permanently if ImGui disappeared
                fallbackActive = true;
                renderFallback();
                return;
            }

            // Animation
            if (animationProgress < 1f) {
                animationProgress = Math.min(1f, animationProgress + 0.05f);
            }

            // Begin ImGui window
            nBegin("Crucifix Client", 0);
            nText("ClickGUI is working!");
            nSeparator();
            nText("Press RSHIFT to close");
            int moduleCount = ModuleManager.getInstance().getModules().size();
            nText("Modules: " + moduleCount);
            // Show a list of modules (for demonstration)
            if (nCollapsingHeader("Module List")) {
                for (com.crucifix.client.modules.Module mod : ModuleManager.getInstance().getModules()) {
                    boolean val = mod.isEnabled();
                    if (nCheckbox(mod.getName(), val)) {
                        mod.toggle();
                    }
                }
            }
            nEnd();

            if (renderCount == 1) {
                System.out.println("[ClickGUI] First successful ImGui render!");
            }

        } catch (Throwable t) {
            if (renderCount <= 5) {
                System.out.println("[ClickGUI] Render error: " + t.getMessage());
                t.printStackTrace();
            }
            // Fallback on error
            fallbackActive = true;
            renderFallback();
        }
    }

    // Simple Java-based fallback rendering – just logs to console
    private void renderFallback() {
        // This is a diagnostic method to confirm render() is being called
        // In a real implementation, you would use Minecraft's font renderer here
        // but we can't compile with Minecraft classes in the build path
        if (renderCount <= 10) {
            System.out.println("[FALLBACK] Rendering fallback window (open=" + open + ")");
        }
    }

    public void toggle() {
        open = !open;
        animationProgress = 0f;
        renderCount = 0;
        System.out.println("[ClickGUI] Toggled: " + open);
        // If ImGui is not available, fallback will be used automatically
        if (!imGuiAvailable) {
            fallbackActive = true;
        }
    }

    public boolean isOpen() {
        return open;
    }

    public Theme getCurrentTheme() {
        return theme;
    }

    // Native methods – these are registered in C++
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


package com.crucifix.client.gui;

import com.crucifix.client.Crucifix;
import com.crucifix.client.gui.components.Panel;
import com.crucifix.client.gui.themes.Theme;
import com.crucifix.client.gui.themes.CrucifixDark;
import com.crucifix.client.modules.Category;

import java.util.ArrayList;
import java.util.List;

/**
 * Main ClickGUI interface - renders via ImGui from C++ hook
 */
public class ClickGUI {
    private boolean open;
    private int toggleKey;
    private List<Panel> panels;
    private Theme currentTheme;
    private int mouseX, mouseY;
    private boolean dragging;
    private Panel draggedPanel;
    private int dragOffsetX, dragOffsetY;
    
    public ClickGUI() {
        this.open = false;
        this.toggleKey = 0xA1; // VK_RSHIFT
        this.panels = new ArrayList<>();
        this.currentTheme = new CrucifixDark();
        this.mouseX = 0;
        this.mouseY = 0;
        this.dragging = false;
        
        initializePanels();
    }
    
    private void initializePanels() {
        int x = 10;
        for (Category category : Category.values()) {
            Panel panel = new Panel(category.getDisplayName(), x, 10, 140, 300, category);
            panels.add(panel);
            x += 150;
        }
    }
    
    /**
     * Called from C++ wglSwapBuffers hook to render the GUI
     */
    public void render(float partialTicks) {
        if (!open) return;
        
        // Render all panels
        for (Panel panel : panels) {
            panel.render(partialTicks, mouseX, mouseY);
        }
    }
    
    /**
     * Toggle the ClickGUI visibility
     */
    public void toggle() {
        open = !open;
        System.out.println("[ClickGUI] Toggled: " + (open ? "OPEN" : "CLOSED"));
    }
    
    /**
     * Handle mouse click from C++ input
     */
    public void handleMouseClick(int button, int x, int y) {
        if (!open) return;
        
        this.mouseX = x;
        this.mouseY = y;
        
        if (button == 0) { // Left click
            for (Panel panel : panels) {
                if (panel.isMouseOver(x, y)) {
                    if (panel.isHeaderOver(x, y)) {
                        dragging = true;
                        draggedPanel = panel;
                        dragOffsetX = x - panel.getX();
                        dragOffsetY = y - panel.getY();
                    } else {
                        panel.handleClick(x, y);
                    }
                    break;
                }
            }
        }
    }
    
    /**
     * Handle mouse release from C++ input
     */
    public void handleMouseRelease(int button) {
        if (button == 0) {
            dragging = false;
            draggedPanel = null;
        }
    }
    
    /**
     * Handle mouse move from C++ input
     */
    public void handleMouseMove(int x, int y) {
        this.mouseX = x;
        this.mouseY = y;
        
        if (dragging && draggedPanel != null) {
            draggedPanel.setPosition(x - dragOffsetX, y - dragOffsetY);
        }
    }
    
    public boolean isOpen() {
        return open;
    }
    
    public int getToggleKey() {
        return toggleKey;
    }
    
    public void setToggleKey(int toggleKey) {
        this.toggleKey = toggleKey;
    }
    
    public Theme getCurrentTheme() {
        return currentTheme;
    }
    
    public void setCurrentTheme(Theme theme) {
        this.currentTheme = theme;
    }
    
    public List<Panel> getPanels() {
        return panels;
    }
}


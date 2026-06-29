package com.crucifix.client.gui.components;

import com.crucifix.client.Crucifix;
import com.crucifix.client.gui.animations.SpringAnimation;
import com.crucifix.client.gui.themes.Theme;
import com.crucifix.client.modules.Module;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Toggleable module button with hover effects
 */
public class ModuleButton {
    private final Module module;
    private int x, y;
    private final int width;
    private final int height;
    private SpringAnimation hoverAnimation;
    private SpringAnimation toggleAnimation;
    private boolean hovered;
    
    public ModuleButton(Module module, int x, int y, int width, int height) {
        this.module = module;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.hoverAnimation = new SpringAnimation(0, 0, 150, 0.8f);
        this.toggleAnimation = new SpringAnimation(module.isEnabled() ? 1 : 0, module.isEnabled() ? 1 : 0, 200, 0.7f);
        this.hovered = false;
    }
    
    public void render(float partialTicks, int mouseX, int mouseY, float parentAlpha) {
        Theme theme = Crucifix.getInstance().getClickGUI().getCurrentTheme();
        
        // Update hover animation
        boolean isHovered = isMouseOver(mouseX, mouseY);
        if (isHovered != hovered) {
            hovered = isHovered;
            hoverAnimation.setTarget(hovered ? 1 : 0);
        }
        
        // Update toggle animation
        toggleAnimation.setTarget(module.isEnabled() ? 1 : 0);
        
        float hoverProgress = hoverAnimation.getPosition();
        float toggleProgress = toggleAnimation.getPosition();
        
        Graphics2D g2d = (Graphics2D) new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB).getGraphics();
        
        // Interpolate background color based on enabled state and hover
        Color bgColor;
        if (module.isEnabled()) {
            bgColor = theme.getEnabledModule();
        } else {
            bgColor = theme.getDisabledModule();
        }
        
        // Apply hover effect
        if (hoverProgress > 0) {
            int r = (int) (bgColor.getRed() + (theme.getHover().getRed() - bgColor.getRed()) * hoverProgress);
            int g = (int) (bgColor.getGreen() + (theme.getHover().getGreen() - bgColor.getGreen()) * hoverProgress);
            int b = (int) (bgColor.getBlue() + (theme.getHover().getBlue() - bgColor.getBlue()) * hoverProgress);
            int a = (int) (bgColor.getAlpha() + (theme.getHover().getAlpha() - bgColor.getAlpha()) * hoverProgress);
            bgColor = new Color(r, g, b, a);
        }
        
        g2d.setColor(bgColor);
        g2d.fillRect(0, 0, width, height);
        
        // Draw enabled indicator
        if (toggleProgress > 0) {
            int indicatorWidth = (int) (3 * toggleProgress);
            g2d.setColor(theme.getAccent());
            g2d.fillRect(0, 0, indicatorWidth, height);
        }
        
        // Draw module name
        g2d.setColor(theme.getText());
        g2d.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        g2d.drawString(module.getName(), 8, 17);
        
        // Draw keybind
        String keybindText = getKeybindName(module.getKeyBind());
        g2d.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        FontMetrics fm = g2d.getFontMetrics();
        int keybindWidth = fm.stringWidth(keybindText);
        g2d.drawString(keybindText, width - keybindWidth - 5, 17);
        
        g2d.dispose();
    }
    
    public void handleClick(int mouseX, int mouseY) {
        module.toggle();
    }
    
    public boolean isMouseOver(int mouseX, int mouseY) {
        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
    }
    
    public void setY(int y) {
        this.y = y;
    }
    
    public int getX() {
        return x;
    }
    
    public int getY() {
        return y;
    }
    
    public int getWidth() {
        return width;
    }
    
    public int getHeight() {
        return height;
    }
    
    public Module getModule() {
        return module;
    }
    
    private String getKeybindName(int keyCode) {
        if (keyCode == 0) return "None";
        if (keyCode >= 1 && keyCode <= 26) return Character.toString((char) (keyCode + 64));
        if (keyCode >= 65 && keyCode <= 90) return Character.toString((char) keyCode);
        
        switch (keyCode) {
            case 184: return "RSHIFT";
            case 19: return "PAUSE";
            case 20: return "CAPS";
            case 157: return "CTRL";
            case 18: return "ALT";
            case 16: return "SHIFT";
            case 10: return "ENTER";
            case 8: return "BACK";
            case 9: return "TAB";
            case 27: return "ESC";
            case 32: return "SPACE";
            case 33: return "PGUP";
            case 34: return "PGDN";
            case 35: return "END";
            case 36: return "HOME";
            case 37: return "LEFT";
            case 38: return "UP";
            case 39: return "RIGHT";
            case 40: return "DOWN";
            case 45: return "INS";
            case 46: return "DEL";
            case 112: return "F1";
            case 113: return "F2";
            case 114: return "F3";
            case 115: return "F4";
            case 116: return "F5";
            case 117: return "F6";
            case 118: return "F7";
            case 119: return "F8";
            case 120: return "F9";
            case 121: return "F10";
            case 122: return "F11";
            case 123: return "F12";
            default: return "KEY_" + keyCode;
        }
    }
}


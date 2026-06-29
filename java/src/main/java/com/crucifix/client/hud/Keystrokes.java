package com.crucifix.client.hud;

import com.crucifix.client.Crucifix;
import com.crucifix.client.gui.themes.Theme;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Keystrokes HUD component
 */
public class Keystrokes extends HUDComponent {
    
    private boolean wPressed, aPressed, sPressed, dPressed;
    private boolean lmbPressed, rmbPressed;
    
    public Keystrokes() {
        super(20, 400);
    }
    
    @Override
    public void update() {
        // Update key states (would hook into Minecraft input)
    }
    
    @Override
    public void render(float partialTicks) {
        Theme theme = Crucifix.getInstance().getClickGUI().getCurrentTheme();
        
        Graphics2D g2d = (Graphics2D) new BufferedImage(100, 100, BufferedImage.TYPE_INT_ARGB).getGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        g2d.setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        // Draw WASD keys
        drawKey(g2d, "W", x + 30, y, wPressed, theme);
        drawKey(g2d, "A", x, y + 25, aPressed, theme);
        drawKey(g2d, "S", x + 30, y + 25, sPressed, theme);
        drawKey(g2d, "D", x + 60, y + 25, dPressed, theme);
        
        // Draw mouse buttons
        drawKey(g2d, "L", x, y + 55, lmbPressed, theme);
        drawKey(g2d, "R", x + 60, y + 55, rmbPressed, theme);
        
        g2d.dispose();
    }
    
    private void drawKey(Graphics2D g2d, String text, int x, int y, boolean pressed, Theme theme) {
        int keySize = 25;
        
        // Draw background
        if (pressed) {
            g2d.setColor(theme.getAccent());
        } else {
            g2d.setColor(new Color(30, 30, 35, 200));
        }
        g2d.fillRoundRect(x, y, keySize, keySize, 4, 4);
        
        // Draw border
        g2d.setColor(theme.getBorder());
        g2d.drawRoundRect(x, y, keySize, keySize, 4, 4);
        
        // Draw text
        g2d.setColor(theme.getText());
        FontMetrics fm = g2d.getFontMetrics();
        int textWidth = fm.stringWidth(text);
        g2d.drawString(text, x + (keySize - textWidth) / 2, y + 17);
    }
    
    @Override
    public int getWidth() {
        return 90;
    }
    
    @Override
    public int getHeight() {
        return 85;
    }
}


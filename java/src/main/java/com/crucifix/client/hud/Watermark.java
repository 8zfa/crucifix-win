package com.crucifix.client.hud;

import com.crucifix.client.Crucifix;
import com.crucifix.client.gui.themes.Theme;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Watermark HUD component
 */
public class Watermark extends HUDComponent {
    
    public Watermark() {
        super(10, 10);
    }
    
    @Override
    public void update() {
        // Update logic if needed
    }
    
    @Override
    public void render(float partialTicks) {
        Theme theme = Crucifix.getInstance().getClickGUI().getCurrentTheme();
        
        Graphics2D g2d = (Graphics2D) new BufferedImage(200, 30, BufferedImage.TYPE_INT_ARGB).getGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Draw gradient text
        String text = Crucifix.NAME;
        g2d.setFont(new Font("Segoe UI", Font.BOLD, 16));
        FontMetrics fm = g2d.getFontMetrics();
        int textWidth = fm.stringWidth(text);
        
        // Create gradient
        GradientPaint gradient = new GradientPaint(x, y, theme.getAccent(), x + textWidth, y, new Color(180, 100, 100));
        g2d.setPaint(gradient);
        g2d.drawString(text, x, y + 20);
        
        g2d.dispose();
    }
    
    @Override
    public int getWidth() {
        return 120;
    }
    
    @Override
    public int getHeight() {
        return 25;
    }
}


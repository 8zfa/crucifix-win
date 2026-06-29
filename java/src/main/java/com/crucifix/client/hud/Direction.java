package com.crucifix.client.hud;

import com.crucifix.client.Crucifix;
import com.crucifix.client.gui.themes.Theme;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Direction HUD component
 */
public class Direction extends HUDComponent {
    
    public Direction(int x, int y) {
        super(x, y);
    }
    
    @Override
    public void update() {
        // Update direction
    }
    
    @Override
    public void render(float partialTicks) {
        Theme theme = Crucifix.getInstance().getClickGUI().getCurrentTheme();
        
        Graphics2D g2d = (Graphics2D) new BufferedImage(80, 50, BufferedImage.TYPE_INT_ARGB).getGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        g2d.setFont(new Font("Segoe UI", Font.BOLD, 12));
        
        // Draw compass background
        g2d.setColor(new Color(30, 30, 35, 200));
        g2d.fillOval(x, y, 40, 40);
        
        // Draw border
        g2d.setColor(theme.getBorder());
        g2d.drawOval(x, y, 40, 40);
        
        // Draw direction indicator (placeholder - would use actual player rotation)
        g2d.setColor(theme.getAccent());
        int centerX = x + 20;
        int centerY = y + 20;
        int indicatorX = centerX + 15;
        int indicatorY = centerY;
        g2d.drawLine(centerX, centerY, indicatorX, indicatorY);
        g2d.fillOval(indicatorX - 3, indicatorY - 3, 6, 6);
        
        // Draw N/S/E/W labels
        g2d.setColor(theme.getText());
        g2d.drawString("N", centerX - 4, y + 5);
        g2d.drawString("S", centerX - 4, y + 38);
        g2d.drawString("E", x + 35, centerY + 4);
        g2d.drawString("W", x + 5, centerY + 4);
        
        // Draw degree
        String degree = "0°";
        FontMetrics fm = g2d.getFontMetrics();
        int degreeWidth = fm.stringWidth(degree);
        g2d.drawString(degree, centerX - degreeWidth / 2, y + 55);
        
        g2d.dispose();
    }
    
    @Override
    public int getWidth() {
        return 80;
    }
    
    @Override
    public int getHeight() {
        return 60;
    }
}


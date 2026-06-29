package com.crucifix.client.hud;

import com.crucifix.client.Crucifix;
import com.crucifix.client.gui.themes.Theme;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * PotionStatus HUD component
 */
public class PotionStatus extends HUDComponent {
    
    public PotionStatus(int x, int y) {
        super(x, y);
    }
    
    @Override
    public void update() {
        // Update potion status
    }
    
    @Override
    public void render(float partialTicks) {
        Theme theme = Crucifix.getInstance().getClickGUI().getCurrentTheme();
        
        Graphics2D g2d = (Graphics2D) new BufferedImage(150, 100, BufferedImage.TYPE_INT_ARGB).getGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        g2d.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        
        // Draw potion effects (placeholder)
        String[] potionNames = {"Speed II", "Strength I", "Regeneration"};
        int[] potionDurations = {45, 120, 30}; // in seconds
        
        int yOffset = 0;
        for (int i = 0; i < potionNames.length; i++) {
            // Draw potion icon background
            g2d.setColor(new Color(30, 30, 35, 200));
            g2d.fillRect(x, y + yOffset, 15, 15);
            
            // Draw border
            g2d.setColor(theme.getBorder());
            g2d.drawRect(x, y + yOffset, 15, 15);
            
            // Draw potion name
            g2d.setColor(theme.getText());
            g2d.drawString(potionNames[i], x + 20, y + yOffset + 12);
            
            // Draw duration
            String durationText = potionDurations[i] + "s";
            FontMetrics fm = g2d.getFontMetrics();
            int durationWidth = fm.stringWidth(durationText);
            g2d.drawString(durationText, x + 130 - durationWidth, y + yOffset + 12);
            
            yOffset += 20;
        }
        
        g2d.dispose();
    }
    
    @Override
    public int getWidth() {
        return 130;
    }
    
    @Override
    public int getHeight() {
        return 60;
    }
}


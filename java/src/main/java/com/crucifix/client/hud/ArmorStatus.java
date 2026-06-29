package com.crucifix.client.hud;

import com.crucifix.client.Crucifix;
import com.crucifix.client.gui.themes.Theme;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * ArmorStatus HUD component
 */
public class ArmorStatus extends HUDComponent {
    
    public ArmorStatus() {
        super(10, 50);
    }
    
    @Override
    public void update() {
        // Update armor status
    }
    
    @Override
    public void render(float partialTicks) {
        Theme theme = Crucifix.getInstance().getClickGUI().getCurrentTheme();
        
        Graphics2D g2d = (Graphics2D) new BufferedImage(100, 50, BufferedImage.TYPE_INT_ARGB).getGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        g2d.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        
        // Draw armor slots (helmet, chestplate, leggings, boots)
        int[] armorDurability = {100, 85, 70, 55}; // Placeholder values
        
        for (int i = 0; i < 4; i++) {
            int slotX = x + i * 25;
            int slotY = y;
            
            // Draw armor slot background
            g2d.setColor(new Color(30, 30, 35, 200));
            g2d.fillRect(slotX, slotY, 20, 20);
            
            // Draw border
            g2d.setColor(theme.getBorder());
            g2d.drawRect(slotX, slotY, 20, 20);
            
            // Draw durability percentage
            g2d.setColor(theme.getText());
            g2d.drawString(armorDurability[i] + "%", slotX + 2, slotY + 12);
            
            // Draw durability bar
            int barWidth = (int) (20 * (armorDurability[i] / 100.0));
            Color barColor = armorDurability[i] > 50 ? Color.GREEN : (armorDurability[i] > 25 ? Color.YELLOW : Color.RED);
            g2d.setColor(barColor);
            g2d.fillRect(slotX, slotY + 18, barWidth, 2);
        }
        
        g2d.dispose();
    }
    
    @Override
    public int getWidth() {
        return 100;
    }
    
    @Override
    public int getHeight() {
        return 25;
    }
}


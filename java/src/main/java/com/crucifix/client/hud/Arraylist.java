package com.crucifix.client.hud;

import com.crucifix.client.Crucifix;
import com.crucifix.client.gui.themes.Theme;
import com.crucifix.client.modules.Module;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;

/**
 * Arraylist HUD component
 */
public class Arraylist extends HUDComponent {
    
    public Arraylist() {
        super(800, 100);
    }
    
    @Override
    public void update() {
        // Update logic if needed
    }
    
    @Override
    public void render(float partialTicks) {
        Theme theme = Crucifix.getInstance().getClickGUI().getCurrentTheme();
        List<Module> enabledModules = Crucifix.getInstance().getModuleManager().getEnabledModules();
        
        if (enabledModules.isEmpty()) return;
        
        Graphics2D g2d = (Graphics2D) new BufferedImage(200, 500, BufferedImage.TYPE_INT_ARGB).getGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        g2d.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        FontMetrics fm = g2d.getFontMetrics();
        
        int yOffset = 0;
        for (Module module : enabledModules) {
            String text = module.getName();
            int textWidth = fm.stringWidth(text);
            
            // Draw module name
            g2d.setColor(theme.getText());
            g2d.drawString(text, x - textWidth - 5, y + yOffset + 12);
            
            // Draw accent line
            g2d.setColor(theme.getAccent());
            g2d.fillRect(x - textWidth - 8, y + yOffset + 4, 2, 10);
            
            yOffset += 15;
        }
        
        g2d.dispose();
    }
    
    @Override
    public int getWidth() {
        return 100;
    }
    
    @Override
    public int getHeight() {
        int enabledCount = Crucifix.getInstance().getModuleManager().getEnabledModules().size();
        return enabledCount * 15;
    }
}


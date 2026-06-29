package com.crucifix.client.gui.components;

import com.crucifix.client.Crucifix;
import com.crucifix.client.gui.ClickGUI;
import com.crucifix.client.gui.animations.Easing;
import com.crucifix.client.gui.animations.SpringAnimation;
import com.crucifix.client.gui.themes.Theme;
import com.crucifix.client.modules.Category;
import com.crucifix.client.modules.Module;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

/**
 * Category panel container with drag support
 */
public class Panel {
    private final String title;
    private int x, y;
    private final int width;
    private int height;
    private final Category category;
    private List<ModuleButton> moduleButtons;
    private boolean expanded;
    private SpringAnimation expandAnimation;
    
    public Panel(String title, int x, int y, int width, int height, Category category) {
        this.title = title;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.category = category;
        this.moduleButtons = new ArrayList<>();
        this.expanded = true;
        this.expandAnimation = new SpringAnimation(1, 1, 200, 0.7f);
        
        initializeModuleButtons();
    }
    
    private void initializeModuleButtons() {
        int yOffset = 30;
        for (Module module : Crucifix.getInstance().getModuleManager().getModulesByCategory(category)) {
            ModuleButton button = new ModuleButton(module, 0, yOffset, width, 25);
            moduleButtons.add(button);
            yOffset += 25;
        }
    }
    
    public void render(float partialTicks, int mouseX, int mouseY) {
        Theme theme = Crucifix.getInstance().getClickGUI().getCurrentTheme();
        
        // Update expand animation with smooth spring physics
        expandAnimation.update(0.016f); // 60 FPS delta time
        float expandProgress = expandAnimation.getPosition();
        
        // Calculate current height based on expansion with smooth interpolation
        int targetHeight = 30 + (moduleButtons.size() * 25);
        int currentHeight = 30 + (int)((targetHeight - 30) * expandProgress);
        this.height = currentHeight;
        
        Graphics2D g2d = (Graphics2D) new BufferedImage(width, currentHeight, BufferedImage.TYPE_INT_ARGB).getGraphics();
        
        // Enable anti-aliasing for smooth rendering
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        
        // Draw panel background with gradient
        Color bgColor = theme.getBackgroundAlpha();
        GradientPaint bgGradient = new GradientPaint(0, 0, bgColor, 0, currentHeight, 
            new Color(bgColor.getRed(), bgColor.getGreen(), bgColor.getBlue(), 
                Math.max(0, bgColor.getAlpha() - 30)));
        g2d.setPaint(bgGradient);
        g2d.fillRoundRect(0, 0, width, currentHeight, 8, 8);
        
        // Draw border with smooth stroke
        g2d.setColor(theme.getBorder());
        g2d.setStroke(new BasicStroke(1.5f));
        g2d.drawRoundRect(0, 0, width, currentHeight, 8, 8);
        
        // Draw header background with gradient
        Color accentColor = theme.getAccent();
        GradientPaint headerGradient = new GradientPaint(0, 0, 
            new Color(accentColor.getRed(), accentColor.getGreen(), accentColor.getBlue(), 120),
            0, 30, 
            new Color(accentColor.getRed(), accentColor.getGreen(), accentColor.getBlue(), 80));
        g2d.setPaint(headerGradient);
        g2d.fillRoundRect(0, 0, width, 30, 8, 8);
        
        // Draw header border
        g2d.setColor(theme.getAccent());
        g2d.setStroke(new BasicStroke(1f));
        g2d.drawRoundRect(0, 0, width, 30, 8, 8);
        
        // Draw title with shadow for better visibility
        g2d.setColor(new Color(0, 0, 0, 100));
        g2d.setFont(new Font("Segoe UI", Font.BOLD, 12));
        FontMetrics fm = g2d.getFontMetrics();
        int titleWidth = fm.stringWidth(title);
        g2d.drawString(title, (width - titleWidth) / 2 + 1, 21);
        
        g2d.setColor(theme.getText());
        g2d.drawString(title, (width - titleWidth) / 2, 20);
        
        // Draw animated expand/collapse indicator
        String indicator = expanded ? "−" : "+";
        g2d.setFont(new Font("Segoe UI", Font.BOLD, 14));
        g2d.drawString(indicator, width - 18, 21);
        
        // Draw module buttons if expanded with fade-in effect
        if (expandProgress > 0.01f) {
            int yOffset = 30;
            int visibleButtons = (int)(moduleButtons.size() * expandProgress);
            
            for (int i = 0; i < visibleButtons; i++) {
                ModuleButton button = moduleButtons.get(i);
                button.setY(yOffset);
                
                // Calculate individual button fade-in
                float buttonProgress = Math.min(1.0f, expandProgress * (moduleButtons.size() - i) / 2.0f);
                button.render(partialTicks, mouseX, mouseY, buttonProgress);
                yOffset += 25;
            }
        }
        
        g2d.dispose();
    }
    
    public void handleClick(int mouseX, int mouseY) {
        // Check if header was clicked (expand/collapse)
        if (isHeaderOver(mouseX, mouseY)) {
            expanded = !expanded;
            expandAnimation.setTarget(expanded ? 1 : 0);
            return;
        }
        
        // Check module buttons
        for (ModuleButton button : moduleButtons) {
            if (button.isMouseOver(mouseX, mouseY) && expanded) {
                button.handleClick(mouseX, mouseY);
                break;
            }
        }
    }
    
    public boolean isMouseOver(int mouseX, int mouseY) {
        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
    }
    
    public boolean isHeaderOver(int mouseX, int mouseY) {
        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + 30;
    }
    
    public void setPosition(int x, int y) {
        this.x = x;
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
    
    public String getTitle() {
        return title;
    }
    
    public Category getCategory() {
        return category;
    }
    
    public List<ModuleButton> getModuleButtons() {
        return moduleButtons;
    }
}


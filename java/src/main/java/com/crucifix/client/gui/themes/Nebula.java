package com.crucifix.client.gui.themes;

import java.awt.Color;

/**
 * Nebula theme - purple/blue gradient
 */
public class Nebula implements Theme {
    
    @Override
    public String getName() {
        return "Nebula";
    }
    
    @Override
    public Color getBackground() {
        return new Color(18, 12, 30);
    }
    
    @Override
    public Color getBackgroundAlpha() {
        return new Color(18, 12, 30, 215);
    }
    
    @Override
    public Color getAccent() {
        return new Color(150, 100, 220);
    }
    
    @Override
    public Color getText() {
        return new Color(230, 220, 255);
    }
    
    @Override
    public Color getEnabledModule() {
        return new Color(35, 25, 55, 180);
    }
    
    @Override
    public Color getDisabledModule() {
        return new Color(20, 15, 35, 100);
    }
    
    @Override
    public Color getTrack() {
        return new Color(50, 35, 80, 150);
    }
    
    @Override
    public Color getKnob() {
        return new Color(150, 100, 220);
    }
    
    @Override
    public Color getHover() {
        return new Color(150, 100, 220, 60);
    }
    
    @Override
    public Color getBorder() {
        return new Color(150, 100, 220, 100);
    }
}


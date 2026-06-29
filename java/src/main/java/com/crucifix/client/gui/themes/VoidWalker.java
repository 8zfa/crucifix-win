package com.crucifix.client.gui.themes;

import java.awt.Color;

/**
 * Void Walker theme - black with cyan accent
 */
public class VoidWalker implements Theme {
    
    @Override
    public String getName() {
        return "VoidWalker";
    }
    
    @Override
    public Color getBackground() {
        return new Color(5, 5, 8);
    }
    
    @Override
    public Color getBackgroundAlpha() {
        return new Color(5, 5, 8, 230);
    }
    
    @Override
    public Color getAccent() {
        return new Color(0, 200, 220);
    }
    
    @Override
    public Color getText() {
        return new Color(200, 220, 240);
    }
    
    @Override
    public Color getEnabledModule() {
        return new Color(10, 20, 30, 180);
    }
    
    @Override
    public Color getDisabledModule() {
        return new Color(8, 8, 12, 100);
    }
    
    @Override
    public Color getTrack() {
        return new Color(20, 40, 50, 150);
    }
    
    @Override
    public Color getKnob() {
        return new Color(0, 200, 220);
    }
    
    @Override
    public Color getHover() {
        return new Color(0, 200, 220, 60);
    }
    
    @Override
    public Color getBorder() {
        return new Color(0, 200, 220, 100);
    }
}


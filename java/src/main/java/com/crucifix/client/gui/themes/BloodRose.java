package com.crucifix.client.gui.themes;

import java.awt.Color;

/**
 * Blood Rose theme - dark red accent
 */
public class BloodRose implements Theme {
    
    @Override
    public String getName() {
        return "BloodRose";
    }
    
    @Override
    public Color getBackground() {
        return new Color(15, 10, 10);
    }
    
    @Override
    public Color getBackgroundAlpha() {
        return new Color(15, 10, 10, 220);
    }
    
    @Override
    public Color getAccent() {
        return new Color(180, 60, 60);
    }
    
    @Override
    public Color getText() {
        return new Color(255, 220, 220);
    }
    
    @Override
    public Color getEnabledModule() {
        return new Color(40, 20, 20, 180);
    }
    
    @Override
    public Color getDisabledModule() {
        return new Color(25, 15, 15, 100);
    }
    
    @Override
    public Color getTrack() {
        return new Color(60, 30, 30, 150);
    }
    
    @Override
    public Color getKnob() {
        return new Color(180, 60, 60);
    }
    
    @Override
    public Color getHover() {
        return new Color(180, 60, 60, 60);
    }
    
    @Override
    public Color getBorder() {
        return new Color(180, 60, 60, 100);
    }
}


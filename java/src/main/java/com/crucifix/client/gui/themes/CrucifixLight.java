package com.crucifix.client.gui.themes;

import java.awt.Color;

/**
 * Light theme for Crucifix
 */
public class CrucifixLight implements Theme {
    
    @Override
    public String getName() {
        return "CrucifixLight";
    }
    
    @Override
    public Color getBackground() {
        return new Color(240, 240, 245);
    }
    
    @Override
    public Color getBackgroundAlpha() {
        return new Color(240, 240, 245, 230);
    }
    
    @Override
    public Color getAccent() {
        return new Color(212, 160, 160);
    }
    
    @Override
    public Color getText() {
        return new Color(30, 30, 35);
    }
    
    @Override
    public Color getEnabledModule() {
        return new Color(220, 220, 225, 200);
    }
    
    @Override
    public Color getDisabledModule() {
        return new Color(200, 200, 205, 150);
    }
    
    @Override
    public Color getTrack() {
        return new Color(180, 180, 185);
    }
    
    @Override
    public Color getKnob() {
        return new Color(212, 160, 160);
    }
    
    @Override
    public Color getHover() {
        return new Color(212, 160, 160, 80);
    }
    
    @Override
    public Color getBorder() {
        return new Color(180, 180, 185);
    }
}


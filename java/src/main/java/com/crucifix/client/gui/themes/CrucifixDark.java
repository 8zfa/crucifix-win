package com.crucifix.client.gui.themes;

import java.awt.Color;

/**
 * Default dark theme for Crucifix
 */
public class CrucifixDark implements Theme {
    
    @Override
    public String getName() {
        return "CrucifixDark";
    }
    
    @Override
    public Color getBackground() {
        return new Color(21, 21, 25);
    }
    
    @Override
    public Color getBackgroundAlpha() {
        return new Color(21, 21, 25, 204);
    }
    
    @Override
    public Color getAccent() {
        return new Color(212, 160, 160);
    }
    
    @Override
    public Color getText() {
        return Color.WHITE;
    }
    
    @Override
    public Color getEnabledModule() {
        return new Color(42, 42, 48, 170);
    }
    
    @Override
    public Color getDisabledModule() {
        return new Color(21, 21, 25, 85);
    }
    
    @Override
    public Color getTrack() {
        return new Color(51, 51, 51, 136);
    }
    
    @Override
    public Color getKnob() {
        return new Color(212, 160, 160);
    }
    
    @Override
    public Color getHover() {
        return new Color(212, 160, 160, 51);
    }
    
    @Override
    public Color getBorder() {
        return new Color(212, 160, 160, 68);
    }
}


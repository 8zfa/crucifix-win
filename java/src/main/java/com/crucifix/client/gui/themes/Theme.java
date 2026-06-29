package com.crucifix.client.gui.themes;

import java.awt.Color;

/**
 * Theme interface for color palettes
 */
public interface Theme {
    String getName();
    
    Color getBackground();
    Color getBackgroundAlpha();
    Color getAccent();
    Color getText();
    Color getEnabledModule();
    Color getDisabledModule();
    Color getTrack();
    Color getKnob();
    Color getHover();
    Color getBorder();
}


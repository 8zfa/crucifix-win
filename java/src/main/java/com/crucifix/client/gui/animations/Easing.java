package com.crucifix.client.gui.animations;

/**
 * Easing functions for smooth animations
 */
public class Easing {
    
    public enum Type {
        LINEAR, QUAD, CUBIC, QUART, QUINT, SINE, EXPO, CIRC, BACK, ELASTIC, BOUNCE
    }
    
    public enum Mode {
        IN, OUT, IN_OUT
    }
    
    public static float apply(float progress, Type type, Mode mode) {
        return com.crucifix.client.core.AnimationEngine.applyEasing(progress, type, mode);
    }
}


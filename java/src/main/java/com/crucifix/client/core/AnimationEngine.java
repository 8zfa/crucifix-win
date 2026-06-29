package com.crucifix.client.core;

import com.crucifix.client.gui.animations.Easing;
import com.crucifix.client.gui.animations.SpringAnimation;

import java.util.ArrayList;
import java.util.List;

/**
 * Animation engine with spring physics and easing functions
 */
public class AnimationEngine {
    private final List<SpringAnimation> animations;
    private long lastUpdateTime;
    
    public AnimationEngine() {
        this.animations = new ArrayList<>();
        this.lastUpdateTime = System.currentTimeMillis();
    }
    
    /**
     * Update all animations
     */
    public void update() {
        long currentTime = System.currentTimeMillis();
        float deltaTime = (currentTime - lastUpdateTime) / 1000f;
        lastUpdateTime = currentTime;
        
        // Update all spring animations
        animations.removeIf(animation -> {
            animation.update(deltaTime);
            return animation.isComplete();
        });
    }
    
    /**
     * Create a new spring animation
     */
    public SpringAnimation createSpringAnimation(float target, float stiffness, float damping) {
        SpringAnimation animation = new SpringAnimation(0, target, stiffness, damping);
        animations.add(animation);
        return animation;
    }
    
    /**
     * Apply easing function
     */
    public static float applyEasing(float progress, Easing.Type easingType, Easing.Mode easingMode) {
        float p = progress;
        
        switch (easingType) {
            case LINEAR:
                return p;
            case QUAD:
                return applyQuad(p, easingMode);
            case CUBIC:
                return applyCubic(p, easingMode);
            case QUART:
                return applyQuart(p, easingMode);
            case QUINT:
                return applyQuint(p, easingMode);
            case SINE:
                return applySine(p, easingMode);
            case EXPO:
                return applyExpo(p, easingMode);
            case CIRC:
                return applyCirc(p, easingMode);
            case BACK:
                return applyBack(p, easingMode);
            case ELASTIC:
                return applyElastic(p, easingMode);
            case BOUNCE:
                return applyBounce(p, easingMode);
            default:
                return p;
        }
    }
    
    private static float applyQuad(float p, Easing.Mode mode) {
        switch (mode) {
            case IN: return p * p;
            case OUT: return p * (2 - p);
            case IN_OUT: return p < 0.5f ? 2 * p * p : -1 + (4 - 2 * p) * p;
            default: return p;
        }
    }
    
    private static float applyCubic(float p, Easing.Mode mode) {
        switch (mode) {
            case IN: return p * p * p;
            case OUT: return (--p) * p * p + 1;
            case IN_OUT: return p < 0.5f ? 4 * p * p * p : (p - 1) * (2 * p - 2) * (2 * p - 2) + 1;
            default: return p;
        }
    }
    
    private static float applyQuart(float p, Easing.Mode mode) {
        switch (mode) {
            case IN: return p * p * p * p;
            case OUT: return 1 - (--p) * p * p * p;
            case IN_OUT: return p < 0.5f ? 8 * p * p * p * p : 1 - 8 * (--p) * p * p * p;
            default: return p;
        }
    }
    
    private static float applyQuint(float p, Easing.Mode mode) {
        switch (mode) {
            case IN: return p * p * p * p * p;
            case OUT: return 1 + (--p) * p * p * p * p;
            case IN_OUT: return p < 0.5f ? 16 * p * p * p * p * p : 1 + 16 * (--p) * p * p * p * p;
            default: return p;
        }
    }
    
    private static float applySine(float p, Easing.Mode mode) {
        switch (mode) {
            case IN: return (float) -Math.cos(p * (Math.PI / 2)) + 1;
            case OUT: return (float) Math.sin(p * (Math.PI / 2));
            case IN_OUT: return (float) (-Math.cos(Math.PI * p) / 2) + 0.5f;
            default: return p;
        }
    }
    
    private static float applyExpo(float p, Easing.Mode mode) {
        switch (mode) {
            case IN: return p == 0 ? 0 : (float) Math.pow(2, 10 * (p - 1));
            case OUT: return p == 1 ? 1 : (float) (-Math.pow(2, -10 * p) + 1);
            case IN_OUT: 
                if (p == 0) return 0;
                if (p == 1) return 1;
                if (p < 0.5f) return (float) (Math.pow(2, 20 * p - 10) / 2);
                return (float) ((-Math.pow(2, -20 * p + 10) + 2) / 2);
            default: return p;
        }
    }
    
    private static float applyCirc(float p, Easing.Mode mode) {
        switch (mode) {
            case IN: return (float) (1 - Math.sqrt(1 - p * p));
            case OUT: return (float) Math.sqrt(1 - (--p) * p);
            case IN_OUT: 
                if (p < 0.5f) return (float) ((1 - Math.sqrt(1 - 2 * p * p)) / 2);
                return (float) ((Math.sqrt(1 - 2 * (--p) * p) + 1) / 2);
            default: return p;
        }
    }
    
    private static float applyBack(float p, Easing.Mode mode) {
        float s = 1.70158f;
        switch (mode) {
            case IN: return p * p * ((s + 1) * p - s);
            case OUT: 
                p -= 1;
                return p * p * ((s + 1) * p + s) + 1;
            case IN_OUT: 
                s *= 1.525f;
                if (p < 0.5f) return (float) (0.5 * p * p * ((s + 1) * 2 * p - s));
                p -= 1;
                return (float) (0.5 * (p * p * ((s + 1) * p + s) + 1));
            default: return p;
        }
    }
    
    private static float applyElastic(float p, Easing.Mode mode) {
        if (p == 0 || p == 1) return p;
        
        switch (mode) {
            case IN:
                return (float) (-Math.pow(2, 10 * p - 10) * Math.sin((p * 10 - 10.75) * ((2 * Math.PI) / 3)));
            case OUT:
                return (float) (Math.pow(2, -10 * p) * Math.sin((p * 10 - 0.75) * ((2 * Math.PI) / 3)) + 1);
            case IN_OUT:
                if (p < 0.5f) {
                    return (float) (-0.5 * Math.pow(2, 20 * p - 10) * Math.sin((20 * p - 11.125) * ((2 * Math.PI) / 4.5)));
                }
                return (float) (0.5 * Math.pow(2, -20 * p + 10) * Math.sin((20 * p - 11.125) * ((2 * Math.PI) / 4.5)) + 1);
            default: return p;
        }
    }
    
    private static float applyBounce(float p, Easing.Mode mode) {
        if (mode == Easing.Mode.IN) {
            return 1 - applyBounceOut(1 - p);
        } else if (mode == Easing.Mode.IN_OUT) {
            if (p < 0.5f) return (1 - applyBounceOut(1 - 2 * p)) / 2;
            return (1 + applyBounceOut(2 * p - 1)) / 2;
        }
        return applyBounceOut(p);
    }
    
    private static float applyBounceOut(float p) {
        if (p < 1 / 2.75f) {
            return 7.5625f * p * p;
        } else if (p < 2 / 2.75f) {
            p -= 1.5f / 2.75f;
            return 7.5625f * p * p + 0.75f;
        } else if (p < 2.5 / 2.75f) {
            p -= 2.25f / 2.75f;
            return 7.5625f * p * p + 0.9375f;
        } else {
            p -= 2.625f / 2.75f;
            return 7.5625f * p * p + 0.984375f;
        }
    }
}


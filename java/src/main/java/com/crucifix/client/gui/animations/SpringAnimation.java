package com.crucifix.client.gui.animations;

/**
 * Physics-based spring animation
 */
public class SpringAnimation {
    private float position;
    private float target;
    private float velocity;
    private final float stiffness;
    private final float damping;
    private final float mass;
    private boolean complete;
    
    public SpringAnimation(float initial, float target, float stiffness, float damping) {
        this.position = initial;
        this.target = target;
        this.stiffness = stiffness;
        this.damping = damping;
        this.mass = 1.0f;
        this.velocity = 0;
        this.complete = false;
    }
    
    public SpringAnimation(float initial, float target, float stiffness, float damping, float mass) {
        this.position = initial;
        this.target = target;
        this.stiffness = stiffness;
        this.damping = damping;
        this.mass = mass;
        this.velocity = 0;
        this.complete = false;
    }
    
    /**
     * Update the spring physics
     */
    public void update(float deltaTime) {
        if (complete) return;
        
        float force = (target - position) * stiffness;
        float acceleration = force / mass;
        velocity += acceleration * deltaTime;
        velocity *= (1 - damping * deltaTime);
        position += velocity * deltaTime;
        
        // Check if animation is complete
        if (Math.abs(target - position) < 0.001f && Math.abs(velocity) < 0.001f) {
            position = target;
            velocity = 0;
            complete = true;
        }
    }
    
    /**
     * Set a new target
     */
    public void setTarget(float target) {
        this.target = target;
        this.complete = false;
    }
    
    public float getPosition() {
        return position;
    }
    
    public float getTarget() {
        return target;
    }
    
    public float getVelocity() {
        return velocity;
    }
    
    public boolean isComplete() {
        return complete;
    }
}


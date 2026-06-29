package com.crucifix.client.core;

import com.crucifix.client.events.Event;
import com.crucifix.client.events.SubscribeEvent;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Annotation-driven event bus with priority support
 */
public class EventBus {
    private final Map<Class<?>, List<EventListener>> listeners;
    private final Map<Object, List<Method>> registeredMethods;
    
    public EventBus() {
        this.listeners = new ConcurrentHashMap<>();
        this.registeredMethods = new ConcurrentHashMap<>();
    }
    
    /**
     * Register an object's event handler methods
     */
    public void register(Object object) {
        for (Method method : object.getClass().getDeclaredMethods()) {
            if (method.isAnnotationPresent(SubscribeEvent.class)) {
                SubscribeEvent annotation = method.getAnnotation(SubscribeEvent.class);
                Class<?>[] parameterTypes = method.getParameterTypes();
                
                if (parameterTypes.length != 1 || !Event.class.isAssignableFrom(parameterTypes[0])) {
                    System.err.println("[EventBus] Invalid event handler method: " + method.getName());
                    continue;
                }
                
                Class<?> eventType = parameterTypes[0];
                byte priority = annotation.priority();
                
                method.setAccessible(true);
                
                List<EventListener> eventListeners = listeners.computeIfAbsent(eventType, k -> new ArrayList<>());
                eventListeners.add(new EventListener(object, method, priority));
                
                // Sort by priority (higher priority = executed first)
                eventListeners.sort((a, b) -> Byte.compare(b.priority, a.priority));
                
                // Track registered methods for unregistering
                registeredMethods.computeIfAbsent(object, k -> new ArrayList<>()).add(method);
                
                System.out.println("[EventBus] Registered handler: " + object.getClass().getSimpleName() + "." + method.getName() + " for " + eventType.getSimpleName());
            }
        }
    }
    
    /**
     * Unregister an object's event handler methods
     */
    public void unregister(Object object) {
        List<Method> methods = registeredMethods.remove(object);
        if (methods != null) {
            for (Method method : methods) {
                Class<?> eventType = method.getParameterTypes()[0];
                List<EventListener> eventListeners = listeners.get(eventType);
                if (eventListeners != null) {
                    eventListeners.removeIf(listener -> listener.object == object);
                }
            }
        }
    }
    
    /**
     * Post an event to all registered listeners
     */
    public void post(Event event) {
        List<EventListener> eventListeners = listeners.get(event.getClass());
        if (eventListeners != null) {
            for (EventListener listener : eventListeners) {
                try {
                    listener.method.invoke(listener.object, event);
                    if (event.isCancelled()) {
                        break;
                    }
                } catch (Exception e) {
                    System.err.println("[EventBus] Error invoking event handler: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    }
    
    private static class EventListener {
        final Object object;
        final Method method;
        final byte priority;
        
        EventListener(Object object, Method method, byte priority) {
            this.object = object;
            this.method = method;
            this.priority = priority;
        }
    }
}


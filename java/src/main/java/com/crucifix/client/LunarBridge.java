package com.crucifix.client;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Bridge to Lunar Client's obfuscated API using reflection
 * This allows accessing Minecraft classes without direct imports
 */
public class LunarBridge {
    private static Object minecraftInstance = null;
    private static Class<?> minecraftClass = null;
    private static Class<?> entityPlayerClass = null;
    private static Class<?> worldClass = null;
    private static boolean initialized = false;
    
    /**
     * Initialize the bridge by finding Lunar Client's Minecraft instance
     */
    public static boolean initialize() {
        try {
            System.out.println("[LunarBridge] Initializing...");
            
            // Try multiple approaches to find Minecraft
            if (!findMinecraftViaReflection()) {
                System.out.println("[LunarBridge] Failed to find Minecraft via reflection");
                return false;
            }
            
            // Cache common classes
            cacheClasses();
            
            initialized = true;
            System.out.println("[LunarBridge] Initialized successfully!");
            return true;
        } catch (Exception e) {
            System.out.println("[LunarBridge] Initialization failed: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Try to find Minecraft instance using various reflection methods
     */
    private static boolean findMinecraftViaReflection() {
        // Method 1: Try standard Minecraft class
        try {
            minecraftClass = Class.forName("net.minecraft.client.Minecraft");
            Method getMinecraft = minecraftClass.getMethod("getMinecraft");
            minecraftInstance = getMinecraft.invoke(null);
            if (minecraftInstance != null) {
                System.out.println("[LunarBridge] Found Minecraft via standard class");
                return true;
            }
        } catch (Exception e) {
            // Continue to next method
        }
        
        // Method 2: Try to find Lunar's obfuscated classes
        try {
            // Scan all loaded classes for Lunar patterns
            Class<?>[] allClasses = getAllLoadedClasses();
            for (Class<?> clazz : allClasses) {
                String name = clazz.getName();
                
                // Look for Lunar's client class
                if (name.contains("moonsworth") && name.contains("Client")) {
                    try {
                        Method getInstance = clazz.getMethod("getInstance");
                        Object instance = getInstance.invoke(null);
                        if (instance != null) {
                            // Try to get MC from this instance
                            try {
                                Method getMC = clazz.getMethod("getMC");
                                minecraftInstance = getMC.invoke(instance);
                                if (minecraftInstance != null) {
                                    minecraftClass = minecraftInstance.getClass();
                                    System.out.println("[LunarBridge] Found Minecraft via Lunar API: " + name);
                                    return true;
                                }
                            } catch (Exception e2) {
                                // Try other method names
                                for (Method m : clazz.getMethods()) {
                                    if (m.getName().toLowerCase().contains("mc") || 
                                        m.getName().toLowerCase().contains("minecraft")) {
                                        try {
                                            Object result = m.invoke(instance);
                                            if (result != null) {
                                                minecraftInstance = result;
                                                minecraftClass = result.getClass();
                                                System.out.println("[LunarBridge] Found Minecraft via method: " + m.getName());
                                                return true;
                                            }
                                        } catch (Exception e3) {}
                                    }
                                }
                            }
                        }
                    } catch (Exception e) {}
                }
            }
        } catch (Exception e) {
            System.out.println("[LunarBridge] Failed to scan loaded classes: " + e.getMessage());
        }
        
        // Method 3: Try to find by field access
        try {
            ClassLoader cl = Thread.currentThread().getContextClassLoader();
            if (cl != null) {
                try {
                    Class<?> mcClass = cl.loadClass("net.minecraft.client.Minecraft");
                    Field mcField = mcClass.getDeclaredField("theMinecraft");
                    mcField.setAccessible(true);
                    minecraftInstance = mcField.get(null);
                    if (minecraftInstance != null) {
                        minecraftClass = mcClass;
                        System.out.println("[LunarBridge] Found Minecraft via field access");
                        return true;
                    }
                } catch (Exception e) {}
            }
        } catch (Exception e) {}
        
        return false;
    }
    
    /**
     * Cache commonly used classes for performance
     */
    private static void cacheClasses() {
        try {
            if (minecraftClass != null) {
                // Try to find EntityPlayer class
                entityPlayerClass = findClass("net.minecraft.entity.player.EntityPlayer");
                
                // Try to find World class
                worldClass = findClass("net.minecraft.world.World");
            }
        } catch (Exception e) {
            System.out.println("[LunarBridge] Failed to cache classes: " + e.getMessage());
        }
    }
    
    /**
     * Helper to find a class using multiple classloaders
     */
    private static Class<?> findClass(String className) {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e1) {
            try {
                ClassLoader cl = Thread.currentThread().getContextClassLoader();
                if (cl != null) {
                    return cl.loadClass(className);
                }
            } catch (ClassNotFoundException e2) {
                return null;
            }
        }
        return null;
    }
    
    /**
     * Get all loaded classes from the JVM
     */
    private static Class<?>[] getAllLoadedClasses() {
        try {
            ClassLoader cl = ClassLoader.getSystemClassLoader();
            Field field = ClassLoader.class.getDeclaredField("classes");
            field.setAccessible(true);
            @SuppressWarnings("unchecked")
            List<Class<?>> classes = (List<Class<?>>) field.get(cl);
            return classes.toArray(new Class<?>[0]);
        } catch (Exception e) {
            return new Class<?>[0];
        }
    }
    
    /**
     * Get the Minecraft instance
     */
    public static Object getMinecraft() {
        return minecraftInstance;
    }
    
    /**
     * Get the current player
     */
    public static Object getPlayer() {
        if (minecraftInstance == null) return null;
        try {
            Field thePlayerField = minecraftClass.getDeclaredField("thePlayer");
            thePlayerField.setAccessible(true);
            return thePlayerField.get(minecraftInstance);
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * Get the current world
     */
    public static Object getWorld() {
        if (minecraftInstance == null) return null;
        try {
            Field theWorldField = minecraftClass.getDeclaredField("theWorld");
            theWorldField.setAccessible(true);
            return theWorldField.get(minecraftInstance);
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * Get a field value from an object using reflection
     */
    public static Object getField(Object obj, String fieldName) {
        if (obj == null) return null;
        try {
            Field field = obj.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.get(obj);
        } catch (NoSuchFieldException e) {
            try {
                Field field = obj.getClass().getField(fieldName);
                return field.get(obj);
            } catch (Exception e2) {
                return null;
            }
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * Set a field value on an object using reflection
     */
    public static boolean setField(Object obj, String fieldName, Object value) {
        if (obj == null) return false;
        try {
            Field field = obj.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(obj, value);
            return true;
        } catch (NoSuchFieldException e) {
            try {
                Field field = obj.getClass().getField(fieldName);
                field.set(obj, value);
                return true;
            } catch (Exception e2) {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Call a method on an object using reflection
     */
    public static Object callMethod(Object obj, String methodName, Class<?>[] paramTypes, Object... args) {
        if (obj == null) return null;
        try {
            Method method = obj.getClass().getDeclaredMethod(methodName, paramTypes);
            method.setAccessible(true);
            return method.invoke(obj, args);
        } catch (NoSuchMethodException e) {
            try {
                Method method = obj.getClass().getMethod(methodName, paramTypes);
                return method.invoke(obj, args);
            } catch (Exception e2) {
                return null;
            }
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * Check if the bridge is initialized
     */
    public static boolean isInitialized() {
        return initialized;
    }
}

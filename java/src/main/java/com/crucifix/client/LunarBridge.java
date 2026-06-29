package com.crucifix.client;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class LunarBridge {
    private static Object minecraftInstance = null;
    private static Class<?> minecraftClass = null;
    private static boolean initialized = false;
    private static String foundLunarClass = null;
    
    public static boolean initialize() {
        if (initialized) return true;
        
        System.out.println("[LunarBridge] Scanning for Lunar Client API...");
        
        try {
            // Method 1: Search all loaded classes
            Class<?>[] allClasses = getAllLoadedClasses();
            System.out.println("[LunarBridge] Found " + allClasses.length + " loaded classes");
            
            for (Class<?> clazz : allClasses) {
                String className = clazz.getName();
                
                // Look for Lunar classes
                if (className.contains("moonsworth") || className.contains("lunar")) {
                    System.out.println("[LunarBridge] Found Lunar class: " + className);
                    
                    // Try to find a class with getInstance or getMC
                    if (tryInitializeFromClass(clazz)) {
                        System.out.println("[LunarBridge] Successfully initialized from: " + className);
                        initialized = true;
                        foundLunarClass = className;
                        return true;
                    }
                }
            }
            
            // Method 2: Look for Minecraft class specifically
            if (!initialized) {
                System.out.println("[LunarBridge] Searching for Minecraft class...");
                for (Class<?> clazz : allClasses) {
                    String name = clazz.getName();
                    if (name.toLowerCase().contains("minecraft") || 
                        name.toLowerCase().contains("mc") ||
                        name.endsWith("Minecraft")) {
                        System.out.println("[LunarBridge] Found Minecraft candidate: " + name);
                        minecraftClass = clazz;
                        try {
                            Method getInstance = clazz.getMethod("getInstance");
                            minecraftInstance = getInstance.invoke(null);
                            if (minecraftInstance != null) {
                                System.out.println("[LunarBridge] Got Minecraft instance!");
                                initialized = true;
                                return true;
                            }
                        } catch (Exception e) {
                            // Not this class
                        }
                    }
                }
            }
            
            // Method 3: Search Thread context classloader
            if (!initialized) {
                System.out.println("[LunarBridge] Searching Thread context classloader...");
                ClassLoader cl = Thread.currentThread().getContextClassLoader();
                if (cl != null) {
                    // Try to find Lunar's classloader
                    System.out.println("[LunarBridge] Context ClassLoader: " + cl.getClass().getName());
                    // Scan all classes in this loader
                    try {
                        Class<?> loaderClass = cl.getClass();
                        Field classesField = loaderClass.getDeclaredField("classes");
                        classesField.setAccessible(true);
                        java.util.Vector<?> classes = (java.util.Vector<?>) classesField.get(cl);
                        System.out.println("[LunarBridge] Context loader has " + classes.size() + " classes");
                        for (Object obj : classes) {
                            if (obj instanceof Class<?>) {
                                Class<?> c = (Class<?>) obj;
                                String name = c.getName();
                                if (name.contains("moonsworth") || name.contains("lunar")) {
                                    System.out.println("[LunarBridge] Found Lunar class in context loader: " + name);
                                    if (tryInitializeFromClass(c)) {
                                        initialized = true;
                                        return true;
                                    }
                                }
                            }
                        }
                    } catch (Exception e) {
                        System.out.println("[LunarBridge] Could not scan context loader: " + e.getMessage());
                    }
                }
            }
            
            // Method 4: Wait for Lunar via JVMTI (will be triggered from C++)
            System.out.println("[LunarBridge] Could not find Lunar API immediately. Will retry...");
            scheduleRetry();
            
        } catch (Exception e) {
            System.out.println("[LunarBridge] Initialization error: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    private static boolean tryInitializeFromClass(Class<?> clazz) {
        try {
            // Try getInstance
            try {
                Method getInstance = clazz.getMethod("getInstance");
                Object instance = getInstance.invoke(null);
                if (instance != null) {
                    System.out.println("[LunarBridge] Found getInstance on: " + clazz.getName());
                    
                    // Try to get Minecraft from this instance
                    return tryExtractMinecraft(instance);
                }
            } catch (Exception e) {}
            
            // Try static methods
            for (Method m : clazz.getMethods()) {
                if (m.getParameterCount() == 0 && 
                    java.lang.reflect.Modifier.isStatic(m.getModifiers())) {
                    String name = m.getName().toLowerCase();
                    if (name.contains("mc") || name.contains("minecraft") || 
                        name.contains("getinstance")) {
                        try {
                            Object result = m.invoke(null);
                            if (result != null) {
                                if (tryExtractMinecraft(result)) {
                                    return true;
                                }
                            }
                        } catch (Exception e) {}
                    }
                }
            }
            
            // Try get fields
            for (Field f : clazz.getFields()) {
                String name = f.getName().toLowerCase();
                if (name.contains("mc") || name.contains("minecraft")) {
                    try {
                        Object result = f.get(null);
                        if (result != null) {
                            if (tryExtractMinecraft(result)) {
                                return true;
                            }
                        }
                    } catch (Exception e) {}
                }
            }
            
        } catch (Exception e) {
            System.out.println("[LunarBridge] Error trying class: " + clazz.getName() + " - " + e.getMessage());
        }
        return false;
    }
    
    private static boolean tryExtractMinecraft(Object obj) {
        if (obj == null) return false;
        
        Class<?> clazz = obj.getClass();
        System.out.println("[LunarBridge] Trying to extract Minecraft from: " + clazz.getName());
        
        // Try methods
        for (Method m : clazz.getMethods()) {
            if (m.getParameterCount() == 0) {
                String name = m.getName().toLowerCase();
                if (name.contains("mc") || name.contains("minecraft") || 
                    name.contains("getmc") || name.contains("getminecraft")) {
                    try {
                        Object result = m.invoke(obj);
                        if (result != null) {
                            System.out.println("[LunarBridge] Found Minecraft via method: " + m.getName());
                            minecraftInstance = result;
                            minecraftClass = result.getClass();
                            return true;
                        }
                    } catch (Exception e) {}
                }
            }
        }
        
        // Try fields
        for (Field f : clazz.getFields()) {
            String name = f.getName().toLowerCase();
            if (name.contains("mc") || name.contains("minecraft")) {
                try {
                    Object result = f.get(obj);
                    if (result != null) {
                        System.out.println("[LunarBridge] Found Minecraft via field: " + f.getName());
                        minecraftInstance = result;
                        minecraftClass = result.getClass();
                        return true;
                    }
                } catch (Exception e) {}
            }
        }
        
        // If this object itself might be Minecraft
        String className = clazz.getName().toLowerCase();
        if (className.contains("minecraft") || className.contains("mc")) {
            System.out.println("[LunarBridge] Object itself is Minecraft class");
            minecraftInstance = obj;
            minecraftClass = clazz;
            return true;
        }
        
        return false;
    }
    
    private static Class<?>[] getAllLoadedClasses() {
        try {
            // Try to use ClassLoader.getLoadedClasses via reflection
            ClassLoader cl = ClassLoader.getSystemClassLoader();
            java.lang.reflect.Method method = ClassLoader.class.getDeclaredMethod("getAllLoadedClasses");
            method.setAccessible(true);
            return (Class<?>[]) method.invoke(null);
        } catch (Exception e1) {
            try {
                // Fallback: Try Thread context
                ClassLoader contextCL = Thread.currentThread().getContextClassLoader();
                if (contextCL != null) {
                    java.lang.reflect.Field field = ClassLoader.class.getDeclaredField("classes");
                    field.setAccessible(true);
                    java.util.Vector<?> vec = (java.util.Vector<?>) field.get(contextCL);
                    return vec.toArray(new Class<?>[0]);
                }
            } catch (Exception e2) {
                try {
                    // Another fallback
                    Class<?> clazz = Class.forName("java.lang.ClassLoader");
                    java.lang.reflect.Method m = clazz.getDeclaredMethod("getAllLoadedClasses");
                    m.setAccessible(true);
                    return (Class<?>[]) m.invoke(null);
                } catch (Exception e3) {}
            }
        }
        return new Class<?>[0];
    }
    
    private static void scheduleRetry() {
        new Thread(() -> {
            try {
                System.out.println("[LunarBridge] Retry in 3 seconds...");
                Thread.sleep(3000);
                System.out.println("[LunarBridge] Retrying...");
                initialize();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }
    
    public static Object getMinecraft() {
        if (!initialized) {
            System.out.println("[LunarBridge] WARNING: getMinecraft called before initialization!");
            initialize();
        }
        return minecraftInstance;
    }
    
    public static Class<?> getMinecraftClass() {
        return minecraftClass;
    }
    
    public static boolean isInitialized() {
        return initialized;
    }
    
    public static String getFoundLunarClass() {
        return foundLunarClass;
    }
}

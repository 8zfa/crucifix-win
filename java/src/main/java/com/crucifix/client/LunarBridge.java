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
        if (initialized) {
            System.out.println("[LunarBridge] Already initialized");
            return true;
        }
        
        System.out.println("[LunarBridge] Initializing...");
        
        try {
            // METHOD 1: Try to find Minecraft class directly
            // Lunar Client uses obfuscated class names, but Minecraft is still there
            String[] possibleMinecraftClasses = {
                "net.minecraft.client.Minecraft",
                "net.minecraft.client.MinecraftClient",
                "ave", // 1.8.9 obfuscated name
                "ave",
                "blt",
                "Minecraft"
            };
            
            for (String className : possibleMinecraftClasses) {
                try {
                    Class<?> clazz = Class.forName(className);
                    System.out.println("[LunarBridge] Found Minecraft class: " + className);
                    
                    // Try to get instance
                    try {
                        Method getMinecraft = clazz.getMethod("getMinecraft");
                        Object mc = getMinecraft.invoke(null);
                        if (mc != null) {
                            minecraftInstance = mc;
                            minecraftClass = clazz;
                            initialized = true;
                            System.out.println("[LunarBridge] Got Minecraft instance via getMinecraft()");
                            return true;
                        }
                    } catch (Exception e) {
                        // Try getInstance
                        try {
                            Method getInstance = clazz.getMethod("getInstance");
                            Object mc = getInstance.invoke(null);
                            if (mc != null) {
                                minecraftInstance = mc;
                                minecraftClass = clazz;
                                initialized = true;
                                System.out.println("[LunarBridge] Got Minecraft instance via getInstance()");
                                return true;
                            }
                        } catch (Exception e2) {
                            // Try field
                            try {
                                Field field = clazz.getField("instance");
                                Object mc = field.get(null);
                                if (mc != null) {
                                    minecraftInstance = mc;
                                    minecraftClass = clazz;
                                    initialized = true;
                                    System.out.println("[LunarBridge] Got Minecraft instance via field");
                                    return true;
                                }
                            } catch (Exception e3) {
                                // Try static field
                                try {
                                    Field[] fields = clazz.getFields();
                                    for (Field f : fields) {
                                        if (f.getType().getName().contains("Minecraft") || 
                                            f.getType().getName().contains("ave")) {
                                            f.setAccessible(true);
                                            Object mc = f.get(null);
                                            if (mc != null) {
                                                minecraftInstance = mc;
                                                minecraftClass = clazz;
                                                initialized = true;
                                                System.out.println("[LunarBridge] Got Minecraft via field: " + f.getName());
                                                return true;
                                            }
                                        }
                                    }
                                } catch (Exception e4) {}
                            }
                        }
                    }
                } catch (ClassNotFoundException e) {
                    // Continue to next class
                }
            }
            
            // METHOD 2: Try Lunar Client API
            String[] lunarClassPatterns = {
                "com.moonsworth.lunar.client.LunarClientAPI",
                "com.moonsworth.lunar.client.LunarClient",
                "com.moonsworth.lunar.client.Client",
                "com.moonsworth.lunar.CRIOIRICHCHHCOOIHRRIRIRIRROOOI"
            };
            
            for (String className : lunarClassPatterns) {
                try {
                    Class<?> clazz = Class.forName(className);
                    System.out.println("[LunarBridge] Found Lunar class: " + className);
                    
                    // Try to get API instance
                    try {
                        Method getInstance = clazz.getMethod("getInstance");
                        Object api = getInstance.invoke(null);
                        if (api != null) {
                            // Try to get Minecraft from API
                            try {
                                Method getMinecraft = api.getClass().getMethod("getMinecraft");
                                Object mc = getMinecraft.invoke(api);
                                if (mc != null) {
                                    minecraftInstance = mc;
                                    minecraftClass = mc.getClass();
                                    initialized = true;
                                    System.out.println("[LunarBridge] Got Minecraft via Lunar API: " + className);
                                    return true;
                                }
                            } catch (Exception e) {}
                            
                            // Try getMC
                            try {
                                Method getMC = api.getClass().getMethod("getMC");
                                Object mc = getMC.invoke(api);
                                if (mc != null) {
                                    minecraftInstance = mc;
                                    minecraftClass = mc.getClass();
                                    initialized = true;
                                    System.out.println("[LunarBridge] Got Minecraft via Lunar API getMC(): " + className);
                                    return true;
                                }
                            } catch (Exception e) {}
                        }
                    } catch (Exception e) {}
                } catch (ClassNotFoundException e) {}
            }
            
            // METHOD 3: Scan all loaded classes
            System.out.println("[LunarBridge] Scanning all loaded classes...");
            try {
                ClassLoader cl = Thread.currentThread().getContextClassLoader();
                if (cl != null) {
                    // Try to get all loaded classes
                    Method getAllLoadedClasses = ClassLoader.class.getDeclaredMethod("getAllLoadedClasses");
                    getAllLoadedClasses.setAccessible(true);
                    Class<?>[] classes = (Class<?>[]) getAllLoadedClasses.invoke(null);
                    System.out.println("[LunarBridge] Found " + classes.length + " loaded classes");
                    
                    for (Class<?> c : classes) {
                        String name = c.getName();
                        // Look for any class that might be Minecraft
                        if (name.toLowerCase().contains("minecraft") || 
                            name.toLowerCase().contains("client") ||
                            name.endsWith("Minecraft")) {
                            System.out.println("[LunarBridge] Found candidate: " + name);
                            
                            try {
                                // Try static methods
                                for (Method m : c.getMethods()) {
                                    if (m.getParameterCount() == 0 && 
                                        java.lang.reflect.Modifier.isStatic(m.getModifiers())) {
                                        String mName = m.getName().toLowerCase();
                                        if (mName.contains("getinstance") || 
                                            mName.contains("getmc") || 
                                            mName.contains("getminecraft")) {
                                            try {
                                                Object result = m.invoke(null);
                                                if (result != null) {
                                                    System.out.println("[LunarBridge] Found Minecraft via: " + m.getName());
                                                    minecraftInstance = result;
                                                    minecraftClass = result.getClass();
                                                    initialized = true;
                                                    return true;
                                                }
                                            } catch (Exception e) {}
                                        }
                                    }
                                }
                            } catch (Exception e) {}
                        }
                    }
                }
            } catch (Exception e) {
                System.out.println("[LunarBridge] Could not scan loaded classes: " + e.getMessage());
            }
            
            System.out.println("[LunarBridge] Failed to find Minecraft via reflection");
            return false;
            
        } catch (Exception e) {
            System.out.println("[LunarBridge] Error: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
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
    
    // Helper methods for reflection
    public static Object getField(Object target, String fieldName) {
        try {
            Class<?> clazz = target.getClass();
            Field field = clazz.getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.get(target);
        } catch (Exception e) {
            // Try superclass
            try {
                Class<?> superClass = target.getClass().getSuperclass();
                while (superClass != null) {
                    try {
                        Field field = superClass.getDeclaredField(fieldName);
                        field.setAccessible(true);
                        return field.get(target);
                    } catch (Exception e2) {
                        superClass = superClass.getSuperclass();
                    }
                }
            } catch (Exception e3) {}
            System.out.println("[LunarBridge] Failed to get field: " + fieldName);
            return null;
        }
    }
    
    public static void setField(Object target, String fieldName, Object value) {
        try {
            Class<?> clazz = target.getClass();
            Field field = clazz.getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (Exception e) {
            System.out.println("[LunarBridge] Failed to set field: " + fieldName);
        }
    }
    
    public static Object callMethod(Object target, String methodName, Class<?>[] paramTypes, Object... args) {
        try {
            Class<?> clazz = target.getClass();
            Method method = clazz.getDeclaredMethod(methodName, paramTypes);
            method.setAccessible(true);
            return method.invoke(target, args);
        } catch (Exception e) {
            System.out.println("[LunarBridge] Failed to call method: " + methodName);
            return null;
        }
    }
}

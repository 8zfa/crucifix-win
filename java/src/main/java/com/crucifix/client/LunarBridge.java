package com.crucifix.client;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class LunarBridge {
    private static Object minecraftInstance = null;
    private static Class<?> minecraftClass = null;
    private static boolean initialized = false;
    private static String foundMethod = null;
    
    public static boolean initialize() {
        if (initialized) {
            System.out.println("[LunarBridge] Already initialized");
            return true;
        }
        
        System.out.println("[LunarBridge] Initializing...");
        
        try {
            // METHOD 1: Try to find Minecraft class directly
            String[] possibleClasses = {
                "net.minecraft.client.Minecraft",
                "ave",  // 1.8.9 obfuscated name
                "blt",
                "Minecraft"
            };
            
            for (String className : possibleClasses) {
                try {
                    Class<?> clazz = Class.forName(className);
                    System.out.println("[LunarBridge] Found: " + className);
                    
                    // Try getMinecraft()
                    try {
                        Method m = clazz.getMethod("getMinecraft");
                        Object mc = m.invoke(null);
                        if (mc != null) {
                            minecraftInstance = mc;
                            minecraftClass = mc.getClass();
                            initialized = true;
                            foundMethod = "getMinecraft()";
                            System.out.println("[LunarBridge] Got Minecraft via getMinecraft()");
                            return true;
                        }
                    } catch (Exception e) {}
                    
                    // Try getInstance()
                    try {
                        Method m = clazz.getMethod("getInstance");
                        Object mc = m.invoke(null);
                        if (mc != null) {
                            minecraftInstance = mc;
                            minecraftClass = mc.getClass();
                            initialized = true;
                            foundMethod = "getInstance()";
                            System.out.println("[LunarBridge] Got Minecraft via getInstance()");
                            return true;
                        }
                    } catch (Exception e) {}
                    
                } catch (ClassNotFoundException e) {}
            }
            
            // METHOD 2: Try Lunar API classes
            String[] lunarClasses = {
                "com.moonsworth.lunar.client.LunarClientAPI",
                "com.moonsworth.lunar.client.LunarClient",
                "com.moonsworth.lunar.client.Client"
            };
            
            for (String className : lunarClasses) {
                try {
                    Class<?> clazz = Class.forName(className);
                    System.out.println("[LunarBridge] Found Lunar API: " + className);
                    
                    try {
                        Method getInstance = clazz.getMethod("getInstance");
                        Object api = getInstance.invoke(null);
                        if (api != null) {
                            // Try getMinecraft
                            try {
                                Method m = api.getClass().getMethod("getMinecraft");
                                Object mc = m.invoke(api);
                                if (mc != null) {
                                    minecraftInstance = mc;
                                    minecraftClass = mc.getClass();
                                    initialized = true;
                                    foundMethod = "LunarAPI.getMinecraft()";
                                    System.out.println("[LunarBridge] Got Minecraft via Lunar API");
                                    return true;
                                }
                            } catch (Exception e) {}
                            
                            // Try getMC
                            try {
                                Method m = api.getClass().getMethod("getMC");
                                Object mc = m.invoke(api);
                                if (mc != null) {
                                    minecraftInstance = mc;
                                    minecraftClass = mc.getClass();
                                    initialized = true;
                                    foundMethod = "LunarAPI.getMC()";
                                    System.out.println("[LunarBridge] Got Minecraft via Lunar API getMC()");
                                    return true;
                                }
                            } catch (Exception e) {}
                        }
                    } catch (Exception e) {}
                } catch (ClassNotFoundException e) {}
            }
            
            // METHOD 3: Use context classloader
            System.out.println("[LunarBridge] Trying context classloader...");
            try {
                ClassLoader cl = Thread.currentThread().getContextClassLoader();
                String[] contextClasses = {"ave", "blt", "net.minecraft.client.Minecraft"};
                for (String className : contextClasses) {
                    try {
                        Class<?> clazz = Class.forName(className, true, cl);
                        System.out.println("[LunarBridge] Found via context CL: " + className);

                        try {
                            Method m = clazz.getMethod("getMinecraft");
                            Object mc = m.invoke(null);
                            if (mc != null) {
                                minecraftInstance = mc;
                                minecraftClass = mc.getClass();
                                initialized = true;
                                foundMethod = "contextCL.getMinecraft()";
                                System.out.println("[LunarBridge] Got Minecraft via context classloader!");
                                return true;
                            }
                        } catch (Exception e) {}

                        try {
                            Method m = clazz.getMethod("getInstance");
                            Object mc = m.invoke(null);
                            if (mc != null) {
                                minecraftInstance = mc;
                                minecraftClass = mc.getClass();
                                initialized = true;
                                foundMethod = "contextCL.getInstance()";
                                System.out.println("[LunarBridge] Got Minecraft via context classloader!");
                                return true;
                            }
                        } catch (Exception e) {}

                    } catch (ClassNotFoundException e) {}
                }
            } catch (Exception e) {
                System.out.println("[LunarBridge] Context classloader scan failed: " + e.getMessage());
            }
            
            System.out.println("[LunarBridge] Failed to find Minecraft");
            return false;
            
        } catch (Exception e) {
            System.out.println("[LunarBridge] Error: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    public static Object getMinecraft() {
        if (!initialized) {
            System.out.println("[LunarBridge] getMinecraft() called before init!");
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
    
    public static String getFoundMethod() {
        return foundMethod;
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

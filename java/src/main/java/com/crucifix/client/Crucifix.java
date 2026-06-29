package com.crucifix.client;

import com.crucifix.client.core.ModuleManager;
import com.crucifix.client.core.EventBus;
import com.crucifix.client.gui.ClickGUI;
import com.crucifix.client.hud.HUDManager;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.StringWriter;

public class Crucifix {
    public static final String VERSION = "1.0.0";
    public static final String NAME = "CRUCIFIX.WIN";
    
    // SINGLETON PATTERN - Fix the null instance issue
    private static Crucifix instance = new Crucifix();  // ✅ Initialize immediately!
    private boolean lunarBridgeInitialized = false;
    private boolean modulesInitialized = false;
    private boolean clickGUIInitialized = false;
    
    private EventBus eventBus;
    private ModuleManager moduleManager;
    private ClickGUI clickGUI;
    
    private Crucifix() {
        System.out.println("[CRUCIFIX] Instance created");
        eventBus = new EventBus();
        moduleManager = ModuleManager.getInstance();
    }
    
    public static Crucifix getInstance() {
        return instance;
    }
    
    public EventBus getEventBus() {
        return eventBus;
    }
    
    public ModuleManager getModuleManager() {
        return moduleManager;
    }
    
    public ClickGUI getClickGUI() {
        return clickGUI;
    }
    
    // Called from JVMTI hook (C++)
    public static void init() {
        System.out.println("[CRUCIFIX] init() method called from JVMTI");
        
        try {
            // Use the instance safely
            Crucifix inst = getInstance();
            if (inst == null) {
                System.out.println("[CRUCIFIX] ERROR: Instance is null! Creating new...");
                instance = new Crucifix();
                inst = instance;
            }
            
            System.out.println("[CRUCIFIX] Waiting 5 seconds for Lunar to fully load...");
            Thread.sleep(5000);
            
            System.out.println("[CRUCIFIX] Initializing LunarBridge...");
            inst.lunarBridgeInitialized = LunarBridge.initialize();
            
            if (inst.lunarBridgeInitialized) {
                System.out.println("[CRUCIFIX] LunarBridge initialized successfully!");
                
                Object mc = LunarBridge.getMinecraft();
                if (mc != null) {
                    System.out.println("[CRUCIFIX] Got Minecraft instance: " + mc);
                    
                    // Initialize modules
                    ModuleManager.initModules(mc);
                    inst.modulesInitialized = true;
                    System.out.println("[CRUCIFIX] Modules initialized!");
                    
                    // Initialize ClickGUI
                    inst.clickGUI = ClickGUI.getInstance();
                    inst.clickGUIInitialized = true;
                    System.out.println("[CRUCIFIX] ClickGUI initialized!");
                    
                    // Initialize HUD
                    HUDManager.getInstance();
                    System.out.println("[CRUCIFIX] HUD initialized!");
                    
                    System.out.println("[CRUCIFIX] ===== INITIALIZATION COMPLETE! =====");
                } else {
                    System.out.println("[CRUCIFIX] Minecraft instance is null!");
                }
            } else {
                System.out.println("[CRUCIFIX] LunarBridge initialization failed!");
                // Schedule retry
                scheduleRetry();
            }
            
        } catch (Throwable t) {
            System.out.println("[CRUCIFIX] Exception in init(): " + t.getMessage());
            t.printStackTrace();
            
            // Write to file
            try {
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                t.printStackTrace(pw);
                String stackTrace = sw.toString();
                
                FileWriter fw = new FileWriter(System.getProperty("user.home") + "/crucifix_error.log");
                fw.write(stackTrace);
                fw.close();
                System.out.println("[CRUCIFIX] Error details written to crucifix_error.log");
            } catch (Exception e) {
                // Ignore
            }
            
            // Retry
            scheduleRetry();
        }
    }
    
    private static void scheduleRetry() {
        new Thread(() -> {
            try {
                System.out.println("[CRUCIFIX] Retrying initialization in 5 seconds...");
                Thread.sleep(5000);
                System.out.println("[CRUCIFIX] Retrying...");
                init();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }
    
    // Called from C++ when RSHIFT is pressed
    public static void toggleClickGUI() {
        if (ClickGUI.getInstance() != null) {
            ClickGUI.getInstance().toggle();
            System.out.println("[CRUCIFIX] ClickGUI toggled");
        } else {
            System.out.println("[CRUCIFIX] ClickGUI not available");
        }
    }
    
    // Called from C++ render hook
    public static void renderGUI() {
        if (instance != null && instance.clickGUIInitialized) {
            ClickGUI.getInstance().render();
        }
    }
    
    public static boolean isInitialized() {
        return instance != null && instance.modulesInitialized;
    }
    
    public static void sendChatMessageStatic(String message) {
        try {
            Object mc = LunarBridge.getMinecraft();
            if (mc == null) return;
            
            Object player = LunarBridge.getField(mc, "thePlayer");
            if (player == null) return;
            
            LunarBridge.callMethod(player, "sendChatMessage", new Class<?>[]{String.class}, message);
            System.out.println("[CRUCIFIX] Sent chat message: " + message);
        } catch (Exception e) {
            System.out.println("[CRUCIFIX] Failed to send chat message: " + e.getMessage());
        }
    }
}

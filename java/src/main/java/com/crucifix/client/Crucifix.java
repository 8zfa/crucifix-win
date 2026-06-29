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
    
    private static Crucifix instance;
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
        if (instance == null) {
            System.out.println("[CRUCIFIX] Creating new instance...");
            instance = new Crucifix();
        }
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
    
    public static void init() {
        System.out.println("[CRUCIFIX] init() called from JVMTI");
        
        try {
            Crucifix inst = getInstance();
            
            System.out.println("[CRUCIFIX] Waiting 5 seconds for Lunar to fully load...");
            Thread.sleep(5000);
            
            System.out.println("[CRUCIFIX] Initializing LunarBridge...");
            inst.lunarBridgeInitialized = LunarBridge.initialize();
            
            if (inst.lunarBridgeInitialized) {
                System.out.println("[CRUCIFIX] LunarBridge initialized!");
                Object mc = LunarBridge.getMinecraft();
                
                if (mc != null) {
                    System.out.println("[CRUCIFIX] Got Minecraft: " + mc);
                    ModuleManager.initModules(mc);
                    inst.modulesInitialized = true;
                    System.out.println("[CRUCIFIX] Modules initialized!");
                    
                    inst.clickGUI = ClickGUI.getInstance();
                    inst.clickGUIInitialized = true;
                    System.out.println("[CRUCIFIX] ClickGUI initialized!");
                    
                    HUDManager.getInstance();
                    System.out.println("[CRUCIFIX] HUD initialized!");
                    
                    System.out.println("[CRUCIFIX] ===== ALL SYSTEMS GO! =====");
                } else {
                    System.out.println("[CRUCIFIX] Minecraft is null!");
                    scheduleRetry();
                }
            } else {
                System.out.println("[CRUCIFIX] LunarBridge failed!");
                scheduleRetry();
            }
            
        } catch (Throwable t) {
            System.out.println("[CRUCIFIX] Error: " + t.getMessage());
            t.printStackTrace();
            
            try {
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                t.printStackTrace(pw);
                FileWriter fw = new FileWriter(System.getProperty("user.home") + "/crucifix_error.log");
                fw.write(sw.toString());
                fw.close();
            } catch (Exception e) {}
            
            scheduleRetry();
        }
    }
    
    private static void scheduleRetry() {
        new Thread(() -> {
            try {
                Thread.sleep(3000);
                System.out.println("[CRUCIFIX] Retrying init...");
                init();
            } catch (InterruptedException e) {}
        }).start();
    }
    
    public static void toggleClickGUI() {
        System.out.println("[CRUCIFIX] toggleClickGUI() called");
        try {
            ClickGUI clickGUI = ClickGUI.getInstance();
            if (clickGUI != null) {
                clickGUI.toggle();
                System.out.println("[CRUCIFIX] ClickGUI toggled: " + clickGUI.isOpen());
            } else {
                System.out.println("[CRUCIFIX] ClickGUI is null!");
            }
        } catch (Throwable t) {
            System.out.println("[CRUCIFIX] toggleClickGUI error: " + t.getMessage());
            t.printStackTrace();
        }
    }
    
    public static void renderGUI() {
        try {
            ClickGUI clickGUI = ClickGUI.getInstance();
            if (clickGUI != null && clickGUI.isOpen()) {
                clickGUI.render();
            }
        } catch (Throwable t) {
            // Silent fail to avoid spam
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

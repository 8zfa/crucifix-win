package com.crucifix.client;

import com.crucifix.client.core.EventBus;
import com.crucifix.client.core.ModuleManager;
import com.crucifix.client.core.ConfigManager;
import com.crucifix.client.core.AnimationEngine;
import com.crucifix.client.core.CommandManager;
import com.crucifix.client.gui.ClickGUI;
import com.crucifix.client.hud.HUDManager;
import com.crucifix.client.modules.Category;
import com.crucifix.client.modules.Module;
import com.crucifix.client.events.UpdateEvent;
import com.crucifix.client.events.KeyEvent;
import com.crucifix.client.events.RenderEvent;

/**
 * Main entry point for the Crucifix client
 */
public class Crucifix {
    public static final String VERSION = "1.0.0";
    public static final String NAME = "CRUCIFIX.WIN";
    
    // Static initializer - runs when class is loaded by classloader
    static {
        try {
            java.io.FileWriter fw = new java.io.FileWriter(System.getProperty("user.home") + "\\AppData\\Local\\Temp\\crucifix.log");
            fw.write("Crucifix class loaded by Lunar Client at: " + new java.util.Date() + "\n");
            fw.close();
            System.out.println("[CRUCIFIX] Class loaded by Lunar Client!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private static Crucifix instance;
    
    private EventBus eventBus;
    private ModuleManager moduleManager;
    private ConfigManager configManager;
    private AnimationEngine animationEngine;
    private HUDManager hudManager;
    private ClickGUI clickGUI;
    private CommandManager commandManager;
    
    private boolean initialized = false;
    private boolean lunarBridgeInitialized = false;
    
    /**
     * Called from native code when the DLL is injected
     */
    public static void init() {
        java.io.FileWriter debugLog = null;
        try {
            debugLog = new java.io.FileWriter(System.getProperty("user.home") + "\\AppData\\Local\\Temp\\crucifix_debug.log", true);
            
            debugLog.write("[CRUCIFIX] init() method called from JVMTI\n");
            debugLog.flush();
            
            System.out.println("[CRUCIFIX] init() method called from JVMTI");
            
            // Wait for Lunar Client to fully load
            debugLog.write("[CRUCIFIX] Waiting 5 seconds for Lunar to fully load...\n");
            debugLog.flush();
            System.out.println("[CRUCIFIX] Waiting 5 seconds for Lunar to fully load...");
            Thread.sleep(5000);
            
            // Initialize LunarBridge first
            debugLog.write("[CRUCIFIX] Initializing LunarBridge...\n");
            debugLog.flush();
            System.out.println("[CRUCIFIX] Initializing LunarBridge...");
            
            boolean bridgeSuccess = LunarBridge.initialize();
            instance.lunarBridgeInitialized = bridgeSuccess;
            
            if (bridgeSuccess) {
                debugLog.write("[CRUCIFIX] LunarBridge initialized successfully!\n");
                debugLog.flush();
                System.out.println("[CRUCIFIX] LunarBridge initialized successfully!");
            } else {
                debugLog.write("[CRUCIFIX] LunarBridge initialization failed, continuing without Minecraft access\n");
                debugLog.flush();
                System.out.println("[CRUCIFIX] LunarBridge initialization failed, continuing without Minecraft access");
            }
            
            // Write debug info to file
            try {
                java.io.FileWriter fw = new java.io.FileWriter(System.getProperty("user.home") + "\\AppData\\Local\\Temp\\crucifix_init.log");
                fw.write("init() called at: " + new java.util.Date() + "\n");
                fw.write("Thread: " + Thread.currentThread().getName() + "\n");
                fw.write("LunarBridge: " + (bridgeSuccess ? "SUCCESS" : "FAILED") + "\n");
                fw.close();
            } catch (Exception e) {
                debugLog.write("[CRUCIFIX] Failed to write init log: " + e.getMessage() + "\n");
                debugLog.flush();
            }
            
            debugLog.write("[CRUCIFIX] Calling initialize()...\n");
            debugLog.flush();
            
            initialize();
            
            debugLog.write("[CRUCIFIX] init() completed successfully\n");
            debugLog.flush();
            System.out.println("[CRUCIFIX] init() completed successfully");
            
        } catch (Throwable t) {
            String errorMsg = "[CRUCIFIX] Exception in init(): " + t.getMessage();
            System.out.println(errorMsg);
            t.printStackTrace(System.out);
            
            // Write full stack trace to file
            try {
                java.io.FileWriter fw = new java.io.FileWriter(System.getProperty("user.home") + "\\AppData\\Local\\Temp\\crucifix_error.log");
                fw.write("Exception in init(): " + t.getMessage() + "\n");
                fw.write("Stack trace:\n");
                for (StackTraceElement ste : t.getStackTrace()) {
                    fw.write("  " + ste.toString() + "\n");
                }
                if (t.getCause() != null) {
                    fw.write("Caused by: " + t.getCause().getMessage() + "\n");
                    for (StackTraceElement ste : t.getCause().getStackTrace()) {
                        fw.write("  " + ste.toString() + "\n");
                    }
                }
                fw.close();
                System.out.println("[CRUCIFIX] Error details written to crucifix_error.log");
            } catch (Exception e) {
                System.out.println("[CRUCIFIX] Failed to write error log: " + e.getMessage());
            }
            
            // Also write to debug log if it's open
            if (debugLog != null) {
                try {
                    debugLog.write(errorMsg + "\n");
                    t.printStackTrace(new java.io.PrintWriter(debugLog));
                    debugLog.flush();
                } catch (Exception e) {
                    // Ignore
                }
            }
        } finally {
            if (debugLog != null) {
                try {
                    debugLog.close();
                } catch (Exception e) {
                    // Ignore
                }
            }
        }
    }
    
    /**
     * Called from native code when the DLL is injected
     */
    public static void initialize() {
        if (instance == null) {
            instance = new Crucifix();
            instance.initializeInternal();
        }
    }
    
    private void initializeInternal() {
        try {
            System.out.println("[CRUCIFIX] Initializing " + NAME + " v" + VERSION);
            
            // Initialize core components
            eventBus = new EventBus();
            moduleManager = new ModuleManager();
            configManager = new ConfigManager();
            animationEngine = new AnimationEngine();
            hudManager = new HUDManager();
            clickGUI = new ClickGUI();
            commandManager = new CommandManager();
            
            System.out.println("[CRUCIFIX] Core components initialized");
            
            // Register event handlers
            registerEventHandlers();
            
            System.out.println("[CRUCIFIX] Event handlers registered");
            
            // Load configuration
            configManager.loadConfig();
            
            System.out.println("[CRUCIFIX] Config loaded");
            
            // Initialize modules only if LunarBridge succeeded
            if (lunarBridgeInitialized) {
                moduleManager.initializeModules();
                System.out.println("[CRUCIFIX] Modules initialized");
            } else {
                System.out.println("[CRUCIFIX] Skipping module initialization (LunarBridge failed)");
            }
            
            initialized = true;
            System.out.println("[CRUCIFIX] Initialization complete");
            
            // Show HUD notification instead of chat message
            hudManager.addNotification("§b[CRUCIFIX] §fInjected successfully! §7Right Shift to toggle menu", 5000);
        } catch (Exception e) {
            System.out.println("[CRUCIFIX] Initialization failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Called from native C++ code to fire key events
     */
    public static void fireKeyEvent(int keyCode, boolean pressed) {
        if (instance != null && instance.initialized) {
            System.out.println("[CRUCIFIX] Received KeyEvent: " + keyCode + " pressed: " + pressed);
            KeyEvent event = new KeyEvent(keyCode);
            instance.eventBus.post(event);
        } else {
            System.out.println("[CRUCIFIX] Cannot fire KeyEvent - instance or initialized is null");
        }
    }
    
    /**
     * Called from native C++ code to fire render events
     */
    public static void fireRenderEvent(float partialTicks) {
        if (instance != null && instance.initialized) {
            RenderEvent event = new RenderEvent(partialTicks);
            instance.eventBus.post(event);
        }
    }
    
    /**
     * Called from native C++ code to fire update events
     */
    public static void fireUpdateEvent() {
        if (instance != null && instance.initialized) {
            UpdateEvent event = new UpdateEvent(true);
            instance.eventBus.post(event);
        }
    }
    
    /**
     * Called from native C++ code to toggle ClickGUI
     */
    public static void toggleClickGUI() {
        if (instance != null && instance.clickGUI != null) {
            instance.clickGUI.toggle();
            System.out.println("[CRUCIFIX] ClickGUI toggled via native call");
        }
    }
    
    /**
     * Called from native C++ code to fire packet events
     */
    public static void firePacketEvent(long packetPtr, boolean cancelled) {
        if (instance != null && instance.initialized) {
            // For now, we'll handle packet events via reflection in CommandManager
            // The pointer approach is complex, so we'll use a simpler method
        }
    }
    
    private void sendChatMessage(String message) {
        try {
            // Use LunarBridge to avoid compile-time dependency on Minecraft
            Object mc = LunarBridge.getMinecraft();
            if (mc == null) return;
            
            // Get the player
            Object player = LunarBridge.getField(mc, "thePlayer");
            if (player == null) return;
            
            // Send chat message
            LunarBridge.callMethod(player, "sendChatMessage", new Class<?>[]{String.class}, message);
            
            System.out.println("[CRUCIFIX] Sent chat message: " + message);
        } catch (Exception e) {
            System.out.println("[CRUCIFIX] Failed to send chat message: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public static void sendChatMessageStatic(String message) {
        if (instance != null) {
            instance.sendChatMessage(message);
        }
    }
    
    private void registerEventHandlers() {
        eventBus.register(this);
        eventBus.register(commandManager);
    }
    
    @com.crucifix.client.events.SubscribeEvent(priority = Byte.MAX_VALUE)
    public void onUpdate(UpdateEvent event) {
        if (!initialized) return;
        
        // Update animation engine
        animationEngine.update();
        
        // Update HUD
        hudManager.update();
    }
    
    @com.crucifix.client.events.SubscribeEvent(priority = Byte.MAX_VALUE)
    public void onRender(RenderEvent event) {
        if (!initialized) return;
        
        // Render HUD
        hudManager.render(event.getPartialTicks());
        
        // Render ClickGUI if open
        if (clickGUI.isOpen()) {
            clickGUI.render(event.getPartialTicks());
        }
    }
    
    @com.crucifix.client.events.SubscribeEvent(priority = Byte.MAX_VALUE)
    public void onKey(KeyEvent event) {
        if (!initialized) return;
        
        // Check for ClickGUI toggle (RSHIFT)
        if (event.getKeyCode() == 0xA1) { // VK_RSHIFT
            clickGUI.toggle();
            event.setCancelled(true);
            return;
        }
        
        // Handle module keybinds
        for (Module module : moduleManager.getModules()) {
            if (module.getKeybind() == event.getKeyCode()) {
                module.toggle();
                event.setCancelled(true);
                break;
            }
        }
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
    
    public ConfigManager getConfigManager() {
        return configManager;
    }
    
    public AnimationEngine getAnimationEngine() {
        return animationEngine;
    }
    
    public HUDManager getHUDManager() {
        return hudManager;
    }
    
    public ClickGUI getClickGUI() {
        return clickGUI;
    }
    
    public boolean isInitialized() {
        return initialized;
    }
    
    public boolean isLunarBridgeInitialized() {
        return lunarBridgeInitialized;
    }
}


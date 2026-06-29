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
    
    private static boolean initialized = false;
    private static boolean initAttempted = false;
    private static int retryCount = 0;
    private static final int MAX_RETRIES = 10;
    
    // Native method to notify C++ that Java initialization is complete
    private static native void setInitialized();
    
    public static void init() {
        if (initAttempted) {
            System.out.println("[CRUCIFIX] init() already called, skipping");
            return;
        }
        
        initAttempted = true;
        System.out.println("[CRUCIFIX] init() called from JVMTI");
        
        // Start initialization in background
        new Thread(() -> initializeWithRetry()).start();
    }
    
    private static void initializeWithRetry() {
        while (retryCount < MAX_RETRIES && !initialized) {
            retryCount++;
            System.out.println("[CRUCIFIX] Initialization attempt " + retryCount + "/" + MAX_RETRIES);
            
            try {
                // Step 1: Initialize Lunar Bridge
                System.out.println("[CRUCIFIX] Establishing Lunar bridge...");
                boolean bridgeSuccess = LunarBridge.initialize();
                
                if (bridgeSuccess) {
                    Object mc = LunarBridge.getMinecraft();
                    Class<?> mcClass = LunarBridge.getMinecraftClass();
                    
                    System.out.println("[CRUCIFIX] Bridge successful!");
                    System.out.println("[CRUCIFIX] Minecraft instance: " + mc);
                    System.out.println("[CRUCIFIX] Minecraft class: " + mcClass);
                    
                    if (mc != null) {
                        // Step 2: Initialize modules with Minecraft instance
                        ModuleManager.initModules(mc);
                        System.out.println("[CRUCIFIX] Modules initialized!");
                        
                        // Step 3: Initialize ClickGUI
                        ClickGUI.getInstance();
                        System.out.println("[CRUCIFIX] ClickGUI initialized!");
                        
                        // Step 4: Initialize HUD
                        HUDManager.getInstance();
                        System.out.println("[CRUCIFIX] HUD initialized!");
                        
                        initialized = true;
                        System.out.println("[CRUCIFIX] INITIALIZATION COMPLETE!");
                        break;
                    } else {
                        System.out.println("[CRUCIFIX] Minecraft instance is null!");
                    }
                } else {
                    System.out.println("[CRUCIFIX] Lunar bridge failed!");
                }
                
                // Wait before retry
                System.out.println("[CRUCIFIX] Retrying in 2 seconds...");
                Thread.sleep(2000);
                
            } catch (Exception e) {
                System.out.println("[CRUCIFIX] Init error: " + e.getMessage());
                e.printStackTrace();
                
                if (retryCount < MAX_RETRIES) {
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException ignored) {}
                }
            }
        }
        
                if (!initialized) {
                    System.out.println("[CRUCIFIX] FAILED TO INITIALIZE AFTER " + MAX_RETRIES + " ATTEMPTS");
                } else {
                    System.out.println("[CRUCIFIX] Initialization complete!");
                    setInitialized();
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
        if (ClickGUI.getInstance() != null) {
            ClickGUI.getInstance().toggle();
            System.out.println("[CRUCIFIX] ClickGUI toggled");
        } else {
            System.out.println("[CRUCIFIX] ClickGUI not available");
        }
    }
    
    /**
     * Called from C++ render hook
     */
    public static void renderGUI() {
        if (initialized && ClickGUI.getInstance() != null) {
            ClickGUI.getInstance().render();
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


package com.crucifix.client.core;

import com.crucifix.client.modules.Category;
import com.crucifix.client.modules.Module;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Manages all modules and their state
 */
public class ModuleManager {
    private static ModuleManager instance;
    private final List<Module> modules;
    private final Map<Category, List<Module>> modulesByCategory;
    private Object minecraftInstance;
    private boolean initialized = false;
    
    private ModuleManager() {
        this.modules = new ArrayList<>();
        this.modulesByCategory = new EnumMap<>(Category.class);
        
        for (Category category : Category.values()) {
            modulesByCategory.put(category, new ArrayList<>());
        }
    }
    
    public static ModuleManager getInstance() {
        if (instance == null) {
            instance = new ModuleManager();
        }
        return instance;
    }
    
    public static void initModules(Object mc) {
        ModuleManager manager = getInstance();
        if (manager.initialized) {
            System.out.println("[ModuleManager] Already initialized");
            return;
        }
        
        manager.minecraftInstance = mc;
        System.out.println("[ModuleManager] Initializing modules with Minecraft: " + mc);
        
        // Register all modules
        manager.registerAllModules();
        manager.initialized = true;
        
        System.out.println("[ModuleManager] Initialized " + manager.modules.size() + " modules");
    }
    
    /**
     * Register all modules
     */
    private void registerAllModules() {
        // Combat modules
        registerModule(new com.crucifix.client.modules.combat.KillAura("KillAura", Category.COMBAT, minecraftInstance));
        registerModule(new com.crucifix.client.modules.combat.AimAssist("AimAssist", Category.COMBAT, minecraftInstance));
        registerModule(new com.crucifix.client.modules.combat.Velocity("Velocity", Category.COMBAT, minecraftInstance));
        registerModule(new com.crucifix.client.modules.combat.Reach("Reach", Category.COMBAT, minecraftInstance));
        registerModule(new com.crucifix.client.modules.combat.HitBoxes("HitBoxes", Category.COMBAT, minecraftInstance));
        registerModule(new com.crucifix.client.modules.combat.AutoClicker("AutoClicker", Category.COMBAT, minecraftInstance));
        registerModule(new com.crucifix.client.modules.combat.WTap("WTap", Category.COMBAT, minecraftInstance));
        registerModule(new com.crucifix.client.modules.combat.RodAura("RodAura", Category.COMBAT, minecraftInstance));
        registerModule(new com.crucifix.client.modules.combat.BowAim("BowAim", Category.COMBAT, minecraftInstance));
        
        // Movement modules
        registerModule(new com.crucifix.client.modules.movement.Speed("Speed", Category.MOVEMENT, minecraftInstance));
        registerModule(new com.crucifix.client.modules.movement.Fly("Fly", Category.MOVEMENT, minecraftInstance));
        registerModule(new com.crucifix.client.modules.movement.BHop("BHop", Category.MOVEMENT, minecraftInstance));
        registerModule(new com.crucifix.client.modules.movement.Strafe("Strafe", Category.MOVEMENT, minecraftInstance));
        registerModule(new com.crucifix.client.modules.movement.Step("Step", Category.MOVEMENT, minecraftInstance));
        registerModule(new com.crucifix.client.modules.movement.LongJump("LongJump", Category.MOVEMENT, minecraftInstance));
        registerModule(new com.crucifix.client.modules.movement.NoSlow("NoSlow", Category.MOVEMENT, minecraftInstance));
        registerModule(new com.crucifix.client.modules.movement.Sprint("Sprint", Category.MOVEMENT, minecraftInstance));
        registerModule(new com.crucifix.client.modules.movement.WaterWalk("WaterWalk", Category.MOVEMENT, minecraftInstance));
        registerModule(new com.crucifix.client.modules.movement.FastLadder("FastLadder", Category.MOVEMENT, minecraftInstance));
        
        // Render modules
        registerModule(new com.crucifix.client.modules.render.ESP("ESP", Category.RENDER, minecraftInstance));
        registerModule(new com.crucifix.client.modules.render.Chams("Chams", Category.RENDER, minecraftInstance));
        registerModule(new com.crucifix.client.modules.render.Nametags("Nametags", Category.RENDER, minecraftInstance));
        registerModule(new com.crucifix.client.modules.render.FullBright("FullBright", Category.RENDER, minecraftInstance));
        registerModule(new com.crucifix.client.modules.render.XRay("XRay", Category.RENDER, minecraftInstance));
        registerModule(new com.crucifix.client.modules.render.Tracers("Tracers", Category.RENDER, minecraftInstance));
        registerModule(new com.crucifix.client.modules.render.Glow("Glow", Category.RENDER, minecraftInstance));
        registerModule(new com.crucifix.client.modules.render.NoHurtCam("NoHurtCam", Category.RENDER, minecraftInstance));
        registerModule(new com.crucifix.client.modules.render.CameraClip("CameraClip", Category.RENDER, minecraftInstance));
        
        // Player modules
        registerModule(new com.crucifix.client.modules.player.AutoEat("AutoEat", Category.PLAYER, minecraftInstance));
        registerModule(new com.crucifix.client.modules.player.AutoSoup("AutoSoup", Category.PLAYER, minecraftInstance));
        registerModule(new com.crucifix.client.modules.player.AutoPearl("AutoPearl", Category.PLAYER, minecraftInstance));
        registerModule(new com.crucifix.client.modules.player.FastPlace("FastPlace", Category.PLAYER, minecraftInstance));
        registerModule(new com.crucifix.client.modules.player.NoFall("NoFall", Category.PLAYER, minecraftInstance));
        registerModule(new com.crucifix.client.modules.player.AntiFire("AntiFire", Category.PLAYER, minecraftInstance));
        registerModule(new com.crucifix.client.modules.player.AutoRespawn("AutoRespawn", Category.PLAYER, minecraftInstance));
        registerModule(new com.crucifix.client.modules.player.AutoGapple("AutoGapple", Category.PLAYER, minecraftInstance));
        
        // Misc modules
        registerModule(new com.crucifix.client.modules.misc.AntiBot("AntiBot", Category.MISC, minecraftInstance));
        registerModule(new com.crucifix.client.modules.misc.AutoGG("AutoGG", Category.MISC, minecraftInstance));
        registerModule(new com.crucifix.client.modules.misc.AutoTip("AutoTip", Category.MISC, minecraftInstance));
        registerModule(new com.crucifix.client.modules.misc.ChatFilter("ChatFilter", Category.MISC, minecraftInstance));
        registerModule(new com.crucifix.client.modules.misc.MiddleClickPearl("MiddleClickPearl", Category.MISC, minecraftInstance));
        registerModule(new com.crucifix.client.modules.misc.TimeChanger("TimeChanger", Category.MISC, minecraftInstance));
        registerModule(new com.crucifix.client.modules.misc.ScoreboardCleaner("ScoreboardCleaner", Category.MISC, minecraftInstance));
        
        // Exploit modules
        registerModule(new com.crucifix.client.modules.exploit.AntiVelocity("AntiVelocity", Category.EXPLOIT, minecraftInstance));
        registerModule(new com.crucifix.client.modules.exploit.NoRotate("NoRotate", Category.EXPLOIT, minecraftInstance));
        registerModule(new com.crucifix.client.modules.exploit.Disabler("Disabler", Category.EXPLOIT, minecraftInstance));
        registerModule(new com.crucifix.client.modules.exploit.Timer("Timer", Category.EXPLOIT, minecraftInstance));
        registerModule(new com.crucifix.client.modules.exploit.Phase("Phase", Category.EXPLOIT, minecraftInstance));
        registerModule(new com.crucifix.client.modules.exploit.FastUse("FastUse", Category.EXPLOIT, minecraftInstance));
        registerModule(new com.crucifix.client.modules.exploit.Scaffold("Scaffold", Category.EXPLOIT, minecraftInstance));
        registerModule(new com.crucifix.client.modules.exploit.Tower("Tower", Category.EXPLOIT, minecraftInstance));
        
        System.out.println("[ModuleManager] Initialized " + modules.size() + " modules");
    }
    
    /**
     * Register a module
     */
    private void registerModule(Module module) {
        modules.add(module);
        modulesByCategory.get(module.getCategory()).add(module);
    }
    
    /**
     * Get all modules
     */
    public List<Module> getModules() {
        return new ArrayList<>(modules);
    }
    
    /**
     * Get modules by category
     */
    public List<Module> getModulesByCategory(Category category) {
        return new ArrayList<>(modulesByCategory.get(category));
    }
    
    /**
     * Get a module by name
     */
    public Module getModule(String name) {
        for (Module module : modules) {
            if (module.getName().equalsIgnoreCase(name)) {
                return module;
            }
        }
        return null;
    }
    
    /**
     * Get all enabled modules
     */
    public List<Module> getEnabledModules() {
        return modules.stream()
                .filter(Module::isEnabled)
                .collect(Collectors.toList());
    }
}


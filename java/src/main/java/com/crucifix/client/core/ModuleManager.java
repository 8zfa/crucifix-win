package com.crucifix.client.core;

import com.crucifix.client.modules.Category;
import com.crucifix.client.modules.Module;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Manages all modules and their state
 */
public class ModuleManager {
    private final List<Module> modules;
    private final Map<Category, List<Module>> modulesByCategory;
    
    public ModuleManager() {
        this.modules = new ArrayList<>();
        this.modulesByCategory = new EnumMap<>(Category.class);
        
        for (Category category : Category.values()) {
            modulesByCategory.put(category, new ArrayList<>());
        }
    }
    
    /**
     * Initialize all modules
     */
    public void initializeModules() {
        // Combat modules
        registerModule(new com.crucifix.client.modules.combat.KillAura());
        registerModule(new com.crucifix.client.modules.combat.AimAssist());
        registerModule(new com.crucifix.client.modules.combat.Velocity());
        registerModule(new com.crucifix.client.modules.combat.Reach());
        registerModule(new com.crucifix.client.modules.combat.HitBoxes());
        registerModule(new com.crucifix.client.modules.combat.AutoClicker());
        registerModule(new com.crucifix.client.modules.combat.WTap());
        registerModule(new com.crucifix.client.modules.combat.RodAura());
        registerModule(new com.crucifix.client.modules.combat.BowAim());
        
        // Movement modules
        registerModule(new com.crucifix.client.modules.movement.Speed());
        registerModule(new com.crucifix.client.modules.movement.Fly());
        registerModule(new com.crucifix.client.modules.movement.BHop());
        registerModule(new com.crucifix.client.modules.movement.Strafe());
        registerModule(new com.crucifix.client.modules.movement.Step());
        registerModule(new com.crucifix.client.modules.movement.LongJump());
        registerModule(new com.crucifix.client.modules.movement.NoSlow());
        registerModule(new com.crucifix.client.modules.movement.Sprint());
        registerModule(new com.crucifix.client.modules.movement.WaterWalk());
        registerModule(new com.crucifix.client.modules.movement.FastLadder());
        
        // Render modules
        registerModule(new com.crucifix.client.modules.render.ESP());
        registerModule(new com.crucifix.client.modules.render.Chams());
        registerModule(new com.crucifix.client.modules.render.Nametags());
        registerModule(new com.crucifix.client.modules.render.FullBright());
        registerModule(new com.crucifix.client.modules.render.XRay());
        registerModule(new com.crucifix.client.modules.render.Tracers());
        registerModule(new com.crucifix.client.modules.render.Glow());
        registerModule(new com.crucifix.client.modules.render.NoHurtCam());
        registerModule(new com.crucifix.client.modules.render.CameraClip());
        
        // Player modules
        registerModule(new com.crucifix.client.modules.player.AutoEat());
        registerModule(new com.crucifix.client.modules.player.AutoSoup());
        registerModule(new com.crucifix.client.modules.player.AutoPearl());
        registerModule(new com.crucifix.client.modules.player.FastPlace());
        registerModule(new com.crucifix.client.modules.player.NoFall());
        registerModule(new com.crucifix.client.modules.player.AntiFire());
        registerModule(new com.crucifix.client.modules.player.AutoRespawn());
        registerModule(new com.crucifix.client.modules.player.AutoGapple());
        
        // Misc modules
        registerModule(new com.crucifix.client.modules.misc.AntiBot());
        registerModule(new com.crucifix.client.modules.misc.AutoGG());
        registerModule(new com.crucifix.client.modules.misc.AutoTip());
        registerModule(new com.crucifix.client.modules.misc.ChatFilter());
        registerModule(new com.crucifix.client.modules.misc.MiddleClickPearl());
        registerModule(new com.crucifix.client.modules.misc.TimeChanger());
        registerModule(new com.crucifix.client.modules.misc.ScoreboardCleaner());
        
        // Exploit modules
        registerModule(new com.crucifix.client.modules.exploit.AntiVelocity());
        registerModule(new com.crucifix.client.modules.exploit.NoRotate());
        registerModule(new com.crucifix.client.modules.exploit.Disabler());
        registerModule(new com.crucifix.client.modules.exploit.Timer());
        registerModule(new com.crucifix.client.modules.exploit.Phase());
        registerModule(new com.crucifix.client.modules.exploit.FastUse());
        registerModule(new com.crucifix.client.modules.exploit.Scaffold());
        registerModule(new com.crucifix.client.modules.exploit.Tower());
        
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


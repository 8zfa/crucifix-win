#pragma once

#include "Module.h"
#include <vector>
#include <memory>
#include <map>

class ModuleManager {
public:
    static ModuleManager& getInstance() {
        static ModuleManager instance;
        return instance;
    }
    
    void init();
    void update();
    void render();
    
    void addModule(std::unique_ptr<Module> module);
    Module* getModule(const std::string& name);
    std::vector<Module*> getModules();
    std::vector<Module*> getModulesByCategory(const std::string& category);
    std::vector<std::string> getCategories();
    
private:
    ModuleManager() = default;
    std::vector<std::unique_ptr<Module>> m_modules;
};

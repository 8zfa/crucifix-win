#include "ModuleManager.h"
#include "movement/Sprint.h"
#include "movement/Speed.h"
#include "movement/Fly.h"
#include "combat/KillAura.h"
#include "render/FullBright.h"
#include <iostream>

void ModuleManager::init() {
    std::cout << "[ModuleManager] Initializing modules..." << std::endl;
    
    // Add modules
    addModule(std::make_unique<Sprint>());
    addModule(std::make_unique<Speed>());
    addModule(std::make_unique<Fly>());
    addModule(std::make_unique<KillAura>());
    addModule(std::make_unique<FullBright>());
    
    std::cout << "[ModuleManager] Initialized " << m_modules.size() << " modules" << std::endl;
}

void ModuleManager::update() {
    for (auto& module : m_modules) {
        if (module->isEnabled()) {
            module->onUpdate();
        }
    }
}

void ModuleManager::render() {
    for (auto& module : m_modules) {
        if (module->isEnabled()) {
            module->onRender();
        }
    }
}

void ModuleManager::addModule(std::unique_ptr<Module> module) {
    m_modules.push_back(std::move(module));
}

Module* ModuleManager::getModule(const std::string& name) {
    for (auto& module : m_modules) {
        if (module->getName() == name) {
            return module.get();
        }
    }
    return nullptr;
}

std::vector<Module*> ModuleManager::getModules() {
    std::vector<Module*> modules;
    for (auto& module : m_modules) {
        modules.push_back(module.get());
    }
    return modules;
}

std::vector<Module*> ModuleManager::getModulesByCategory(const std::string& category) {
    std::vector<Module*> modules;
    for (auto& module : m_modules) {
        if (module->getCategory() == category) {
            modules.push_back(module.get());
        }
    }
    return modules;
}

std::vector<std::string> ModuleManager::getCategories() {
    std::vector<std::string> categories;
    for (auto& module : m_modules) {
        bool found = false;
        for (const auto& cat : categories) {
            if (cat == module->getCategory()) {
                found = true;
                break;
            }
        }
        if (!found) {
            categories.push_back(module->getCategory());
        }
    }
    return categories;
}

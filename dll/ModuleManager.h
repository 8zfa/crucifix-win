#pragma once
#include "Module.h"
#include <vector>
#include <memory>

class ModuleManager
{
public:
    static ModuleManager& GetInstance()
    {
        static ModuleManager instance;
        return instance;
    }

    void RegisterModule(std::shared_ptr<Module> module)
    {
        m_modules.push_back(module);
    }

    void EnableModule(const std::string& name)
    {
        for (auto& module : m_modules)
        {
            if (module->GetName() == name)
            {
                module->SetEnabled(true);
                return;
            }
        }
    }

    void DisableModule(const std::string& name)
    {
        for (auto& module : m_modules)
        {
            if (module->GetName() == name)
            {
                module->SetEnabled(false);
                return;
            }
        }
    }

    void UpdateAll()
    {
        for (auto& module : m_modules)
        {
            if (module->IsEnabled())
            {
                module->OnUpdate();
            }
        }
    }

private:
    ModuleManager() = default;
    std::vector<std::shared_ptr<Module>> m_modules;
};

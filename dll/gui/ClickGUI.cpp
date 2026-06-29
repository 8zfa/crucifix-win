#include "ClickGUI.h"
#include <iostream>

void ClickGUI::render() {
    if (!m_open) {
        return;
    }
    
    // Main window
    ImGui::SetNextWindowSize(ImVec2(500, 400), ImGuiCond_FirstUseEver);
    ImGui::SetNextWindowPos(ImVec2(100, 100), ImGuiCond_FirstUseEver);
    
    if (ImGui::Begin("Crucifix", &m_open, ImGuiWindowFlags_NoCollapse))
    {
        ImGui::Text("Press INSERT to close");
        ImGui::Separator();
        
        // Render categories as dropdowns
        auto categories = ModuleManager::getInstance().getCategories();
        
        for (const auto& category : categories)
        {
            if (ImGui::CollapsingHeader(category.c_str()))
            {
                renderCategory(category);
            }
        }
    }
    
    ImGui::End();
}

void ClickGUI::renderCategory(const std::string& category) {
    auto modules = ModuleManager::getInstance().getModulesByCategory(category);
    
    for (auto* module : modules)
    {
        bool enabled = module->isEnabled();
        if (ImGui::Checkbox(module->getName().c_str(), &enabled))
        {
            if (enabled != module->isEnabled())
            {
                module->toggle();
                std::cout << "[ClickGUI] Module " << module->getName() << " " << (module->isEnabled() ? "enabled" : "disabled") << std::endl;
            }
        }
    }
}

void ClickGUI::toggle() {
    m_open = !m_open;
    std::cout << "[ClickGUI] " << (m_open ? "Opened" : "Closed") << std::endl;
}

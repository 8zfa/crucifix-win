#pragma once

#include <windows.h>
#include "../modules/ModuleManager.h"
#include <imgui.h>
#include <imgui_impl_win32.h>
#include <imgui_impl_opengl2.h>
#include <string>

class ClickGUI {
public:
    static ClickGUI& getInstance() {
        static ClickGUI instance;
        return instance;
    }
    
    void render();
    void toggle();
    bool isOpen() const { return m_open; }
    
private:
    ClickGUI() = default;
    void renderCategory(const std::string& category);
    
    bool m_open = false;
};

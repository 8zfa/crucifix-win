#pragma once

#include "../Module.h"

class Sprint : public Module {
public:
    Sprint() : Module("Sprint", "Movement", 0) {} // No keybind
    
    void onUpdate() override {
        // TODO: Hook Minecraft sprint
        std::cout << "[Sprint] Sprinting" << std::endl;
    }
};

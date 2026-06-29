#pragma once

#include "../Module.h"

class Speed : public Module {
public:
    Speed() : Module("Speed", "Movement", 0) {}
    
    void onUpdate() override {
        // TODO: Hook Minecraft speed
        std::cout << "[Speed] Speeding" << std::endl;
    }
};

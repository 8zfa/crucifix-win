#pragma once

#include "../Module.h"

class Fly : public Module {
public:
    Fly() : Module("Fly", "Movement", 0) {}
    
    void onUpdate() override {
        // TODO: Hook Minecraft fly
        std::cout << "[Fly] Flying" << std::endl;
    }
};

#pragma once

#include "../Module.h"

class KillAura : public Module {
public:
    KillAura() : Module("KillAura", "Combat", 0) {}
    
    void onUpdate() override {
        // TODO: Hook Minecraft killaura
        std::cout << "[KillAura] Attacking" << std::endl;
    }
};

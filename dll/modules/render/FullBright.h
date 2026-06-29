#pragma once

#include "../Module.h"

class FullBright : public Module {
public:
    FullBright() : Module("FullBright", "Render", 0) {}
    
    void onUpdate() override {
        // TODO: Hook Minecraft fullbright
        std::cout << "[FullBright] Fullbright enabled" << std::endl;
    }
};

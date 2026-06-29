#pragma once
#include <windows.h>
#include <functional>

typedef void (*HookFunction)();

void InitializeHooks();
void CleanupHooks();
void HookMinecraftTick(HookFunction hook);
void HookPacketSend(HookFunction hook);
void HookRenderWorld(HookFunction hook);

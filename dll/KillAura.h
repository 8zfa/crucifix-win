#pragma once
#include "Module.h"
#include <jni.h>

class KillAura : public Module
{
public:
    KillAura();
    ~KillAura() override = default;

    void OnEnable() override;
    void OnDisable() override;
    void OnUpdate() override;

private:
    JNIEnv* m_env;
    jclass m_minecraftClass;
    jclass m_entityClass;
    jobject m_minecraftInstance;
};

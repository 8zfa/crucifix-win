#pragma once

#include <jni.h>
#include <Windows.h>
#include "hotspot/hotspot.h"

namespace JavaHook
{
    typedef void (*i2i_detour_t)(HotSpot::frame* frame, HotSpot::Thread* thread, bool* cancel);
    
    bool Hook(jmethodID methodID, i2i_detour_t detour);
    void Shutdown();
    
    class JNIFrame
    {
    public:
        JNIFrame(JNIEnv* env, int ref_count);
        ~JNIFrame();
        void Pop();
        operator bool();
    private:
        JNIEnv* m_env;
        bool m_isSuccess;
    };
    
    class Midi2iHook
    {
    public:
        Midi2iHook(uint8_t* target, i2i_detour_t detour);
        ~Midi2iHook();
    private:
        bool m_isError;
        uint8_t* m_target;
        uint8_t* m_allocatedAssembly;
    };
}

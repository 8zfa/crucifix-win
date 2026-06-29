#pragma once
#include <jni.h>
#include <string>
#include <vector>

bool InitializeJNI();
void CleanupJNI();
bool LoadJavaPayload(const std::string& jarPath);
jclass LoadClassFromJARBytes(const std::string& jarPath, const std::string& className);
jclass DefineClassFromBytes(const std::vector<unsigned char>& classBytes, const std::string& className);
JNIEnv* GetJNIEnv();
JavaVM* GetJavaVM();

// Event firing functions
void FireKeyEvent(int keyCode, bool pressed);
void FireRenderEvent(float partialTicks);
void FireUpdateEvent();
void FirePacketEvent(void* packet, bool cancelled);

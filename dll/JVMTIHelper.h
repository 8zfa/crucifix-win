#pragma once
#include <jni.h>
#include <jvmti.h>
#include <string>
#include <vector>

extern JavaVM* g_jvm;
extern JNIEnv* g_env;

// JVMTI environment pointer
extern jvmtiEnv* g_jvmti;

// Class bytes for injection
extern std::vector<unsigned char> g_crucifixClassBytes;

// Initialize JVMTI from existing JVM
bool InitializeJVMTI();

// Load a class via JVMTI by finding it in loaded classes
jclass FindClassViaJVMTI(const std::string& className);

// Redefine a class with new bytecode (bypass classloader)
bool RedefineClass(jclass targetClass, const std::vector<unsigned char>& newBytes);

// Read class bytes from JAR file
std::vector<unsigned char> ReadClassBytesFromJAR(const std::string& jarPath, const std::string& className);

// Add JAR to system classloader search path
bool AddToSystemClassLoaderSearch(const std::string& jarPath);

// Load JAR via JVMTI with ClassPrepare hook
bool LoadJARViaJVMTI(const std::string& jarPath);

// Set up ClassFileLoadHook to inject class bytes
bool SetupClassFileLoadHook(const std::string& className, const std::vector<unsigned char>& classBytes);

// Cleanup JVMTI
void CleanupJVMTI();

// Log helper for JVMTI operations
void LogJVMTI(const std::string& message);

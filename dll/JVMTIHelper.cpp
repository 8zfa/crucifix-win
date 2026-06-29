#include "JVMTIHelper.h"
#include "JNIHelper.h"
#include <iostream>
#include <fstream>
#include <algorithm>
#include <cstring>

jvmtiEnv* g_jvmti = nullptr;
std::vector<unsigned char> g_crucifixClassBytes;
std::string g_targetClassName = "com/crucifix/client/Crucifix";

void LogJVMTI(const std::string& message)
{
    std::ofstream logFile("C:\\Users\\raw\\Desktop\\crucifix_dll.log", std::ios::app);
    if (logFile.is_open())
    {
        logFile << message << std::endl;
        logFile.close();
    }
    std::cout << message << std::endl;
}

bool InitializeJVMTI()
{
    if (!g_jvm)
    {
        LogJVMTI("[JVMTI] JVM not initialized, cannot initialize JVMTI");
        return false;
    }
    
    LogJVMTI("[JVMTI] Attempting to get JVMTI environment...");
    
    // Get JVMTI environment from existing JVM (RiptermsGhost approach)
    jint result = g_jvm->GetEnv((void**)&g_jvmti, JVMTI_VERSION_1_2);
    
    if (result != JNI_OK)
    {
        LogJVMTI("[JVMTI] Failed to get JVMTI environment, error: " + std::to_string(result));
        return false;
    }
    
    if (!g_jvmti)
    {
        LogJVMTI("[JVMTI] JVMTI environment is null after GetEnv");
        return false;
    }
    
    LogJVMTI("[JVMTI] JVMTI environment initialized successfully");
    
    // Add capabilities for class retransformation
    jvmtiCapabilities capabilities = {};
    capabilities.can_retransform_classes = JVMTI_ENABLE;
    capabilities.can_generate_all_class_hook_events = JVMTI_ENABLE;
    
    jvmtiError err = g_jvmti->AddCapabilities(&capabilities);
    if (err != JVMTI_ERROR_NONE)
    {
        LogJVMTI("[JVMTI] Failed to add JVMTI capabilities, error: " + std::to_string(err));
        return false;
    }
    
    LogJVMTI("[JVMTI] JVMTI capabilities added successfully");
    return true;
}

jclass FindClassViaJVMTI(const std::string& className)
{
    if (!g_jvmti || !g_env)
    {
        LogJVMTI("[JVMTI] JVMTI or JNIEnv not initialized");
        return nullptr;
    }
    
    LogJVMTI("[JVMTI] Searching for class: " + className);
    
    // Get all loaded classes (RiptermsGhost approach)
    jint classCount = 0;
    jclass* classes = nullptr;
    
    jvmtiError err = g_jvmti->GetLoadedClasses(&classCount, &classes);
    if (err != JVMTI_ERROR_NONE)
    {
        LogJVMTI("[JVMTI] Failed to get loaded classes, error: " + std::to_string(err));
        return nullptr;
    }
    
    LogJVMTI("[JVMTI] Found " + std::to_string(classCount) + " loaded classes");
    
    jclass foundClass = nullptr;
    
    for (int i = 0; i < classCount; ++i)
    {
        char* signature = nullptr;
        err = g_jvmti->GetClassSignature(classes[i], &signature, nullptr);
        
        if (err == JVMTI_ERROR_NONE && signature)
        {
            // Convert from Lcom/example/Class; to com/example/Class
            std::string sig = signature;
            if (sig.length() >= 2 && sig[0] == 'L' && sig[sig.length() - 1] == ';')
            {
                sig = sig.substr(1, sig.length() - 2);
                sig = sig; // Replace '/' with '.' if needed
            }
            
            // Replace '/' with '.' for comparison
            std::replace(sig.begin(), sig.end(), '/', '.');
            
            if (sig == className)
            {
                LogJVMTI("[JVMTI] Found class: " + className);
                foundClass = (jclass)g_env->NewLocalRef(classes[i]);
            }
            
            g_jvmti->Deallocate((unsigned char*)signature);
        }
        
        g_env->DeleteLocalRef(classes[i]);
    }
    
    g_jvmti->Deallocate((unsigned char*)classes);
    
    if (!foundClass)
    {
        LogJVMTI("[JVMTI] Class not found: " + className);
    }
    
    return foundClass;
}

bool RedefineClass(jclass targetClass, const std::vector<unsigned char>& newBytes)
{
    if (!g_jvmti || !g_env)
    {
        LogJVMTI("[JVMTI] JVMTI or JNIEnv not initialized");
        return false;
    }
    
    if (!targetClass)
    {
        LogJVMTI("[JVMTI] Target class is null");
        return false;
    }
    
    LogJVMTI("[JVMTI] Attempting to redefine class with " + std::to_string(newBytes.size()) + " bytes");
    
    // Prepare class definition for redefinition
    jvmtiClassDefinition classDef;
    classDef.klass = targetClass;
    classDef.class_byte_count = newBytes.size();
    classDef.class_bytes = newBytes.data();
    
    jvmtiError err = g_jvmti->RedefineClasses(1, &classDef);
    
    if (err != JVMTI_ERROR_NONE)
    {
        LogJVMTI("[JVMTI] Failed to redefine class, error: " + std::to_string(err));
        return false;
    }
    
    LogJVMTI("[JVMTI] Class redefined successfully");
    return true;
}

void CleanupJVMTI()
{
    if (g_jvmti)
    {
        g_jvmti->DisposeEnvironment();
        g_jvmti = nullptr;
        LogJVMTI("[JVMTI] JVMTI environment disposed");
    }
}

std::vector<unsigned char> ReadClassBytesFromJAR(const std::string& jarPath, const std::string& className)
{
    LogJVMTI("[JVMTI] Reading class bytes from JAR: " + jarPath + " for class: " + className);
    
    // Convert class name to path format (com.crucifix.client.Crucifix -> com/crucifix/client/Crucifix.class)
    std::string classPath = className;
    std::replace(classPath.begin(), classPath.end(), '.', '/');
    classPath += ".class";
    
    // Open JAR file
    std::ifstream jarFile(jarPath, std::ios::binary);
    if (!jarFile.is_open())
    {
        LogJVMTI("[JVMTI] Failed to open JAR file: " + jarPath);
        return {};
    }
    
    // Read entire JAR into memory
    jarFile.seekg(0, std::ios::end);
    size_t jarSize = jarFile.tellg();
    jarFile.seekg(0, std::ios::beg);
    
    std::vector<char> jarData(jarSize);
    jarFile.read(jarData.data(), jarSize);
    jarFile.close();
    
    LogJVMTI("[JVMTI] JAR size: " + std::to_string(jarSize) + " bytes");
    
    // Simple JAR parsing - look for class file name in the data
    // This is a simplified approach - real JAR parsing would use the central directory
    std::string searchPattern = classPath;
    
    // Search for the class entry in the JAR
    for (size_t i = 0; i < jarData.size() - searchPattern.size(); i++)
    {
        bool match = true;
        for (size_t j = 0; j < searchPattern.size(); j++)
        {
            if (jarData[i + j] != searchPattern[j])
            {
                match = false;
                break;
            }
        }
        
        if (match)
        {
            LogJVMTI("[JVMTI] Found class entry at offset: " + std::to_string(i));
            
            // This is a very simplified approach - in reality we'd need to parse the JAR structure
            // For now, return empty and we'll use a different approach
            LogJVMTI("[JVMTI] Note: Full JAR parsing not implemented, using alternative approach");
            break;
        }
    }
    
    // For now, return empty - we'll use the custom ClassLoader approach instead
    LogJVMTI("[JVMTI] JAR parsing not fully implemented, will use custom ClassLoader");
    return {};
}

// ClassFileLoadHook callback - intercepts class loading and injects our bytes
void JNICALL ClassFileLoadHook(
    jvmtiEnv* jvmti_env,
    JNIEnv* jni_env,
    jclass class_being_redefined,
    jobject loader,
    const char* name,
    jobject protection_domain,
    jint class_data_len,
    const unsigned char* class_data,
    jint* new_class_data_len,
    unsigned char** new_class_data)
{
    if (!name) return;
    
    std::string className = name;
    LogJVMTI("[JVMTI] ClassFileLoadHook called for: " + className);
    
    // Check if this is our target class
    if (className == g_targetClassName && !g_crucifixClassBytes.empty())
    {
        LogJVMTI("[JVMTI] Intercepting Crucifix class load, injecting custom bytes");
        
        // Allocate memory for our class bytes
        jvmtiError err = jvmti_env->Allocate(g_crucifixClassBytes.size(), new_class_data);
        if (err != JVMTI_ERROR_NONE)
        {
            LogJVMTI("[JVMTI] Failed to allocate memory for class bytes, error: " + std::to_string(err));
            return;
        }
        
        // Copy our class bytes
        memcpy(*new_class_data, g_crucifixClassBytes.data(), g_crucifixClassBytes.size());
        *new_class_data_len = g_crucifixClassBytes.size();
        
        LogJVMTI("[JVMTI] Successfully injected Crucifix class bytes (" + std::to_string(g_crucifixClassBytes.size()) + " bytes)");
    }
}

bool SetupClassFileLoadHook(const std::string& className, const std::vector<unsigned char>& classBytes)
{
    if (!g_jvmti)
    {
        LogJVMTI("[JVMTI] JVMTI not initialized");
        return false;
    }
    
    LogJVMTI("[JVMTI] Setting up ClassFileLoadHook for: " + className);
    
    // Store class bytes globally for the hook
    g_crucifixClassBytes = classBytes;
    g_targetClassName = className;
    
    // Convert class name to JVM format (com.example.Class -> com/example/Class)
    std::string jvmClassName = className;
    std::replace(jvmClassName.begin(), jvmClassName.end(), '.', '/');
    
    // Set up event callbacks
    jvmtiEventCallbacks callbacks = {};
    callbacks.ClassFileLoadHook = &ClassFileLoadHook;
    
    jvmtiError err = g_jvmti->SetEventCallbacks(&callbacks, sizeof(jvmtiEventCallbacks));
    if (err != JVMTI_ERROR_NONE)
    {
        LogJVMTI("[JVMTI] Failed to set event callbacks, error: " + std::to_string(err));
        return false;
    }
    
    // Enable ClassFileLoadHook event
    err = g_jvmti->SetEventNotificationMode(JVMTI_ENABLE, JVMTI_EVENT_CLASS_FILE_LOAD_HOOK, nullptr);
    if (err != JVMTI_ERROR_NONE)
    {
        LogJVMTI("[JVMTI] Failed to enable ClassFileLoadHook event, error: " + std::to_string(err));
        return false;
    }
    
    LogJVMTI("[JVMTI] ClassFileLoadHook enabled successfully");
    return true;
}

// ClassPrepare hook - triggered when class is prepared (ready for use)
void JNICALL ClassPrepareHook(
    jvmtiEnv* jvmti_env,
    JNIEnv* jni_env,
    jthread thread,
    jclass klass)
{
    char* class_name = nullptr;
    jvmtiError err = jvmti_env->GetClassSignature(klass, &class_name, nullptr);
    
    if (err == JVMTI_ERROR_NONE && class_name)
    {
        std::string name(class_name);
        LogJVMTI("[JVMTI] Class prepared: " + name);
        
        // Check if this is our Crucifix class
        if (name.find("com/crucifix/client/Crucifix") != std::string::npos)
        {
            LogJVMTI("[JVMTI] Crucifix class found in ClassPrepare! Initializing...");
            
            // Call Crucifix.init() via JNI
            jclass crucifixClass = jni_env->FindClass("com/crucifix/client/Crucifix");
            if (crucifixClass)
            {
                jmethodID initMethod = jni_env->GetStaticMethodID(crucifixClass, "init", "()V");
                if (initMethod)
                {
                    jni_env->CallStaticVoidMethod(crucifixClass, initMethod);
                    if (jni_env->ExceptionCheck())
                    {
                        LogJVMTI("[JVMTI] Exception in Crucifix.init()");
                        jni_env->ExceptionDescribe();
                        jni_env->ExceptionClear();
                    }
                    else
                    {
                        LogJVMTI("[JVMTI] Crucifix.init() called successfully!");
                    }
                }
                else
                {
                    LogJVMTI("[JVMTI] Failed to find init method");
                }
            }
            else
            {
                LogJVMTI("[JVMTI] Failed to find Crucifix class");
            }
        }
        
        jvmti_env->Deallocate((unsigned char*)class_name);
    }
}

bool LoadJARViaJVMTI(const std::string& jarPath)
{
    LogJVMTI("[JVMTI] Loading JAR via JVMTI: " + jarPath);
    
    if (!g_jvmti)
    {
        if (!InitializeJVMTI())
        {
            LogJVMTI("[JVMTI] Failed to initialize JVMTI");
            return false;
        }
    }
    
    // Add JAR to system classloader search
    jvmtiError err = g_jvmti->AddToSystemClassLoaderSearch(jarPath.c_str());
    if (err != JVMTI_ERROR_NONE)
    {
        LogJVMTI("[JVMTI] Failed to add JAR to search: " + std::to_string(err));
        return false;
    }
    
    LogJVMTI("[JVMTI] JAR added to system classloader search");
    
    // Attach thread to JVM
    JavaVM* vm = nullptr;
    jsize vmCount = 0;
    jint result = JNI_GetCreatedJavaVMs(&vm, 1, &vmCount);
    
    if (result != JNI_OK || !vm)
    {
        LogJVMTI("[JVMTI] Failed to get JavaVM");
        return false;
    }
    
    result = vm->AttachCurrentThread((void**)&g_env, nullptr);
    if (result != JNI_OK)
    {
        LogJVMTI("[JVMTI] Failed to attach thread to JVM");
        return false;
    }
    
    LogJVMTI("[JVMTI] Thread attached to JVM");
    
    // Set up ClassPrepare hook
    jvmtiEventCallbacks callbacks = {};
    callbacks.ClassPrepare = &ClassPrepareHook;
    
    err = g_jvmti->SetEventCallbacks(&callbacks, sizeof(callbacks));
    if (err != JVMTI_ERROR_NONE)
    {
        LogJVMTI("[JVMTI] Failed to set callbacks: " + std::to_string(err));
        return false;
    }
    
    // Enable ClassPrepare event
    err = g_jvmti->SetEventNotificationMode(JVMTI_ENABLE, JVMTI_EVENT_CLASS_PREPARE, nullptr);
    if (err != JVMTI_ERROR_NONE)
    {
        LogJVMTI("[JVMTI] Failed to enable ClassPrepare event: " + std::to_string(err));
        return false;
    }
    
    LogJVMTI("[JVMTI] ClassPrepare hook enabled");
    
    // Try to load the class to trigger the hooks
    jclass cls = g_env->FindClass("com/crucifix/client/Crucifix");
    if (cls)
    {
        LogJVMTI("[JVMTI] Class found directly!");
        // Call init
        jmethodID initMethod = g_env->GetStaticMethodID(cls, "init", "()V");
        if (initMethod)
        {
            g_env->CallStaticVoidMethod(cls, initMethod);
            if (g_env->ExceptionCheck())
            {
                LogJVMTI("[JVMTI] Exception in init()");
                g_env->ExceptionDescribe();
                g_env->ExceptionClear();
            }
            else
            {
                LogJVMTI("[JVMTI] Init called successfully!");
            }
            return true;
        }
    }
    else
    {
        LogJVMTI("[JVMTI] Class not found directly, waiting for ClassPrepare hook...");
        // The ClassPrepare hook will handle it when the class is loaded
        return true; // The hook will initialize it
    }
    
    return false;
}

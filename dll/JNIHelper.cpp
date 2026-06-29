#include "JNIHelper.h"
#include <windows.h>
#include <tlhelp32.h>
#include <iostream>
#include <fstream>
#include <vector>
#include <psapi.h>
#include <algorithm>
#include "LogWindow.h"
#include <imgui.h>

extern JavaVM* g_jvm;
extern JNIEnv* g_env;
extern bool g_JavaInitialized;

// Log to file helper
void LogJNI(const std::string& message)
{
    std::ofstream logFile("C:\\Users\\raw\\Desktop\\crucifix_dll.log", std::ios::app);
    if (logFile.is_open())
    {
        logFile << message << std::endl;
        logFile.close();
    }
    std::cout << message << std::endl;
}

// Function pointer type for JNI_GetCreatedJavaVMs
typedef jint (JNICALL *JNI_GetCreatedJavaVMsFunc)(JavaVM**, jsize, jsize*);

bool InitializeJNI()
{
    LogJNI("[JNIHelper] Starting JNI initialization...");
    
    // Get jvm.dll handle (proven working method from payload test)
    HMODULE jvmModule = GetModuleHandleA("jvm.dll");
    if (!jvmModule)
    {
        LogJNI("[JNIHelper] jvm.dll not loaded, trying to load it...");
        jvmModule = LoadLibraryA("jvm.dll");
        if (!jvmModule)
        {
            LogJNI("[JNIHelper] Failed to load jvm.dll");
            return false;
        }
    }
    
    LogJNI("[JNIHelper] jvm.dll found");
    
    // Get JNI_GetCreatedJavaVMs function
    JNI_GetCreatedJavaVMsFunc jniGetCreatedJavaVMs = (JNI_GetCreatedJavaVMsFunc)GetProcAddress(jvmModule, "JNI_GetCreatedJavaVMs");
    if (!jniGetCreatedJavaVMs)
    {
        LogJNI("[JNIHelper] Failed to get JNI_GetCreatedJavaVMs address");
        return false;
    }
    
    LogJNI("[JNIHelper] JNI_GetCreatedJavaVMs function found");
    
    // Call JNI_GetCreatedJavaVMs (proven working)
    jsize vmCount = 0;
    jint result = jniGetCreatedJavaVMs(&g_jvm, 1, &vmCount);
    
    if (result != JNI_OK)
    {
        LogJNI("[JNIHelper] JNI_GetCreatedJavaVMs returned error: " + std::to_string(result));
        return false;
    }
    
    if (vmCount == 0)
    {
        LogJNI("[JNIHelper] No JVM found (vmCount = 0)");
        return false;
    }
    
    if (!g_jvm)
    {
        LogJNI("[JNIHelper] JVM pointer is null");
        return false;
    }
    
    LogJNI("[JNIHelper] JVM found at: " + std::to_string(reinterpret_cast<uintptr_t>(g_jvm)));
    
    // Attach current thread (proven working)
    result = g_jvm->AttachCurrentThread((void**)&g_env, nullptr);
    if (result != JNI_OK)
    {
        LogJNI("[JNIHelper] Failed to attach current thread, error: " + std::to_string(result));
        return false;
    }
    
    if (!g_env)
    {
        LogJNI("[JNIHelper] JNIEnv is null after attach");
        return false;
    }
    
    LogJNI("[JNIHelper] Successfully attached to JVM");
    LogJNI("[JNIHelper] JNI initialization complete!");
    return true;
}

void CleanupJNI()
{
    if (g_jvm && g_env)
    {
        g_jvm->DetachCurrentThread();
        g_env = nullptr;
        g_jvm = nullptr;
    }
}

bool LoadJavaPayload(const std::string& jarPath)
{
    if (!g_env)
    {
        LogJNI("[JNIHelper] JNIEnv is null");
        return false;
    }
    
    LogJNI("[JNIHelper] Loading Java payload from: " + jarPath);
    
    // Read JAR file bytes
    std::ifstream jarFile(jarPath, std::ios::binary | std::ios::ate);
    if (!jarFile.is_open())
    {
        LogJNI("[JNIHelper] Failed to open JAR file: " + jarPath);
        return false;
    }
    
    std::streamsize jarSize = jarFile.tellg();
    jarFile.seekg(0, std::ios::beg);
    
    std::vector<char> jarBytes(jarSize);
    if (!jarFile.read(jarBytes.data(), jarSize))
    {
        LogJNI("[JNIHelper] Failed to read JAR file");
        return false;
    }
    jarFile.close();
    
    LogJNI("[JNIHelper] Read JAR file: " + std::to_string(jarSize) + " bytes");
    
    // Try to find LunarClassLoader and inject class directly
    LogJNI("[JNIHelper] Attempting to inject class via LunarClassLoader...");
    
    // Find LunarClassLoader class
    jclass lunarClassLoaderClass = g_env->FindClass("com/lunarclient/lunar/LunarClassLoader");
    if (!lunarClassLoaderClass)
    {
        LogJNI("[JNIHelper] LunarClassLoader not found, trying alternative...");
        // Try to find any classloader
        jclass threadClass = g_env->FindClass("java/lang/Thread");
        if (!threadClass)
        {
            LogJNI("[JNIHelper] Failed to find Thread class");
            return false;
        }
        
        jmethodID currentThreadMethod = g_env->GetStaticMethodID(threadClass, "currentThread", "()Ljava/lang/Thread;");
        jobject currentThread = g_env->CallStaticObjectMethod(threadClass, currentThreadMethod);
        
        jmethodID getContextLoader = g_env->GetMethodID(threadClass, "getContextClassLoader", "()Ljava/lang/ClassLoader;");
        jobject classLoader = g_env->CallObjectMethod(currentThread, getContextLoader);
        
        if (!classLoader)
        {
            LogJNI("[JNIHelper] Failed to get context classloader");
            return false;
        }
        
        LogJNI("[JNIHelper] Got context classloader");
        
        // Try to use addURL method to add JAR to classpath
        jclass urlClassLoaderClass = g_env->FindClass("java/net/URLClassLoader");
        if (urlClassLoaderClass)
        {
            jmethodID addURLMethod = g_env->GetMethodID(urlClassLoaderClass, "addURL", "(Ljava/net/URL;)V");
            if (addURLMethod)
            {
                // Create URL from JAR path
                jclass urlClass = g_env->FindClass("java/net/URL");
                jmethodID urlConstructor = g_env->GetMethodID(urlClass, "<init>", "(Ljava/lang/String;)V");
                
                std::string jarUrl = "file:///" + jarPath;
                jstring jarUrlStr = g_env->NewStringUTF(jarUrl.c_str());
                jobject urlObj = g_env->NewObject(urlClass, urlConstructor, jarUrlStr);
                
                g_env->CallVoidMethod(classLoader, addURLMethod, urlObj);
                
                LogJNI("[JNIHelper] Added JAR to classpath via addURL");
                
                // Now try to find the class
                jclass crucifixClass = g_env->FindClass("com/crucifix/client/Crucifix");
                if (crucifixClass)
                {
                    LogJNI("[JNIHelper] Found Crucifix class after adding to classpath!");
                    return true;
                }
                else
                {
                    LogJNI("[JNIHelper] Still cannot find Crucifix class after addURL");
                }
            }
            else
            {
                LogJNI("[JNIHelper] addURL method not found");
            }
        }
        
        return false;
    }
    
    LogJNI("[JNIHelper] Found LunarClassLoader");
    
    // TODO: Use LunarClassLoader specific methods to inject class
    // For now, return false to indicate we need a different approach
    return false;
}

jclass LoadClassFromJARBytes(const std::string& jarPath, const std::string& className)
{
    if (!g_env)
    {
        LogJNI("[JNIHelper] JNIEnv is null in LoadClassFromJARBytes");
        return nullptr;
    }
    
    LogJNI("[JNIHelper] Loading class from JAR bytes: " + className);
    
    // Read JAR file bytes
    std::ifstream jarFile(jarPath, std::ios::binary | std::ios::ate);
    if (!jarFile.is_open())
    {
        LogJNI("[JNIHelper] Failed to open JAR file: " + jarPath);
        return nullptr;
    }
    
    std::streamsize jarSize = jarFile.tellg();
    jarFile.seekg(0, std::ios::beg);
    
    std::vector<char> jarBytes(jarSize);
    if (!jarFile.read(jarBytes.data(), jarSize))
    {
        LogJNI("[JNIHelper] Failed to read JAR file");
        return nullptr;
    }
    jarFile.close();
    
    LogJNI("[JNIHelper] Read JAR file: " + std::to_string(jarSize) + " bytes");
    
    // Create a byte array from the JAR bytes
    jbyteArray jarByteArray = g_env->NewByteArray(jarSize);
    g_env->SetByteArrayRegion(jarByteArray, 0, jarSize, (const jbyte*)jarBytes.data());
    
    // Create a custom ClassLoader using URLClassLoader with a JAR URL
    jclass urlClassLoaderClass = g_env->FindClass("java/net/URLClassLoader");
    if (!urlClassLoaderClass)
    {
        LogJNI("[JNIHelper] Failed to find URLClassLoader class");
        return nullptr;
    }
    
    // Create URL for the JAR file
    jclass urlClass = g_env->FindClass("java/net/URL");
    if (!urlClass)
    {
        LogJNI("[JNIHelper] Failed to find URL class");
        return nullptr;
    }
    
    jmethodID urlConstructor = g_env->GetMethodID(urlClass, "<init>", "(Ljava/lang/String;)V");
    if (!urlConstructor)
    {
        LogJNI("[JNIHelper] Failed to get URL constructor");
        return nullptr;
    }
    
    std::string jarUrl = "file:///" + jarPath;
    jstring jarUrlStr = g_env->NewStringUTF(jarUrl.c_str());
    jobject urlObj = g_env->NewObject(urlClass, urlConstructor, jarUrlStr);
    
    // Create URL array
    jobjectArray urlArray = g_env->NewObjectArray(1, urlClass, urlObj);
    
    // Get URLClassLoader constructor
    jmethodID urlClassLoaderConstructor = g_env->GetMethodID(urlClassLoaderClass, "<init>", "([Ljava/net/URL;)V");
    if (!urlClassLoaderConstructor)
    {
        LogJNI("[JNIHelper] Failed to get URLClassLoader constructor");
        return nullptr;
    }
    
    // Create custom ClassLoader instance
    jobject customClassLoader = g_env->NewObject(urlClassLoaderClass, urlClassLoaderConstructor, urlArray);
    if (!customClassLoader)
    {
        LogJNI("[JNIHelper] Failed to create custom ClassLoader");
        return nullptr;
    }
    
    LogJNI("[JNIHelper] Created custom ClassLoader");
    
    // Load the class using the custom ClassLoader
    jstring classNameStr = g_env->NewStringUTF(className.c_str());
    jmethodID loadClassMethod = g_env->GetMethodID(urlClassLoaderClass, "loadClass", "(Ljava/lang/String;)Ljava/lang/Class;");
    
    jclass loadedClass = (jclass)g_env->CallObjectMethod(customClassLoader, loadClassMethod, classNameStr);
    
    if (loadedClass)
    {
        LogJNI("[JNIHelper] Successfully loaded class: " + className);
    }
    else
    {
        LogJNI("[JNIHelper] Failed to load class: " + className);
        if (g_env->ExceptionCheck())
        {
            g_env->ExceptionDescribe();
            g_env->ExceptionClear();
        }
    }
    
    // Cleanup
    g_env->DeleteLocalRef(jarByteArray);
    g_env->DeleteLocalRef(jarUrlStr);
    g_env->DeleteLocalRef(urlObj);
    g_env->DeleteLocalRef(urlArray);
    g_env->DeleteLocalRef(customClassLoader);
    g_env->DeleteLocalRef(classNameStr);
    
    return loadedClass;
}

jclass DefineClassFromBytes(const std::vector<unsigned char>& classBytes, const std::string& className)
{
    if (!g_env)
    {
        LogJNI("[JNIHelper] JNIEnv is null in DefineClassFromBytes");
        return nullptr;
    }
    
    LogJNI("[JNIHelper] Defining class from bytes: " + className);
    
    // Get system classloader as parent
    jclass classLoaderClass = g_env->FindClass("java/lang/ClassLoader");
    if (!classLoaderClass)
    {
        LogJNI("[JNIHelper] Failed to find ClassLoader class");
        return nullptr;
    }
    
    jmethodID getSystemClassLoader = g_env->GetStaticMethodID(classLoaderClass, "getSystemClassLoader", "()Ljava/lang/ClassLoader;");
    if (!getSystemClassLoader)
    {
        LogJNI("[JNIHelper] Failed to get getSystemClassLoader method");
        return nullptr;
    }
    
    jobject systemClassLoader = g_env->CallStaticObjectMethod(classLoaderClass, getSystemClassLoader);
    if (!systemClassLoader)
    {
        LogJNI("[JNIHelper] Failed to get system classloader");
        return nullptr;
    }
    
    // Convert class name to JVM format (com.example.Class -> com/example/Class)
    std::string jvmClassName = className;
    std::replace(jvmClassName.begin(), jvmClassName.end(), '.', '/');
    
    // Define the class directly from bytes
    jclass definedClass = g_env->DefineClass(jvmClassName.c_str(), systemClassLoader, (const jbyte*)classBytes.data(), classBytes.size());
    
    if (definedClass)
    {
        LogJNI("[JNIHelper] Successfully defined class from bytes: " + className);
    }
    else
    {
        LogJNI("[JNIHelper] Failed to define class from bytes: " + className);
        if (g_env->ExceptionCheck())
        {
            g_env->ExceptionDescribe();
            g_env->ExceptionClear();
        }
    }
    
    // Cleanup
    g_env->DeleteLocalRef(systemClassLoader);
    
    return definedClass;
}

JNIEnv* GetJNIEnv()
{
    return g_env;
}

JavaVM* GetJavaVM()
{
    return g_jvm;
}

// Event firing functions
void FireKeyEvent(int keyCode, bool pressed)
{
    if (!g_env) return;
    
    try {
        jclass crucifixClass = g_env->FindClass("com/crucifix/client/Crucifix");
        if (!crucifixClass) {
            std::cout << "[JNIHelper] Failed to find Crucifix class" << std::endl;
            return;
        }
        
        jmethodID fireKeyEvent = g_env->GetStaticMethodID(crucifixClass, "fireKeyEvent", "(IZ)V");
        if (!fireKeyEvent) {
            std::cout << "[JNIHelper] Failed to find fireKeyEvent method" << std::endl;
            return;
        }
        
        std::cout << "[JNIHelper] Firing KeyEvent: " << keyCode << " pressed: " << pressed << std::endl;
        g_env->CallStaticVoidMethod(crucifixClass, fireKeyEvent, keyCode, pressed);
        
        g_env->DeleteLocalRef(crucifixClass);
    } catch (...) {
        std::cout << "[JNIHelper] Exception in FireKeyEvent" << std::endl;
    }
}

void FireRenderEvent(float partialTicks)
{
    if (!g_env) return;
    
    try {
        jclass crucifixClass = g_env->FindClass("com/crucifix/client/Crucifix");
        if (!crucifixClass) return;
        
        jmethodID fireRenderEvent = g_env->GetStaticMethodID(crucifixClass, "fireRenderEvent", "(F)V");
        if (!fireRenderEvent) return;
        
        g_env->CallStaticVoidMethod(crucifixClass, fireRenderEvent, partialTicks);
        
        g_env->DeleteLocalRef(crucifixClass);
    } catch (...) {
        // Ignore
    }
}

void FireUpdateEvent()
{
    if (!g_env) return;
    
    try {
        jclass crucifixClass = g_env->FindClass("com/crucifix/client/Crucifix");
        if (!crucifixClass) return;
        
        jmethodID fireUpdateEvent = g_env->GetStaticMethodID(crucifixClass, "fireUpdateEvent", "()V");
        if (!fireUpdateEvent) return;
        
        g_env->CallStaticVoidMethod(crucifixClass, fireUpdateEvent);
        
        g_env->DeleteLocalRef(crucifixClass);
    } catch (...) {
        // Ignore
    }
}

void FirePacketEvent(void* packet, bool cancelled)
{
    if (!g_env) return;
    
    try {
        jclass crucifixClass = g_env->FindClass("com/crucifix/client/Crucifix");
        if (!crucifixClass) return;
        
        jmethodID firePacketEvent = g_env->GetStaticMethodID(crucifixClass, "firePacketEvent", "(JZ)V");
        if (!firePacketEvent) return;
        
        jlong packetPtr = (jlong)packet;
        g_env->CallStaticVoidMethod(crucifixClass, firePacketEvent, packetPtr, cancelled);
        
        g_env->DeleteLocalRef(crucifixClass);
    } catch (...) {
        // Ignore
    }
}

// Native method implementation for ClickGUI.isImGuiAvailable()
extern bool g_ImGuiAvailable;

JNIEXPORT jboolean JNICALL Java_com_crucifix_client_gui_ClickGUI_isImGuiAvailable(JNIEnv* env, jobject obj) {
    printf("[JNI] isImGuiAvailable() called - g_ImGuiAvailable = %d\n", g_ImGuiAvailable);
    LogDebug(("isImGuiAvailable() called - returning " + std::to_string(g_ImGuiAvailable)).c_str());
    return g_ImGuiAvailable ? JNI_TRUE : JNI_FALSE;
}

// ImGui rendering methods
JNIEXPORT void JNICALL Java_com_crucifix_client_gui_ClickGUI_nBegin(JNIEnv* env, jobject obj, jstring name, jint flags) {
    const char* nameStr = env->GetStringUTFChars(name, nullptr);
    ImGui::Begin(nameStr, nullptr, flags);
    env->ReleaseStringUTFChars(name, nameStr);
}

JNIEXPORT void JNICALL Java_com_crucifix_client_gui_ClickGUI_nEnd(JNIEnv* env, jobject obj) {
    ImGui::End();
}

JNIEXPORT void JNICALL Java_com_crucifix_client_gui_ClickGUI_nText(JNIEnv* env, jobject obj, jstring text) {
    const char* textStr = env->GetStringUTFChars(text, nullptr);
    ImGui::Text("%s", textStr);
    env->ReleaseStringUTFChars(text, textStr);
}

JNIEXPORT void JNICALL Java_com_crucifix_client_gui_ClickGUI_nSeparator(JNIEnv* env, jobject obj) {
    ImGui::Separator();
}

JNIEXPORT jboolean JNICALL Java_com_crucifix_client_gui_ClickGUI_nCollapsingHeader(JNIEnv* env, jobject obj, jstring label) {
    const char* labelStr = env->GetStringUTFChars(label, nullptr);
    bool result = ImGui::CollapsingHeader(labelStr);
    env->ReleaseStringUTFChars(label, labelStr);
    return result ? JNI_TRUE : JNI_FALSE;
}

JNIEXPORT jboolean JNICALL Java_com_crucifix_client_gui_ClickGUI_nCheckbox(JNIEnv* env, jobject obj, jstring label, jboolean value) {
    const char* labelStr = env->GetStringUTFChars(label, nullptr);
    bool val = (value == JNI_TRUE);
    bool result = ImGui::Checkbox(labelStr, &val);
    env->ReleaseStringUTFChars(label, labelStr);
    return result ? JNI_TRUE : JNI_FALSE;
}

JNIEXPORT jboolean JNICALL Java_com_crucifix_client_gui_ClickGUI_nButton(JNIEnv* env, jobject obj, jstring label) {
    const char* labelStr = env->GetStringUTFChars(label, nullptr);
    bool result = ImGui::Button(labelStr);
    env->ReleaseStringUTFChars(label, labelStr);
    return result ? JNI_TRUE : JNI_FALSE;
}

JNIEXPORT void JNICALL Java_com_crucifix_client_gui_ClickGUI_nSameLine(JNIEnv* env, jobject obj) {
    ImGui::SameLine();
}

JNIEXPORT void JNICALL Java_com_crucifix_client_gui_ClickGUI_nPushStyleColor(JNIEnv* env, jobject obj, jint idx, jfloat r, jfloat g, jfloat b, jfloat a) {
    ImGui::PushStyleColor(idx, ImVec4(r, g, b, a));
}

JNIEXPORT void JNICALL Java_com_crucifix_client_gui_ClickGUI_nPopStyleColor(JNIEnv* env, jobject obj) {
    ImGui::PopStyleColor();
}

JNIEXPORT void JNICALL Java_com_crucifix_client_gui_ClickGUI_nPushStyleVar(JNIEnv* env, jobject obj, jint idx, jfloat value) {
    ImGui::PushStyleVar(idx, value);
}

JNIEXPORT void JNICALL Java_com_crucifix_client_gui_ClickGUI_nPopStyleVar(JNIEnv* env, jobject obj) {
    ImGui::PopStyleVar();
}

JNIEXPORT void JNICALL Java_com_crucifix_client_gui_ClickGUI_nSetNextWindowSize(JNIEnv* env, jobject obj, jfloat w, jfloat h) {
    ImGui::SetNextWindowSize(ImVec2(w, h));
}

JNIEXPORT void JNICALL Java_com_crucifix_client_gui_ClickGUI_nSetNextWindowPos(JNIEnv* env, jobject obj, jfloat x, jfloat y) {
    ImGui::SetNextWindowPos(ImVec2(x, y));
}

JNIEXPORT void JNICALL Java_com_crucifix_client_gui_ClickGUI_nSetNextWindowBgAlpha(JNIEnv* env, jobject obj, jfloat alpha) {
    ImGui::SetNextWindowBgAlpha(alpha);
}

// Register all ClickGUI native methods
void RegisterClickGUINatives(JNIEnv* env) {
    jclass clickGUIClass = env->FindClass("com/crucifix/client/gui/ClickGUI");
    if (clickGUIClass == nullptr) {
        printf("[JNI] Could not find ClickGUI class!\n");
        LogError("Could not find ClickGUI class");
        return;
    }
    
    JNINativeMethod methods[] = {
        {"isImGuiAvailable", "()Z", (void*)Java_com_crucifix_client_gui_ClickGUI_isImGuiAvailable},
        {"nBegin", "(Ljava/lang/String;I)V", (void*)Java_com_crucifix_client_gui_ClickGUI_nBegin},
        {"nEnd", "()V", (void*)Java_com_crucifix_client_gui_ClickGUI_nEnd},
        {"nText", "(Ljava/lang/String;)V", (void*)Java_com_crucifix_client_gui_ClickGUI_nText},
        {"nSeparator", "()V", (void*)Java_com_crucifix_client_gui_ClickGUI_nSeparator},
        {"nCollapsingHeader", "(Ljava/lang/String;)Z", (void*)Java_com_crucifix_client_gui_ClickGUI_nCollapsingHeader},
        {"nCheckbox", "(Ljava/lang/String;Z)Z", (void*)Java_com_crucifix_client_gui_ClickGUI_nCheckbox},
        {"nButton", "(Ljava/lang/String;)Z", (void*)Java_com_crucifix_client_gui_ClickGUI_nButton},
        {"nSameLine", "()V", (void*)Java_com_crucifix_client_gui_ClickGUI_nSameLine},
        {"nPushStyleColor", "(IFFFF)V", (void*)Java_com_crucifix_client_gui_ClickGUI_nPushStyleColor},
        {"nPopStyleColor", "()V", (void*)Java_com_crucifix_client_gui_ClickGUI_nPopStyleColor},
        {"nPushStyleVar", "(IF)V", (void*)Java_com_crucifix_client_gui_ClickGUI_nPushStyleVar},
        {"nPopStyleVar", "()V", (void*)Java_com_crucifix_client_gui_ClickGUI_nPopStyleVar},
        {"nSetNextWindowSize", "(FF)V", (void*)Java_com_crucifix_client_gui_ClickGUI_nSetNextWindowSize},
        {"nSetNextWindowPos", "(FF)V", (void*)Java_com_crucifix_client_gui_ClickGUI_nSetNextWindowPos},
        {"nSetNextWindowBgAlpha", "(F)V", (void*)Java_com_crucifix_client_gui_ClickGUI_nSetNextWindowBgAlpha}
    };
    
    int result = env->RegisterNatives(clickGUIClass, methods, sizeof(methods) / sizeof(methods[0]));
    if (result != JNI_OK) {
        printf("[JNI] Failed to register ClickGUI natives! Error: %d\n", result);
        LogError("Failed to register ClickGUI native methods");
    } else {
        printf("[JNI] Registered %d ClickGUI native methods\n", sizeof(methods) / sizeof(methods[0]));
        LogInfo("Registered ClickGUI native methods");
    }
}

#include <windows.h>
#include <iostream>
#include <fstream>
#include <thread>
#include "JNIHelper.h"
#include "JVMTIHelper.h"
#include "HookManager.h"
#include "ResourceLoader.h"
#include "modules/ModuleManager.h"
#include "gui/ClickGUI.h"
#include "LogWindow.h"

// Forward declarations for wglSwapBuffers hook
void Hook_wglSwapBuffers();
void Unhook_wglSwapBuffers();

HMODULE g_hModule = nullptr;
JavaVM* g_jvm = nullptr;
JNIEnv* g_env = nullptr;
bool g_initialized = false;
bool g_JavaInitialized = false;
HHOOK g_keyboardHook = nullptr;
bool g_menuOpen = false;
HWND g_minecraftWindow = nullptr;
bool g_shouldExit = false;

// JNI method to set Java initialization flag
extern "C" JNIEXPORT void JNICALL Java_com_crucifix_client_Crucifix_setInitialized(JNIEnv* env, jclass cls) {
    g_JavaInitialized = true;
    LogInfo("Java initialization complete!");
}

// Keyboard hook callback
LRESULT CALLBACK KeyboardHook(int nCode, WPARAM wParam, LPARAM lParam)
{
    static int hookCallCount = 0;
    hookCallCount++;
    
    if (hookCallCount % 100 == 0)
    {
        std::cout << "[Keyboard] Hook called, count=" << hookCallCount << " nCode=" << nCode << std::endl;
    }
    
    if (nCode >= 0)
    {
        KBDLLHOOKSTRUCT* kbStruct = (KBDLLHOOKSTRUCT*)lParam;
        int keyCode = kbStruct->vkCode;
        bool pressed = (wParam == WM_KEYDOWN || wParam == WM_SYSKEYDOWN);
        
        // Log all key presses for debugging
        if (pressed)
        {
            std::cout << "[Keyboard] Key pressed: " << keyCode << std::endl;
        }
        
        // Toggle menu with RSHIFT only
        if (pressed && keyCode == VK_RSHIFT)
        {
            std::cout << "[CRUCIFIX] RSHIFT pressed, toggling ClickGUI" << std::endl;
            g_menuOpen = !g_menuOpen;
            std::cout << "[CRUCIFIX] g_menuOpen now: " << (g_menuOpen ? "true" : "false") << std::endl;
            
            // Call Java toggleClickGUI method
            if (g_env)
            {
                jclass crucifixClass = g_env->FindClass("com/crucifix/client/Crucifix");
                if (crucifixClass)
                {
                    jmethodID toggleMethod = g_env->GetStaticMethodID(crucifixClass, "toggleClickGUI", "()V");
                    if (toggleMethod)
                    {
                        g_env->CallStaticVoidMethod(crucifixClass, toggleMethod);
                        if (g_env->ExceptionCheck())
                        {
                            g_env->ExceptionDescribe();
                            g_env->ExceptionClear();
                        }
                    }
                }
            }
        }
    }
    
    return CallNextHookEx(g_keyboardHook, nCode, wParam, lParam);
}

// Background thread for event loop
void EventLoop()
{
    try
    {
        std::cout << "[CRUCIFIX] Event loop started" << std::endl;
        
        // Install wglSwapBuffers hook for ImGui rendering
        std::cout << "[CRUCIFIX] About to install wglSwapBuffers hook" << std::endl;
        Hook_wglSwapBuffers();
        std::cout << "[CRUCIFIX] wglSwapBuffers hook installation returned" << std::endl;
        
        // Initialize C++ modules
        std::cout << "[CRUCIFIX] About to initialize modules" << std::endl;
        ModuleManager::getInstance().init();
        std::cout << "[CRUCIFIX] Modules initialized" << std::endl;
        
        // Install keyboard hook
        std::cout << "[CRUCIFIX] About to install keyboard hook" << std::endl;
        g_keyboardHook = SetWindowsHookEx(WH_KEYBOARD_LL, KeyboardHook, g_hModule, 0);
        
        if (g_keyboardHook)
        {
            std::cout << "[CRUCIFIX] Keyboard hook installed" << std::endl;
        }
        else
        {
            std::cout << "[CRUCIFIX] Failed to install keyboard hook" << std::endl;
        }
        
        // Fire update events periodically (20 times per second)
        DWORD lastUpdateTime = GetTickCount();
        int loopCount = 0;
        
        std::cout << "[CRUCIFIX] Entering main loop" << std::endl;
        
        // Keep thread alive with sleep loop
        while (!g_shouldExit)
        {
            Sleep(1);
            loopCount++;
            
            // Log every 5000 iterations to show loop is running
            if (loopCount % 5000 == 0)
            {
                std::cout << "[CRUCIFIX] Loop running, count=" << loopCount << std::endl;
            }
            
            // Fire update events periodically
            DWORD currentTime = GetTickCount();
            if (currentTime - lastUpdateTime >= 50) // 50ms = 20 ticks per second
            {
                ModuleManager::getInstance().update();
                lastUpdateTime = currentTime;
            }
        }
        
        std::cout << "[CRUCIFIX] Event loop exiting, count=" << loopCount << std::endl;
    }
    catch (const std::exception& e)
    {
        std::cout << "[CRUCIFIX] Event loop exception: " << e.what() << std::endl;
    }
    catch (...)
    {
        std::cout << "[CRUCIFIX] Event loop unknown exception" << std::endl;
    }
}

// Log to file
void LogToFile(const std::string& message)
{
    std::ofstream logFile("C:\\Users\\raw\\Desktop\\crucifix_dll.log", std::ios::app);
    if (logFile.is_open())
    {
        logFile << message << std::endl;
        logFile.close();
    }
    std::cout << message << std::endl;
}

void InitializeCrucifix()
{
    // Create separate log window
    CreateLogWindow();
    LogInfo("Starting initialization in background thread...");
    
    LogToFile("[CRUCIFIX] Starting initialization in background thread...");
    
    // Wait 10 seconds for Lunar Client to fully initialize (proven working delay)
    Sleep(10000);
    
    try
    {
        // Initialize JNI and attach to JVM
        LogInfo("Attempting to attach to JVM...");
        LogToFile("[CRUCIFIX] Attempting to attach to JVM...");
        bool jniInitialized = InitializeJNI();
        
        if (jniInitialized && g_env)
        {
            LogInfo("JVM attached successfully!");
            LogToFile("[CRUCIFIX] JVM attached successfully!");
            
            // Try to load JAR via addURL method
            LogInfo("Attempting to load JAR via addURL...");
            LogToFile("[CRUCIFIX] Attempting to load JAR via addURL...");
            std::string jarPath = "C:\\Users\\raw\\Desktop\\crucifix.win\\deploy\\CrucifixPayload.jar";
            bool jarLoaded = LoadJavaPayload(jarPath);
            
            if (jarLoaded)
            {
                LogInfo("JAR loaded successfully via addURL");
                LogToFile("[CRUCIFIX] JAR loaded successfully via addURL");
            }
            else
            {
                LogWarning("JAR loading via addURL failed, trying direct class loading");
                LogToFile("[CRUCIFIX] JAR loading via addURL failed, trying direct class loading");
                
                // Try to call Java init method using context classloader
                LogToFile("[CRUCIFIX] Attempting to load Java Crucifix class with context classloader...");
                
                // Get current thread's context classloader
                jclass threadClass = g_env->FindClass("java/lang/Thread");
                if (threadClass)
                {
                    jmethodID currentThreadMethod = g_env->GetStaticMethodID(threadClass, "currentThread", "()Ljava/lang/Thread;");
                    jobject currentThread = g_env->CallStaticObjectMethod(threadClass, currentThreadMethod);
                    
                    jmethodID getContextLoader = g_env->GetMethodID(threadClass, "getContextClassLoader", "()Ljava/lang/ClassLoader;");
                    jobject contextLoader = g_env->CallObjectMethod(currentThread, getContextLoader);
                    
                    if (contextLoader)
                    {
                        LogToFile("[CRUCIFIX] Got context classloader");
                        
                        // Load class using context classloader
                        jclass loaderClass = g_env->FindClass("java/lang/ClassLoader");
                        jmethodID loadClassMethod = g_env->GetMethodID(loaderClass, "loadClass", "(Ljava/lang/String;)Ljava/lang/Class;");
                        
                        jstring className = g_env->NewStringUTF("com.crucifix.client.Crucifix");
                        jclass mainClass = (jclass)g_env->CallObjectMethod(contextLoader, loadClassMethod, className);
                        
                        if (mainClass)
                        {
                            LogToFile("[CRUCIFIX] Loaded Crucifix class via context classloader");
                            
                            jmethodID initMethod = g_env->GetStaticMethodID(mainClass, "init", "()V");
                            if (initMethod)
                            {
                                LogToFile("[CRUCIFIX] Found init method, calling...");
                                g_env->CallStaticVoidMethod(mainClass, initMethod);
                                if (g_env->ExceptionCheck())
                                {
                                    LogToFile("[CRUCIFIX] Exception in Java init()");
                                    g_env->ExceptionDescribe();
                                    g_env->ExceptionClear();
                                }
                                else
                                {
                                    LogToFile("[CRUCIFIX] Java init() called successfully");
                                    LogInfo("Java init() called successfully");
                                    
                                    // Register native methods
                                    RegisterNativeMethods(g_env);
                                }
                            }
                            else
                            {
                                LogToFile("[CRUCIFIX] Failed to find init method");
                            }
                        }
                        else
                        {
                            LogToFile("[CRUCIFIX] Failed to load Crucifix class via context classloader");
                            if (g_env->ExceptionCheck())
                            {
                                g_env->ExceptionDescribe();
                                g_env->ExceptionClear();
                            }
                        }
                    }
                    else
                    {
                        LogToFile("[CRUCIFIX] Failed to get context classloader, trying FindClass directly");
                        jclass crucifixClass = g_env->FindClass("com/crucifix/client/Crucifix");
                        if (crucifixClass)
                        {
                            LogToFile("[CRUCIFIX] Found Crucifix class via FindClass");
                            jmethodID initMethod = g_env->GetStaticMethodID(crucifixClass, "init", "()V");
                            if (initMethod)
                            {
                                LogToFile("[CRUCIFIX] Found init method, calling...");
                                g_env->CallStaticVoidMethod(crucifixClass, initMethod);
                                if (g_env->ExceptionCheck())
                                {
                                    LogToFile("[CRUCIFIX] Exception in Java init()");
                                    g_env->ExceptionDescribe();
                                    g_env->ExceptionClear();
                                }
                                else
                                {
                                    LogToFile("[CRUCIFIX] Java init() called successfully");
                                }
                            }
                            else
                            {
                                LogToFile("[CRUCIFIX] Failed to find init method");
                            }
                        }
                        else
                        {
                            LogToFile("[CRUCIFIX] Failed to find Crucifix class via FindClass");
                            
                            // Wait for Lunar Client to fully load before JVMTI
                            LogToFile("[CRUCIFIX] Waiting 5 seconds for Lunar Client to fully load...");
                            Sleep(5000);
                            
                            // Fallback: Try JVMTI with ClassPrepare hook
                            LogToFile("[CRUCIFIX] Trying JVMTI with ClassPrepare hook...");
                            std::string jarPath = "C:\\Users\\raw\\Desktop\\crucifix.win\\deploy\\CrucifixPayload.jar";
                            bool jvmtiLoaded = LoadJARViaJVMTI(jarPath);
                            
                            if (jvmtiLoaded)
                            {
                                LogToFile("[CRUCIFIX] JVMTI LoadJARViaJVMTI initiated successfully");
                            }
                            else
                            {
                                LogToFile("[CRUCIFIX] JVMTI LoadJARViaJVMTI failed");
                            }
                        }
                    }
                }
                else
                {
                    LogToFile("[CRUCIFIX] Failed to find Thread class");
                }
            }
        }
        else
        {
            LogToFile("[CRUCIFIX] JVM attachment failed, running C++ only mode");
        }
        
        // Initialize C++ modules directly
        LogToFile("[CRUCIFIX] Initializing C++ modules...");
        
        // Start event loop thread (this will initialize modules and install hooks)
        LogToFile("[CRUCIFIX] Creating event thread...");
        std::thread eventThread(EventLoop);
        LogToFile("[CRUCIFIX] Event thread created, detaching...");
        eventThread.detach();
        LogToFile("[CRUCIFIX] Event thread detached");
        
        LogToFile("[CRUCIFIX] Crucifix C++ client is now running!");
        
        // Keep this thread alive to prevent DLL unload
        LogToFile("[CRUCIFIX] Init thread entering sleep loop...");
        while (!g_shouldExit)
        {
            Sleep(1000);
            LogToFile("[CRUCIFIX] Init thread still alive");
        }
        LogToFile("[CRUCIFIX] Init thread exiting");
    }
    catch (const std::exception& e)
    {
        LogToFile("[CRUCIFIX] Exception during initialization: " + std::string(e.what()));
    }
    catch (...)
    {
        LogToFile("[CRUCIFIX] Unknown exception during initialization");
    }
}

BOOL APIENTRY DllMain(HMODULE hModule, DWORD ul_reason_for_call, LPVOID lpReserved)
{
    switch (ul_reason_for_call)
    {
    case DLL_PROCESS_ATTACH:
    {
        g_hModule = hModule;
        DisableThreadLibraryCalls(hModule);
        
        // Enable console for debugging
        AllocConsole();
        freopen("CONOUT$", "w", stdout);
        freopen("CONIN$", "r", stdin);
        
        // Clear log file
        std::ofstream logFile("C:\\Users\\raw\\Desktop\\crucifix_dll.log", std::ios::trunc);
        logFile.close();
        
        std::cout << "=== Crucifix DLL Attached ===" << std::endl;
        LogToFile("=== Crucifix DLL Attached ===");
        
        // Initialize in a separate thread to prevent freezing
        std::thread initThread(InitializeCrucifix);
        initThread.detach();
        
        break;
    }
    case DLL_PROCESS_DETACH:
    {
        std::cout << "[CRUCIFIX] DLL Detaching" << std::endl;
        LogToFile("[CRUCIFIX] DLL Detaching");
        
        g_shouldExit = true;
        Sleep(100); // Give thread time to exit
        
        if (g_keyboardHook)
        {
            UnhookWindowsHookEx(g_keyboardHook);
        }
        
        Unhook_wglSwapBuffers();
        CleanupJNI();
        FreeConsole();
        break;
    }
    }
    return TRUE;
}

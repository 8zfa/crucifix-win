#include <windows.h>
#include <jni.h>
#include <iostream>
#include <fstream>
#include <string>

// Global variables
HMODULE g_hModule = nullptr;
JavaVM* g_jvm = nullptr;
JNIEnv* g_env = nullptr;

// Function pointer type for JNI_GetCreatedJavaVMs
typedef jint (JNICALL *JNI_GetCreatedJavaVMsFunc)(JavaVM**, jsize, jsize*);

// Log to file
void LogToFile(const char* message)
{
    std::ofstream logFile("C:\\Users\\raw\\Desktop\\crucifix_payload.log", std::ios::app);
    if (logFile.is_open())
    {
        logFile << message << std::endl;
        logFile.close();
    }
    std::cout << message << std::endl;
}

void LogToFile(const std::string& message)
{
    LogToFile(message.c_str());
}

// Initialize JNI using ToadClient's approach
bool InitializeJNI()
{
    LogToFile("[Payload] Starting JNI initialization...");
    
    // Get jvm.dll handle
    HMODULE jvmModule = GetModuleHandleA("jvm.dll");
    if (!jvmModule)
    {
        LogToFile("[Payload] jvm.dll not loaded, trying to load it...");
        jvmModule = LoadLibraryA("jvm.dll");
        if (!jvmModule)
        {
            LogToFile("[Payload] Failed to load jvm.dll");
            return false;
        }
    }
    
    LogToFile("[Payload] jvm.dll found");
    
    // Get JNI_GetCreatedJavaVMs function
    JNI_GetCreatedJavaVMsFunc jniGetCreatedJavaVMs = (JNI_GetCreatedJavaVMsFunc)GetProcAddress(jvmModule, "JNI_GetCreatedJavaVMs");
    if (!jniGetCreatedJavaVMs)
    {
        LogToFile("[Payload] Failed to get JNI_GetCreatedJavaVMs address");
        return false;
    }
    
    LogToFile("[Payload] JNI_GetCreatedJavaVMs function found");
    
    // Call JNI_GetCreatedJavaVMs
    jsize vmCount = 0;
    jint result = jniGetCreatedJavaVMs(&g_jvm, 1, &vmCount);
    
    if (result != JNI_OK)
    {
        LogToFile("[Payload] JNI_GetCreatedJavaVMs returned error: " + std::to_string(result));
        return false;
    }
    
    if (vmCount == 0)
    {
        LogToFile("[Payload] No JVM found (vmCount = 0)");
        return false;
    }
    
    if (!g_jvm)
    {
        LogToFile("[Payload] JVM pointer is null");
        return false;
    }
    
    LogToFile("[Payload] JVM found at: " + std::to_string(reinterpret_cast<uintptr_t>(g_jvm)));
    
    // Attach current thread
    result = g_jvm->AttachCurrentThread((void**)&g_env, nullptr);
    if (result != JNI_OK)
    {
        LogToFile("[Payload] Failed to attach current thread, error: " + std::to_string(result));
        return false;
    }
    
    if (!g_env)
    {
        LogToFile("[Payload] JNIEnv is null after attach");
        return false;
    }
    
    LogToFile("[Payload] Successfully attached to JVM");
    LogToFile("[Payload] JNI initialization complete!");
    return true;
}

// Cleanup JNI
void CleanupJNI()
{
    if (g_jvm && g_env)
    {
        LogToFile("[Payload] Detaching from JVM...");
        g_jvm->DetachCurrentThread();
        g_env = nullptr;
        g_jvm = nullptr;
    }
}

// Injection thread
DWORD WINAPI InjectionThread(LPVOID lpParam)
{
    LogToFile("[Payload] Injection thread started");
    
    // Wait 8-10 seconds for Lunar Client to fully initialize
    LogToFile("[Payload] Waiting 10 seconds for Lunar Client to initialize...");
    Sleep(10000);
    
    LogToFile("[Payload] Starting JNI initialization...");
    
    if (InitializeJNI())
    {
        LogToFile("[Payload] === SUCCESS: JNI attachment confirmed ===");
        LogToFile("[Payload] Payload is running without crashes!");
        
        // Keep thread alive to prove stability
        for (int i = 0; i < 30; i++)
        {
            Sleep(1000);
            if (i % 5 == 0)
            {
                LogToFile("[Payload] Still running... (" + std::to_string(30 - i) + "s remaining)");
            }
        }
        
        LogToFile("[Payload] Test complete, cleaning up...");
    }
    else
    {
        LogToFile("[Payload] === FAILED: JNI initialization failed ===");
    }
    
    CleanupJNI();
    LogToFile("[Payload] Injection thread exiting");
    return 0;
}

BOOL APIENTRY DllMain(HMODULE hModule, DWORD ul_reason_for_call, LPVOID lpReserved)
{
    switch (ul_reason_for_call)
    {
    case DLL_PROCESS_ATTACH:
    {
        g_hModule = hModule;
        DisableThreadLibraryCalls(hModule);
        
        // Clear log file
        std::ofstream logFile("C:\\Users\\raw\\Desktop\\crucifix_payload.log", std::ios::trunc);
        logFile.close();
        
        LogToFile("=== Crucifix Payload DLL Attached ===");
        LogToFile("[Payload] Creating injection thread...");
        
        HANDLE hThread = CreateThread(nullptr, 0, InjectionThread, nullptr, 0, nullptr);
        if (hThread)
        {
            CloseHandle(hThread);
            LogToFile("[Payload] Injection thread created successfully");
        }
        else
        {
            LogToFile("[Payload] Failed to create injection thread");
        }
        
        break;
    }
    case DLL_PROCESS_DETACH:
    {
        LogToFile("[Payload] DLL detaching");
        CleanupJNI();
        break;
    }
    }
    return TRUE;
}

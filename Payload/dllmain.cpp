#include <windows.h>
#include <jni.h>
#include <iostream>
#include <fstream>
#include <string>

HMODULE g_hModule = nullptr;
JavaVM* g_jvm = nullptr;
JNIEnv* g_env = nullptr;

typedef jint (JNICALL *JNI_GetCreatedJavaVMsFunc)(JavaVM**, jsize, jsize*);

void LogToFile(const std::string& message)
{
    std::ofstream logFile("C:\\Users\\Public\\crucifix_payload.log", std::ios::app);
    if (logFile.is_open())
    {
        logFile << message << std::endl;
        logFile.close();
    }
    std::cout << message << std::endl;
}

bool InitializeJNI()
{
    LogToFile("[Payload] Starting JNI initialization...");

    HMODULE jvmModule = GetModuleHandleA("jvm.dll");
    if (!jvmModule)
    {
        LogToFile("[Payload] jvm.dll not found in process, trying to load...");
        jvmModule = LoadLibraryA("jvm.dll");
        if (!jvmModule)
        {
            LogToFile("[Payload] Failed to load jvm.dll");
            return false;
        }
    }

    LogToFile("[Payload] jvm.dll found");

    JNI_GetCreatedJavaVMsFunc jniGetCreatedJavaVMs =
        (JNI_GetCreatedJavaVMsFunc)GetProcAddress(jvmModule, "JNI_GetCreatedJavaVMs");
    if (!jniGetCreatedJavaVMs)
    {
        LogToFile("[Payload] Failed to get JNI_GetCreatedJavaVMs address");
        return false;
    }

    jsize vmCount = 0;
    jint result = jniGetCreatedJavaVMs(&g_jvm, 1, &vmCount);

    if (result != JNI_OK || vmCount == 0 || !g_jvm)
    {
        LogToFile("[Payload] No JVM found (count=" + std::to_string(vmCount) + " result=" + std::to_string(result) + ")");
        return false;
    }

    LogToFile("[Payload] JVM found, attaching thread...");

    result = g_jvm->AttachCurrentThread((void**)&g_env, nullptr);
    if (result != JNI_OK || !g_env)
    {
        LogToFile("[Payload] Failed to attach to JVM, error: " + std::to_string(result));
        return false;
    }

    LogToFile("[Payload] Successfully attached to JVM!");
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

DWORD WINAPI InjectionThread(LPVOID lpParam)
{
    LogToFile("[Payload] Injection thread started");
    LogToFile("[Payload] Waiting 15 seconds for Lunar to fully initialize...");
    Sleep(15000);

    if (!InitializeJNI())
    {
        LogToFile("[Payload] JNI initialization failed, exiting");
        return 1;
    }

    LogToFile("[Payload] Looking for Crucifix class...");

    jclass crucifixClass = g_env->FindClass("com/crucifix/client/Crucifix");
    if (!crucifixClass)
    {
        LogToFile("[Payload] Could not find Crucifix class!");
        LogToFile("[Payload] Make sure CrucifixLoader ran before launching Lunar");
        if (g_env->ExceptionCheck()) {
            g_env->ExceptionDescribe();
            g_env->ExceptionClear();
        }
        CleanupJNI();
        return 1;
    }

    LogToFile("[Payload] Found Crucifix class!");

    jmethodID initMethod = g_env->GetStaticMethodID(crucifixClass, "init", "()V");
    if (!initMethod)
    {
        LogToFile("[Payload] Could not find Crucifix.init() method!");
        if (g_env->ExceptionCheck()) {
            g_env->ExceptionDescribe();
            g_env->ExceptionClear();
        }
        CleanupJNI();
        return 1;
    }

    LogToFile("[Payload] Calling Crucifix.init()...");
    g_env->CallStaticVoidMethod(crucifixClass, initMethod);

    if (g_env->ExceptionCheck())
    {
        LogToFile("[Payload] Exception thrown during Crucifix.init()!");
        g_env->ExceptionDescribe();
        g_env->ExceptionClear();
    }
    else
    {
        LogToFile("[Payload] Crucifix.init() completed successfully!");
    }

    CleanupJNI();
    LogToFile("[Payload] Injection thread done");
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

        // Clear log
        std::ofstream logFile("C:\\Users\\Public\\crucifix_payload.log", std::ios::trunc);
        logFile.close();

        LogToFile("=== Crucifix Payload DLL Attached ===");

        HANDLE hThread = CreateThread(nullptr, 0, InjectionThread, nullptr, 0, nullptr);
        if (hThread)
        {
            CloseHandle(hThread);
            LogToFile("[Payload] Injection thread created");
        }
        else
        {
            LogToFile("[Payload] Failed to create injection thread!");
        }
        break;
    }
    case DLL_PROCESS_DETACH:
        CleanupJNI();
        break;
    }
    return TRUE;
}

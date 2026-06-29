#include <windows.h>
#include <stdio.h>
#include <jni.h>

extern JavaVM* g_jvm;
extern bool g_initialized;

// Keyboard hook procedure
LRESULT CALLBACK KeyboardProc(int nCode, WPARAM wParam, LPARAM lParam) {
    if (nCode >= 0) {
        KBDLLHOOKSTRUCT* pKbStruct = (KBDLLHOOKSTRUCT*)lParam;
        
        if (wParam == WM_KEYDOWN) {
            int key = pKbStruct->vkCode;
            
            // RSHIFT key to open ClickGUI
            if (key == VK_RSHIFT) {
                printf("[Keyboard] RSHIFT pressed - toggling ClickGUI\n");
                
                if (g_jvm != nullptr && g_initialized) {
                    JNIEnv* env;
                    if (g_jvm->AttachCurrentThread((void**)&env, nullptr) == JNI_OK) {
                        jclass crucifixClass = env->FindClass("com/crucifix/client/Crucifix");
                        if (crucifixClass != nullptr) {
                            jmethodID toggleMethod = env->GetStaticMethodID(
                                crucifixClass, 
                                "toggleClickGUI", 
                                "()V"
                            );
                            if (toggleMethod != nullptr) {
                                env->CallStaticVoidMethod(crucifixClass, toggleMethod);
                                printf("[Keyboard] Called Crucifix.toggleClickGUI()\n");
                            } else {
                                printf("[Keyboard] Could not find toggleClickGUI method\n");
                            }
                        } else {
                            printf("[Keyboard] Could not find Crucifix class\n");
                        }
                        g_jvm->DetachCurrentThread();
                    } else {
                        printf("[Keyboard] Could not attach to JVM\n");
                    }
                } else {
                    printf("[Keyboard] JVM not ready (g_jvm=%p, g_initialized=%d)\n", g_jvm, g_initialized);
                }
                
                return 1; // Prevent default behavior
            }
        }
    }
    
    return CallNextHookEx(NULL, nCode, wParam, lParam);
}

HHOOK g_KeyboardHook = NULL;

bool InstallKeyboardHook() {
    g_KeyboardHook = SetWindowsHookEx(
        WH_KEYBOARD_LL,
        KeyboardProc,
        GetModuleHandle(NULL),
        0
    );
    
    if (g_KeyboardHook == NULL) {
        printf("[Keyboard] Failed to install hook: %d\n", GetLastError());
        return false;
    }
    
    printf("[Keyboard] Hook installed successfully\n");
    return true;
}

void UninstallKeyboardHook() {
    if (g_KeyboardHook != NULL) {
        UnhookWindowsHookEx(g_KeyboardHook);
        g_KeyboardHook = NULL;
        printf("[Keyboard] Hook uninstalled\n");
    }
}

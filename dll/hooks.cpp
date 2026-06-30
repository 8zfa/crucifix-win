#include <windows.h>
#include <detours.h>
#include <GL/gl.h>
#include <imgui.h>
#include <imgui_impl_win32.h>
#include <imgui_impl_opengl2.h>
#include <jni.h>
#include <stdio.h>

// Forward declarations
JNIEnv* getJNIEnv();

typedef BOOL(WINAPI* wglSwapBuffers_t)(HDC hdc);
static wglSwapBuffers_t original_wglSwapBuffers = nullptr;
static bool imgui_initialized = false;
static HGLRC g_glrc = nullptr;
static HDC g_hdc = nullptr;

// Initialize ImGui context
void initImGui(HWND hwnd, HDC hdc) {
    if (imgui_initialized) return;
    g_hdc = hdc;

    IMGUI_CHECKVERSION();
    ImGui::CreateContext();
    ImGuiIO& io = ImGui::GetIO();
    io.IniFilename = nullptr; // disable .ini file

    // Setup platform/renderer backends
    ImGui_ImplWin32_Init(hwnd);
    ImGui_ImplOpenGL2_Init();

    // Style
    ImGui::StyleColorsDark();

    imgui_initialized = true;
    printf("[DLL] ImGui initialized.\n");
    FILE* f = fopen("C:\\crucifix_imgui_init.txt", "w");
    if (f) { fprintf(f, "ImGui initialized\n"); fclose(f); }
}

// Hooked wglSwapBuffers
BOOL WINAPI Hooked_wglSwapBuffers(HDC hdc) {
    // Get current OpenGL context
    HGLRC current_glrc = wglGetCurrentContext();
    if (current_glrc == nullptr) return original_wglSwapBuffers(hdc);

    // Initialize ImGui if not done
    if (!imgui_initialized) {
        HWND hwnd = WindowFromDC(hdc);
        initImGui(hwnd, hdc);
    }

    // Get JNI environment
    JNIEnv* env = getJNIEnv();
    if (env != nullptr) {
        // Call ClickGUI.render()
        jclass guiClass = env->FindClass("com/crucifix/client/gui/ClickGUI");
        if (guiClass != nullptr) {
            jmethodID getInstance = env->GetStaticMethodID(guiClass, "getInstance", "()Lcom/crucifix/client/gui/ClickGUI;");
            if (getInstance != nullptr) {
                jobject gui = env->CallStaticObjectMethod(guiClass, getInstance);
                if (gui != nullptr) {
                    jmethodID render = env->GetMethodID(env->GetObjectClass(gui), "render", "()V");
                    if (render != nullptr) {
                        env->CallVoidMethod(gui, render);
                    } else {
                        // render method not found – write error
                        static bool once = true;
                        if (once) {
                            FILE* f = fopen("C:\\crucifix_render_method_error.txt", "w");
                            if (f) { fprintf(f, "render() method not found\n"); fclose(f); }
                            once = false;
                        }
                    }
                }
            }
        }
    }

    // Now let ImGui render
    if (imgui_initialized) {
        ImGui_ImplOpenGL2_NewFrame();
        ImGui_ImplWin32_NewFrame();
        ImGui::NewFrame();

        // You can draw ImGui windows here if you want (but Java already does that)
        // The Java render() calls ImGui functions directly.

        ImGui::Render();
        ImGui_ImplOpenGL2_RenderDrawData(ImGui::GetDrawData());
    }

    // Call original swap buffers
    return original_wglSwapBuffers(hdc);
}

// Internal hook installation
void installHooks() {
    HMODULE opengl32 = GetModuleHandleA("opengl32.dll");
    if (opengl32) {
        original_wglSwapBuffers = (wglSwapBuffers_t)GetProcAddress(opengl32, "wglSwapBuffers");
        if (original_wglSwapBuffers) {
            DetourTransactionBegin();
            DetourUpdateThread(GetCurrentThread());
            DetourAttach(&(PVOID&)original_wglSwapBuffers, Hooked_wglSwapBuffers);
            if (DetourTransactionCommit() == NO_ERROR) {
                printf("[DLL] wglSwapBuffers hooked successfully.\n");
                FILE* f = fopen("C:\\crucifix_hook_ok.txt", "w");
                if (f) { fprintf(f, "Hook installed\n"); fclose(f); }
            } else {
                printf("[DLL] Failed to hook wglSwapBuffers.\n");
            }
        }
    }
}

// Hook installation function (called from DllMain or injector)
void Hook_wglSwapBuffers() {
    installHooks();
}

// Unhook function
void Unhook_wglSwapBuffers() {
    HMODULE opengl32 = GetModuleHandleA("opengl32.dll");
    if (opengl32 && original_wglSwapBuffers) {
        DetourTransactionBegin();
        DetourUpdateThread(GetCurrentThread());
        DetourDetach(&(PVOID&)original_wglSwapBuffers, Hooked_wglSwapBuffers);
        DetourTransactionCommit();
        printf("[DLL] wglSwapBuffers unhooked.\n");
    }
}

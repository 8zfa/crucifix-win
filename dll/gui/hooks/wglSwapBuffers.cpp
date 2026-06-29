#include "../ClickGUI.h"
#include <gl/GL.h>
#include <MinHook.h>
#include <mutex>
#include <iostream>

typedef BOOL(__stdcall* wglSwapBuffers_t)(HDC hdc);
wglSwapBuffers_t original_wglSwapBuffers = nullptr;
std::once_flag setupFlag;

HWND g_hdcWindow = nullptr;
HGLRC g_originalGLContext = nullptr;
HGLRC g_menuGLContext = nullptr;
HDC g_hdc = nullptr;

BOOL __stdcall hook_wglSwapBuffers(HDC hdc)
{
    static int callCount = 0;
    callCount++;
    
    g_hdc = hdc;
    g_hdcWindow = WindowFromDC(hdc);
    g_originalGLContext = wglGetCurrentContext();
    
    std::call_once(setupFlag, [&] {
        std::cout << "[wglSwapBuffers] First call, setting up ImGui" << std::endl;
        
        // Create OpenGL context for ImGui
        g_menuGLContext = wglCreateContext(hdc);
        if (!g_menuGLContext)
        {
            std::cout << "[wglSwapBuffers] Failed to create GL context" << std::endl;
            return;
        }
        
        wglMakeCurrent(hdc, g_menuGLContext);
        
        // Setup OpenGL
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        
        GLint viewport[4];
        glGetIntegerv(GL_VIEWPORT, viewport);
        
        glOrtho(0, viewport[2], viewport[3], 0, 1, -1);
        glMatrixMode(GL_MODELVIEW);
        glLoadIdentity();
        
        // Initialize ImGui
        ImGui::CreateContext();
        ImGuiIO& io = ImGui::GetIO();
        io.ConfigFlags |= ImGuiConfigFlags_NavEnableKeyboard;
        
        ImGui::StyleColorsDark();
        
        ImGui_ImplWin32_Init(g_hdcWindow);
        ImGui_ImplOpenGL2_Init();
        
        std::cout << "[wglSwapBuffers] ImGui initialized" << std::endl;
        
        wglMakeCurrent(hdc, g_originalGLContext);
    });
    
    // Switch to menu GL context for rendering
    wglMakeCurrent(g_hdc, g_menuGLContext);
    
    ImGui_ImplOpenGL2_NewFrame();
    ImGui_ImplWin32_NewFrame();
    ImGui::NewFrame();
    
    // Render ClickGUI if open
    bool isOpen = ClickGUI::getInstance().isOpen();
    if (isOpen)
    {
        if (callCount % 60 == 0) // Log every 60 frames to avoid spam
        {
            std::cout << "[wglSwapBuffers] Rendering ClickGUI, callCount=" << callCount << std::endl;
        }
        ClickGUI::getInstance().render();
    }
    
    ImGui::EndFrame();
    ImGui::Render();
    ImGui_ImplOpenGL2_RenderDrawData(ImGui::GetDrawData());
    
    // Switch back to original GL context
    wglMakeCurrent(g_hdc, g_originalGLContext);
    
    return original_wglSwapBuffers(hdc);
}

void Hook_wglSwapBuffers()
{
    std::cout << "[wglSwapBuffers] Installing hook..." << std::endl;
    
    // Initialize MinHook
    MH_STATUS status = MH_Initialize();
    if (status != MH_OK && status != MH_ERROR_ALREADY_INITIALIZED)
    {
        std::cout << "[wglSwapBuffers] Failed to initialize MinHook: " << status << std::endl;
        return;
    }
    
    HMODULE opengl32Handle = GetModuleHandleA("opengl32.dll");
    if (opengl32Handle == nullptr)
    {
        std::cout << "[wglSwapBuffers] Failed to find opengl32.dll" << std::endl;
        return;
    }
    
    LPVOID wglSwapBuffersAddr = GetProcAddress(opengl32Handle, "wglSwapBuffers");
    if (wglSwapBuffersAddr == nullptr)
    {
        std::cout << "[wglSwapBuffers] Failed to find wglSwapBuffers" << std::endl;
        return;
    }
    
    std::cout << "[wglSwapBuffers] wglSwapBuffers address: " << wglSwapBuffersAddr << std::endl;
    
    status = MH_CreateHook(wglSwapBuffersAddr, (LPVOID)hook_wglSwapBuffers, (LPVOID*)&original_wglSwapBuffers);
    if (status != MH_OK)
    {
        std::cout << "[wglSwapBuffers] Failed to create hook: " << status << std::endl;
        return;
    }
    
    status = MH_EnableHook(wglSwapBuffersAddr);
    if (status != MH_OK)
    {
        std::cout << "[wglSwapBuffers] Failed to enable hook: " << status << std::endl;
        return;
    }
    
    std::cout << "[wglSwapBuffers] Hook installed successfully" << std::endl;
}

void Unhook_wglSwapBuffers()
{
    std::cout << "[wglSwapBuffers] Removing hook..." << std::endl;
    MH_DisableHook(MH_ALL_HOOKS);
    MH_RemoveHook(MH_ALL_HOOKS);
}

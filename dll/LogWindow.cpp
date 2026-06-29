#include <windows.h>
#include <stdio.h>
#include <fcntl.h>
#include <io.h>

static HANDLE g_hConsole = NULL;
static bool g_logWindowCreated = false;

void CreateLogWindow() {
    if (g_logWindowCreated) return;
    
    // Allocate a new console for this process
    if (AllocConsole()) {
        // Redirect stdout to the new console
        freopen("CONOUT$", "w", stdout);
        freopen("CONOUT$", "w", stderr);
        freopen("CONIN$", "r", stdin);
        
        // Set console title
        SetConsoleTitleA("CRUCIFIX.WIN Debug Log");
        
        // Set console color
        HANDLE hConsole = GetStdHandle(STD_OUTPUT_HANDLE);
        SetConsoleTextAttribute(hConsole, FOREGROUND_GREEN | FOREGROUND_INTENSITY);
        
        printf("========================================\n");
        printf("   CRUCIFIX.WIN Debug Console\n");
        printf("========================================\n\n");
        
        g_logWindowCreated = true;
        g_hConsole = hConsole;
    }
}

void CloseLogWindow() {
    if (g_logWindowCreated) {
        printf("\n========================================\n");
        printf("   Closing Debug Console\n");
        printf("========================================\n");
        
        FreeConsole();
        g_logWindowCreated = false;
        g_hConsole = NULL;
    }
}

bool IsLogWindowCreated() {
    return g_logWindowCreated;
}

void LogInfo(const char* message) {
    if (g_logWindowCreated) {
        HANDLE hConsole = GetStdHandle(STD_OUTPUT_HANDLE);
        SetConsoleTextAttribute(hConsole, FOREGROUND_GREEN | FOREGROUND_INTENSITY);
        printf("[INFO] %s\n", message);
    }
}

void LogWarning(const char* message) {
    if (g_logWindowCreated) {
        HANDLE hConsole = GetStdHandle(STD_OUTPUT_HANDLE);
        SetConsoleTextAttribute(hConsole, FOREGROUND_RED | FOREGROUND_GREEN | FOREGROUND_INTENSITY);
        printf("[WARN] %s\n", message);
    }
}

void LogError(const char* message) {
    if (g_logWindowCreated) {
        HANDLE hConsole = GetStdHandle(STD_OUTPUT_HANDLE);
        SetConsoleTextAttribute(hConsole, FOREGROUND_RED | FOREGROUND_INTENSITY);
        printf("[ERROR] %s\n", message);
    }
}

void LogDebug(const char* message) {
    if (g_logWindowCreated) {
        HANDLE hConsole = GetStdHandle(STD_OUTPUT_HANDLE);
        SetConsoleTextAttribute(hConsole, FOREGROUND_BLUE | FOREGROUND_GREEN | FOREGROUND_INTENSITY);
        printf("[DEBUG] %s\n", message);
    }
}

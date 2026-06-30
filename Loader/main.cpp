#include <windows.h>
#include <iostream>
#include <string>

int main()
{
    AllocConsole();
    freopen("CONOUT$", "w", stdout);
    freopen("CONIN$", "r", stdin);

    std::cout << "=== Crucifix Loader ===" << std::endl;

    // Get current directory
    char currentPath[MAX_PATH];
    GetModuleFileNameA(NULL, currentPath, MAX_PATH);
    std::string currentDir = currentPath;
    size_t lastSlash = currentDir.find_last_of("\\/");
    if (lastSlash != std::string::npos)
        currentDir = currentDir.substr(0, lastSlash);

    // Get username
    char username[256];
    DWORD usernameSize = sizeof(username);
    if (!GetUserNameA(username, &usernameSize))
    {
        std::cout << "[Loader] Failed to get username, exiting" << std::endl;
        std::cin.get();
        return 1;
    }

    // Build overrides path
    std::string overridesPath = "C:\\Users\\";
    overridesPath += username;
    overridesPath += "\\.lunarclient\\offline\\multiver\\overrides\\";

    std::cout << "[Loader] Overrides path: " << overridesPath << std::endl;

    // Create directory if it doesn't exist
    CreateDirectoryA(overridesPath.c_str(), nullptr);

    // Copy JAR
    std::string sourceJar = currentDir + "\\CrucifixPayload.jar";
    std::string destJar = overridesPath + "opengl-Crucifix.jar";

    std::cout << "[Loader] Copying JAR..." << std::endl;
    std::cout << "[Loader] From: " << sourceJar << std::endl;
    std::cout << "[Loader] To:   " << destJar << std::endl;

    if (CopyFileA(sourceJar.c_str(), destJar.c_str(), FALSE))
    {
        std::cout << "[Loader] JAR copied successfully!" << std::endl;
    }
    else
    {
        std::cout << "[Loader] Failed to copy JAR (error: " << GetLastError() << ")" << std::endl;
        std::cout << "[Loader] Make sure CrucifixPayload.jar is in the same folder as this exe" << std::endl;
        std::cin.get();
        return 1;
    }

    std::cout << std::endl;
    std::cout << "[Loader] Done! Now:" << std::endl;
    std::cout << "[Loader] 1. Launch Lunar Client" << std::endl;
    std::cout << "[Loader] 2. Wait until you reach the main menu" << std::endl;
    std::cout << "[Loader] 3. Run CrucifixInjector.exe" << std::endl;
    std::cout << std::endl;
    std::cout << "Press any key to exit..." << std::endl;
    std::cin.get();
    return 0;
}

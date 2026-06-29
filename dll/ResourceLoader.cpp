#include "ResourceLoader.h"
#include <windows.h>
#include <iostream>
#include <fstream>
#include <shlobj.h>

// Resource ID for the embedded JAR
#define IDR_JAR_FILE 101

std::string ExtractEmbeddedJAR()
{
    // Get temp directory
    char tempPath[MAX_PATH];
    GetTempPathA(MAX_PATH, tempPath);
    
    std::string jarPath = std::string(tempPath) + "CrucifixPayload.jar";
    
    // Try to load from resources
    HMODULE hModule = GetModuleHandleA("CrucifixDLL.dll");
    if (!hModule)
    {
        std::cout << "[ResourceLoader] Failed to get module handle" << std::endl;
        return "";
    }
    
    HRSRC hResource = FindResourceA(hModule, MAKEINTRESOURCEA(IDR_JAR_FILE), "BINARY");
    if (!hResource)
    {
        std::cout << "[ResourceLoader] Resource not found, trying to load from file" << std::endl;
        
        // Try to load from file next to DLL
        char dllPath[MAX_PATH];
        GetModuleFileNameA(hModule, dllPath, MAX_PATH);
        std::string dllDir = dllPath;
        size_t lastSlash = dllDir.find_last_of("\\/");
        if (lastSlash != std::string::npos)
        {
            dllDir = dllDir.substr(0, lastSlash);
        }
        
        std::string externalJarPath = dllDir + "\\CrucifixPayload.jar";
        std::ifstream checkFile(externalJarPath, std::ios::binary);
        if (checkFile.good())
        {
            checkFile.close();
            std::cout << "[ResourceLoader] Found external JAR at: " << externalJarPath << std::endl;
            return externalJarPath;
        }
        
        return "";
    }
    
    HGLOBAL hLoaded = LoadResource(hModule, hResource);
    if (!hLoaded)
    {
        std::cout << "[ResourceLoader] Failed to load resource" << std::endl;
        return "";
    }
    
    void* pData = LockResource(hLoaded);
    DWORD size = SizeofResource(hModule, hResource);
    
    if (!pData || size == 0)
    {
        std::cout << "[ResourceLoader] Invalid resource data" << std::endl;
        return "";
    }
    
    // Write to temp file
    std::ofstream outFile(jarPath, std::ios::binary);
    if (!outFile)
    {
        std::cout << "[ResourceLoader] Failed to create output file" << std::endl;
        return "";
    }
    
    outFile.write((const char*)pData, size);
    outFile.close();
    
    std::cout << "[ResourceLoader] Extracted JAR to: " << jarPath << std::endl;
    return jarPath;
}

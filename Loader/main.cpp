#include <windows.h>
#include <tlhelp32.h>
#include <iostream>
#include <string>
#include <psapi.h>

// Find Lunar Client process (javaw.exe)
DWORD FindLunarClientProcess()
{
    HANDLE hSnapshot = CreateToolhelp32Snapshot(TH32CS_SNAPPROCESS, 0);
    if (hSnapshot == INVALID_HANDLE_VALUE)
    {
        std::cout << "[Loader] Failed to create process snapshot" << std::endl;
        return 0;
    }

    PROCESSENTRY32 pe32;
    pe32.dwSize = sizeof(PROCESSENTRY32);

    if (Process32First(hSnapshot, &pe32))
    {
        do
        {
            std::string processName = pe32.szExeFile;
            if (processName == "javaw.exe")
            {
                // Check if it's Lunar Client by examining command line
                HANDLE hProcess = OpenProcess(PROCESS_QUERY_INFORMATION | PROCESS_VM_READ, FALSE, pe32.th32ProcessID);
                if (hProcess)
                {
                    char cmdLine[MAX_PATH * 4];
                    if (GetModuleFileNameExA(hProcess, NULL, cmdLine, sizeof(cmdLine)))
                    {
                        std::string cmdLineStr = cmdLine;
                        if (cmdLineStr.find("Lunar") != std::string::npos || cmdLineStr.find("lunar") != std::string::npos)
                        {
                            CloseHandle(hProcess);
                            CloseHandle(hSnapshot);
                            std::cout << "[Loader] Found Lunar Client process: " << pe32.th32ProcessID << std::endl;
                            return pe32.th32ProcessID;
                        }
                    }
                    CloseHandle(hProcess);
                }
                // If we can't verify, just return first javaw.exe
                CloseHandle(hSnapshot);
                std::cout << "[Loader] Found javaw.exe process: " << pe32.th32ProcessID << std::endl;
                return pe32.th32ProcessID;
            }
        } while (Process32Next(hSnapshot, &pe32));
    }

    CloseHandle(hSnapshot);
    return 0;
}

// Inject DLL into process
bool InjectDLL(DWORD processId, const std::string& dllPath)
{
    HANDLE hProcess = OpenProcess(PROCESS_ALL_ACCESS, FALSE, processId);
    if (!hProcess)
    {
        std::cout << "[Loader] Failed to open process: " << GetLastError() << std::endl;
        return false;
    }

    // Allocate memory for DLL path
    SIZE_T pathLen = dllPath.length() + 1;
    LPVOID pRemoteMem = VirtualAllocEx(hProcess, NULL, pathLen, MEM_COMMIT | MEM_RESERVE, PAGE_EXECUTE_READWRITE);
    if (!pRemoteMem)
    {
        std::cout << "[Loader] Failed to allocate memory: " << GetLastError() << std::endl;
        CloseHandle(hProcess);
        return false;
    }

    // Write DLL path to remote memory
    if (!WriteProcessMemory(hProcess, pRemoteMem, dllPath.c_str(), pathLen, NULL))
    {
        std::cout << "[Loader] Failed to write memory: " << GetLastError() << std::endl;
        VirtualFreeEx(hProcess, pRemoteMem, 0, MEM_RELEASE);
        CloseHandle(hProcess);
        return false;
    }

    // Get LoadLibraryA address
    HMODULE hKernel32 = GetModuleHandleA("kernel32.dll");
    LPVOID pLoadLibraryA = (LPVOID)GetProcAddress(hKernel32, "LoadLibraryA");
    if (!pLoadLibraryA)
    {
        std::cout << "[Loader] Failed to get LoadLibraryA address" << std::endl;
        VirtualFreeEx(hProcess, pRemoteMem, 0, MEM_RELEASE);
        CloseHandle(hProcess);
        return false;
    }

    // Create remote thread to load DLL
    HANDLE hThread = CreateRemoteThread(hProcess, NULL, 0, (LPTHREAD_START_ROUTINE)pLoadLibraryA, pRemoteMem, 0, NULL);
    if (!hThread)
    {
        std::cout << "[Loader] Failed to create remote thread: " << GetLastError() << std::endl;
        VirtualFreeEx(hProcess, pRemoteMem, 0, MEM_RELEASE);
        CloseHandle(hProcess);
        return false;
    }

    std::cout << "[Loader] Injection thread created, waiting for completion..." << std::endl;
    WaitForSingleObject(hThread, INFINITE);

    // Cleanup
    CloseHandle(hThread);
    VirtualFreeEx(hProcess, pRemoteMem, 0, MEM_RELEASE);
    CloseHandle(hProcess);

    std::cout << "[Loader] DLL injected successfully" << std::endl;
    return true;
}

int main()
{
    // Enable console
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
    
    // Copy JAR to Lunar Client overrides folder
    char username[256];
    DWORD usernameSize = sizeof(username);
    if (GetUserNameA(username, &usernameSize))
    {
        std::string overridesPath = "C:\\Users\\";
        overridesPath += username;
        overridesPath += "\\.lunarclient\\offline\\multiver\\overrides\\";
        
        std::cout << "[Loader] Checking overrides path: " << overridesPath << std::endl;
        
        // Create directory if it doesn't exist
        CreateDirectoryA(overridesPath.c_str(), nullptr);
        
        // Copy JAR to overrides folder
        std::string sourceJar = currentDir + "\\CrucifixPayload.jar";
        std::string destJar = overridesPath + "opengl-Crucifix.jar";
        
        std::cout << "[Loader] Copying JAR from: " << sourceJar << std::endl;
        std::cout << "[Loader] Copying JAR to: " << destJar << std::endl;
        
        BOOL copyResult = CopyFileA(sourceJar.c_str(), destJar.c_str(), FALSE);
        if (copyResult)
        {
            std::cout << "[Loader] JAR copied successfully to overrides folder" << std::endl;
            std::cout << "[Loader] Launching Lunar Client..." << std::endl;
            
            // Launch Lunar Client
            HINSTANCE result = ShellExecuteA(NULL, "open", "C:\\Program Files\\Lunar Client\\Lunar Client.exe", NULL, NULL, SW_SHOWNORMAL);
            if ((int)result > 32)
            {
                std::cout << "[Loader] Lunar Client launched successfully" << std::endl;
            }
            else
            {
                std::cout << "[Loader] Failed to launch Lunar Client, please launch it manually" << std::endl;
            }
        }
        else
        {
            std::cout << "[Loader] Failed to copy JAR to overrides folder (error: " << GetLastError() << ")" << std::endl;
        }
    }
    else
    {
        std::cout << "[Loader] Failed to get username" << std::endl;
    }
    
    std::cout << "[Loader] Waiting for Lunar Client..." << std::endl;
    std::cout << "[Loader] If Lunar Client is already running, please restart it first" << std::endl;

    // Wait for Lunar Client to start
    DWORD processId = 0;
    while (processId == 0)
    {
        processId = FindLunarClientProcess();
        if (processId == 0)
        {
            Sleep(1000);
        }
    }

    // Get DLL path
    std::string dllPathStr = currentDir + "\\CrucifixDLL.dll";
    std::cout << "[Loader] DLL path: " << dllPathStr << std::endl;

    // Inject DLL
    if (InjectDLL(processId, dllPathStr))
    {
        std::cout << "[Loader] Injection successful!" << std::endl;
        std::cout << "[Loader] Press any key to exit..." << std::endl;
        std::cin.get();
        return 0;
    }
    else
    {
        std::cout << "[Loader] Injection failed!" << std::endl;
        std::cout << "[Loader] Press any key to exit..." << std::endl;
        std::cin.get();
        return 1;
    }
}

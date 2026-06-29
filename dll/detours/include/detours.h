#pragma once

// Minimal Detours stub for compilation
// In production, download Microsoft Detours from https://github.com/microsoft/Detours/releases

#include <windows.h>

#ifdef __cplusplus
extern "C" {
#endif

// Detours function stubs
LONG DetourAttach(PVOID* ppPointer, PVOID pDetour);
LONG DetourDetach(PVOID* ppPointer, PVOID pDetour);
LONG DetourUpdateThread(HANDLE hThread);
LONG DetourTransactionBegin();
LONG DetourTransactionCommit();
LONG DetourTransactionAbort();
LONG DetourAttachEx(PVOID* ppPointer, PVOID pDetour);
LONG DetourDetachEx(PVOID* ppPointer, PVOID pDetour);

PVOID DetourFindFunction(PCSTR pszModule, PCSTR pszFunction);
PVOID DetourCodeFromPointer(PVOID pPointer, PBOOL pbCode);

#ifdef __cplusplus
}
#endif

// Simple inline implementations for stub
inline LONG DetourAttach(PVOID* ppPointer, PVOID pDetour) {
    // Placeholder - in production use actual Detours library
    return 0;
}

inline LONG DetourDetach(PVOID* ppPointer, PVOID pDetour) {
    // Placeholder - in production use actual Detours library
    return 0;
}

inline LONG DetourUpdateThread(HANDLE hThread) {
    // Placeholder - in production use actual Detours library
    return 0;
}

inline LONG DetourTransactionBegin() {
    // Placeholder - in production use actual Detours library
    return 0;
}

inline LONG DetourTransactionCommit() {
    // Placeholder - in production use actual Detours library
    return 0;
}

inline LONG DetourTransactionAbort() {
    // Placeholder - in production use actual Detours library
    return 0;
}

inline LONG DetourAttachEx(PVOID* ppPointer, PVOID pDetour) {
    // Placeholder - in production use actual Detours library
    return 0;
}

inline LONG DetourDetachEx(PVOID* ppPointer, PVOID pDetour) {
    // Placeholder - in production use actual Detours library
    return 0;
}

inline PVOID DetourFindFunction(PCSTR pszModule, PCSTR pszFunction) {
    HMODULE hModule = GetModuleHandleA(pszModule);
    if (hModule == NULL) {
        hModule = LoadLibraryA(pszModule);
    }
    if (hModule == NULL) {
        return NULL;
    }
    return GetProcAddress(hModule, pszFunction);
}

inline PVOID DetourCodeFromPointer(PVOID pPointer, PBOOL pbCode) {
    *pbCode = TRUE;
    return pPointer;
}

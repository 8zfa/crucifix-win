using System;
using System.Diagnostics;
using System.IO;
using System.Runtime.InteropServices;
using System.Text;

namespace CrucifixInjector
{
    public class Injector
    {
        [DllImport("kernel32.dll")]
        private static extern IntPtr OpenProcess(int dwDesiredAccess, bool bInheritHandle, uint dwProcessId);

        [DllImport("kernel32.dll", CharSet = CharSet.Auto, SetLastError = true)]
        private static extern IntPtr GetProcAddress(IntPtr hModule, string lpProcName);

        [DllImport("kernel32.dll", SetLastError = true)]
        private static extern IntPtr GetModuleHandle(string lpModuleName);

        [DllImport("kernel32.dll")]
        private static extern IntPtr VirtualAllocEx(IntPtr hProcess, IntPtr lpAddress, UIntPtr dwSize, uint flAllocationType, uint flProtect);

        [DllImport("kernel32.dll")]
        private static extern bool WriteProcessMemory(IntPtr hProcess, IntPtr lpBaseAddress, byte[] lpBuffer, UIntPtr nSize, out UIntPtr lpNumberOfBytesWritten);

        [DllImport("kernel32.dll")]
        private static extern IntPtr CreateRemoteThread(IntPtr hProcess, IntPtr lpThreadAttributes, UIntPtr dwStackSize, IntPtr lpStartAddress, IntPtr lpParameter, uint dwCreationFlags, out IntPtr lpThreadId);

        [DllImport("kernel32.dll")]
        private static extern bool CloseHandle(IntPtr hObject);

        [DllImport("kernel32.dll")]
        private static extern bool VirtualFreeEx(IntPtr hProcess, IntPtr lpAddress, UIntPtr dwSize, uint dwFreeType);

        [DllImport("ntdll.dll")]
        private static extern IntPtr NtCreateThreadEx(out IntPtr hThread, uint DesiredAccess, IntPtr ObjectAttributes, IntPtr ProcessHandle, IntPtr lpStartAddress, IntPtr lpParameter, bool CreateSuspended, ulong StackZeroBits, ulong SizeOfStackCommit, ulong SizeOfStackReserve, IntPtr lpBytesBuffer);

        [DllImport("kernel32.dll", CharSet = CharSet.Ansi)]
        private static extern IntPtr LoadLibraryA(string lpLibFileName);

        // Delegate for LoadLibraryA
        private delegate IntPtr LoadLibraryADelegate(string lpLibFileName);

        private const int PROCESS_CREATE_THREAD = 0x0002;
        private const int PROCESS_QUERY_INFORMATION = 0x0400;
        private const int PROCESS_VM_OPERATION = 0x0008;
        private const int PROCESS_VM_WRITE = 0x0020;
        private const int PROCESS_VM_READ = 0x0010;
        private const uint MEM_COMMIT = 0x1000;
        private const uint MEM_RESERVE = 0x2000;
        private const uint PAGE_EXECUTE_READWRITE = 0x40;
        private const uint MEM_RELEASE = 0x8000;

        public static bool Inject(Process targetProcess, string dllPath)
        {
            if (!File.Exists(dllPath))
            {
                throw new FileNotFoundException("DLL file not found", dllPath);
            }

            IntPtr hProcess = OpenProcess(PROCESS_CREATE_THREAD | PROCESS_QUERY_INFORMATION | PROCESS_VM_OPERATION | PROCESS_VM_WRITE | PROCESS_VM_READ, false, (uint)targetProcess.Id);
            
            if (hProcess == IntPtr.Zero)
            {
                throw new Exception("Failed to open process. Try running as administrator.");
            }

            try
            {
                // Use ASCII encoding for LoadLibraryA
                byte[] dllPathBytes = Encoding.ASCII.GetBytes(dllPath);
                IntPtr allocMem = VirtualAllocEx(hProcess, IntPtr.Zero, (UIntPtr)((dllPathBytes.Length + 1) * Marshal.SizeOf(typeof(char))), MEM_COMMIT | MEM_RESERVE, PAGE_EXECUTE_READWRITE);
                
                if (allocMem == IntPtr.Zero)
                {
                    throw new Exception("Failed to allocate memory in target process.");
                }

                try
                {
                    WriteProcessMemory(hProcess, allocMem, dllPathBytes, (UIntPtr)((dllPathBytes.Length + 1) * Marshal.SizeOf(typeof(char))), out _);
                    
                    // Get LoadLibraryA address using delegate method
                    LoadLibraryADelegate loadLibraryDelegate = new LoadLibraryADelegate(LoadLibraryA);
                    IntPtr loadLibrary = Marshal.GetFunctionPointerForDelegate(loadLibraryDelegate);
                    
                    if (loadLibrary == IntPtr.Zero)
                    {
                        throw new Exception("Failed to get LoadLibraryA address via delegate.");
                    }

                    // Use standard CreateRemoteThread
                    IntPtr hThread = CreateRemoteThread(hProcess, IntPtr.Zero, UIntPtr.Zero, loadLibrary, allocMem, 0, out _);
                    
                    if (hThread == IntPtr.Zero)
                    {
                        throw new Exception("Failed to create remote thread. Error code: " + Marshal.GetLastWin32Error());
                    }
                    
                    CloseHandle(hThread);
                    
                    return true;
                }
                finally
                {
                    VirtualFreeEx(hProcess, allocMem, UIntPtr.Zero, MEM_RELEASE);
                }
            }
            finally
            {
                CloseHandle(hProcess);
            }
        }
    }
}

using System;
using System.Diagnostics;
using System.IO;
using System.Runtime.InteropServices;
using System.Threading;

namespace CrucifixInjector
{
    public class Program
    {
        // WinAPI imports
        [DllImport("kernel32.dll", SetLastError = true, CharSet = CharSet.Auto)]
        static extern IntPtr OpenProcess(uint dwDesiredAccess, bool bInheritHandle, int dwProcessId);

        [DllImport("kernel32.dll", SetLastError = true, ExactSpelling = true)]
        static extern IntPtr VirtualAllocEx(IntPtr hProcess, IntPtr lpAddress, uint dwSize, uint flAllocationType, uint flProtect);

        [DllImport("kernel32.dll", SetLastError = true)]
        static extern bool WriteProcessMemory(IntPtr hProcess, IntPtr lpBaseAddress, byte[] lpBuffer, uint nSize, out IntPtr lpNumberOfBytesWritten);

        [DllImport("kernel32.dll", SetLastError = true)]
        static extern IntPtr CreateRemoteThread(IntPtr hProcess, IntPtr lpThreadAttributes, uint dwStackSize, IntPtr lpStartAddress, IntPtr lpParameter, uint dwCreationFlags, out IntPtr lpThreadId);

        [DllImport("kernel32.dll", SetLastError = true)]
        static extern IntPtr GetProcAddress(IntPtr hModule, string lpProcName);

        [DllImport("kernel32.dll", SetLastError = true, CharSet = CharSet.Auto)]
        static extern IntPtr GetModuleHandle(string lpModuleName);

        [DllImport("kernel32.dll", SetLastError = true)]
        static extern bool CloseHandle(IntPtr hObject);

        [DllImport("kernel32.dll", SetLastError = true)]
        static extern uint GetLastError();

        [DllImport("kernel32.dll", SetLastError = true)]
        static extern bool VirtualFreeEx(IntPtr hProcess, IntPtr lpAddress, int dwSize, uint dwFreeType);

        [DllImport("kernel32.dll", SetLastError = true)]
        static extern uint WaitForSingleObject(IntPtr hHandle, uint dwMilliseconds);

        // Constants
        const uint PROCESS_ALL_ACCESS = 0x1F0FFF;
        const uint PROCESS_CREATE_THREAD = 0x0002;
        const uint PROCESS_QUERY_INFORMATION = 0x0400;
        const uint PROCESS_VM_OPERATION = 0x0008;
        const uint PROCESS_VM_WRITE = 0x0020;
        const uint PROCESS_VM_READ = 0x0010;
        const uint MEM_COMMIT = 0x1000;
        const uint MEM_RESERVE = 0x2000;
        const uint MEM_RELEASE = 0x8000;
        const uint PAGE_READWRITE = 0x04;
        const uint PAGE_EXECUTE_READWRITE = 0x40;
        const uint INFINITE = 0xFFFFFFFF;
        const uint WAIT_OBJECT_0 = 0x00000000;

        static void Main(string[] args)
        {
            Console.Title = "CRUCIFIX Injector v1.1";
            Console.ForegroundColor = ConsoleColor.Magenta;
            Console.WriteLine("╔══════════════════════════════════════════════════════════╗");
            Console.WriteLine("║                   CRUCIFIX INJECTOR v1.1               ║");
            Console.WriteLine("║                 Enhanced Crash Prevention             ║");
            Console.WriteLine("╚══════════════════════════════════════════════════════════╝");
            Console.ResetColor();
            Console.WriteLine();

            try
            {
                // Check for administrator privileges
                if (!IsAdministrator())
                {
                    Console.ForegroundColor = ConsoleColor.Yellow;
                    Console.WriteLine("[WARNING] Not running as Administrator!");
                    Console.WriteLine("[WARNING] Injection may fail without admin privileges.");
                    Console.WriteLine("[WARNING] Please restart as Administrator if injection fails.");
                    Console.ResetColor();
                    Console.WriteLine();
                }

                // Get DLL path
                string dllPath = Path.Combine(AppDomain.CurrentDomain.BaseDirectory, "CrucifixDLL.dll");
                
                if (!File.Exists(dllPath))
                {
                    Console.ForegroundColor = ConsoleColor.Red;
                    Console.WriteLine("[ERROR] CrucifixDLL.dll not found!");
                    Console.WriteLine($"Looking for: {dllPath}");
                    Console.WriteLine();
                    Console.WriteLine("Please ensure:");
                    Console.WriteLine("1. CrucifixDLL.dll is in the same directory as the injector");
                    Console.WriteLine("2. The DLL was built successfully");
                    Console.ResetColor();
                    Console.WriteLine("\nPress any key to exit...");
                    Console.ReadKey();
                    return;
                }

                Console.WriteLine($"[*] DLL found: {dllPath}");
                Console.WriteLine($"[*] DLL size: {new FileInfo(dllPath).Length} bytes");

                // Validate DLL
                if (!ValidateDLL(dllPath))
                {
                    Console.ForegroundColor = ConsoleColor.Red;
                    Console.WriteLine("[ERROR] DLL validation failed!");
                    Console.WriteLine("[ERROR] The DLL may be corrupted or incompatible.");
                    Console.ResetColor();
                    Console.WriteLine("\nPress any key to exit...");
                    Console.ReadKey();
                    return;
                }

                // Find Lunar Client process
                Console.WriteLine("[*] Searching for Lunar Client...");
                Process target = FindLunarClient();

                if (target == null)
                {
                    Console.ForegroundColor = ConsoleColor.Red;
                    Console.WriteLine("[ERROR] Lunar Client not found!");
                    Console.WriteLine("Make sure Lunar Client is running.");
                    Console.ResetColor();
                    Console.WriteLine("\nPress any key to exit...");
                    Console.ReadKey();
                    return;
                }

                int pid = target.Id;
                Console.WriteLine($"[+] Found Lunar Client (PID: {pid})");
                Console.WriteLine($"[+] Window Title: {target.MainWindowTitle}");
                Console.WriteLine($"[+] Process Name: {target.ProcessName}");

                // Check if process is 64-bit
                if (!Environment.Is64BitProcess)
                {
                    Console.ForegroundColor = ConsoleColor.Yellow;
                    Console.WriteLine("[WARNING] Injector is running in 32-bit mode");
                    Console.WriteLine("[WARNING] Lunar Client is likely 64-bit");
                    Console.WriteLine("[WARNING] This may cause injection to fail");
                    Console.WriteLine("[WARNING] Please use a 64-bit build of the injector");
                    Console.ResetColor();
                    Console.WriteLine();
                }

                // Inject DLL with retry mechanism
                Console.WriteLine("[*] Attempting to inject DLL...");
                bool success = false;
                int maxRetries = 3;
                
                for (int i = 0; i < maxRetries; i++)
                {
                    if (i > 0)
                    {
                        Console.WriteLine($"[*] Retry {i + 1}/{maxRetries}...");
                        Thread.Sleep(1000);
                    }
                    
                    success = InjectDLL(dllPath, pid);
                    if (success) break;
                }

                if (success)
                {
                    Console.ForegroundColor = ConsoleColor.Green;
                    Console.WriteLine("[SUCCESS] DLL injected successfully!");
                    Console.ResetColor();
                    Console.WriteLine("\n[+] Press RSHIFT in-game to open the ClickGUI");
                    Console.WriteLine("[+] Check logs for initialization status");
                }
                else
                {
                    Console.ForegroundColor = ConsoleColor.Red;
                    Console.WriteLine("[ERROR] Injection failed after " + maxRetries + " attempts!");
                    Console.ResetColor();
                    Console.WriteLine("\nTroubleshooting:");
                    Console.WriteLine("1. Run the injector as Administrator");
                    Console.WriteLine("2. Make sure Lunar Client is running");
                    Console.WriteLine("3. Check if antivirus is blocking the injection");
                    Console.WriteLine("4. Verify the DLL is compatible with your Lunar Client version");
                }
            }
            catch (Exception ex)
            {
                Console.ForegroundColor = ConsoleColor.Red;
                Console.WriteLine("[ERROR] Unexpected error: " + ex.Message);
                Console.WriteLine("[ERROR] Stack: " + ex.StackTrace);
                Console.ResetColor();
            }

            Console.WriteLine("\nPress any key to exit...");
            Console.ReadKey();
        }

        static bool IsAdministrator()
        {
            try
            {
                using (var identity = System.Security.Principal.WindowsIdentity.GetCurrent())
                {
                    var principal = new System.Security.Principal.WindowsPrincipal(identity);
                    return principal.IsInRole(System.Security.Principal.WindowsBuiltInRole.Administrator);
                }
            }
            catch
            {
                return false;
            }
        }

        static bool ValidateDLL(string dllPath)
        {
            try
            {
                // Check if file is a valid PE file
                byte[] header = new byte[2];
                using (var fs = new FileStream(dllPath, FileMode.Open, FileAccess.Read))
                {
                    fs.Read(header, 0, 2);
                }
                
                // Check for MZ header
                if (header[0] != 0x4D || header[1] != 0x5A)
                {
                    Console.WriteLine("[VALIDATION] Invalid PE header (not MZ)");
                    return false;
                }

                // Check file size
                FileInfo fi = new FileInfo(dllPath);
                if (fi.Length < 1024)
                {
                    Console.WriteLine("[VALIDATION] DLL too small, possibly corrupted");
                    return false;
                }

                Console.WriteLine("[VALIDATION] DLL appears valid");
                return true;
            }
            catch (Exception ex)
            {
                Console.WriteLine("[VALIDATION] Error validating DLL: " + ex.Message);
                return false;
            }
        }

        static Process FindLunarClient()
        {
            Process[] processes = Process.GetProcessesByName("javaw");

            if (processes.Length == 0)
            {
                return null;
            }

            Process target = null;
            foreach (Process p in processes)
            {
                try
                {
                    // Check if it's Lunar Client by window title or modules
                    if (!string.IsNullOrEmpty(p.MainWindowTitle))
                    {
                        if (p.MainWindowTitle.Contains("Minecraft") || 
                            p.MainWindowTitle.Contains("Lunar") ||
                            p.MainWindowTitle.Contains("lunar"))
                        {
                            target = p;
                            break;
                        }
                    }
                }
                catch { }
            }

            if (target == null && processes.Length > 0)
            {
                // Fallback: take first javaw process
                target = processes[0];
            }

            return target;
        }

        static bool InjectDLL(string dllPath, int pid)
        {
            IntPtr hProcess = IntPtr.Zero;
            IntPtr pDllPath = IntPtr.Zero;
            IntPtr hThread = IntPtr.Zero;
            IntPtr hKernel32 = IntPtr.Zero;

            try
            {
                // Step 1: Open process with minimal required rights
                uint processAccess = PROCESS_CREATE_THREAD | PROCESS_QUERY_INFORMATION | 
                                   PROCESS_VM_OPERATION | PROCESS_VM_WRITE | PROCESS_VM_READ;
                
                hProcess = OpenProcess(processAccess, false, pid);
                if (hProcess == IntPtr.Zero)
                {
                    uint error = GetLastError();
                    Console.WriteLine($"[!] OpenProcess failed! Error: {error}");
                    Console.WriteLine("[!] Run the injector as Administrator!");
                    return false;
                }

                Console.WriteLine("[+] Process opened successfully");

                // Step 2: Get LoadLibraryA address from kernel32.dll (already loaded)
                hKernel32 = GetModuleHandle("kernel32.dll");
                if (hKernel32 == IntPtr.Zero)
                {
                    uint error = GetLastError();
                    Console.WriteLine($"[!] Failed to get kernel32.dll handle! Error: {error}");
                    return false;
                }

                IntPtr pLoadLibrary = GetProcAddress(hKernel32, "LoadLibraryA");
                if (pLoadLibrary == IntPtr.Zero)
                {
                    Console.WriteLine("[!] Failed to find LoadLibraryA");
                    return false;
                }

                Console.WriteLine("[+] LoadLibraryA address found");

                // Step 3: Allocate memory for DLL path
                byte[] dllBytes = System.Text.Encoding.ASCII.GetBytes(dllPath + "\0");
                pDllPath = VirtualAllocEx(hProcess, IntPtr.Zero, (uint)dllBytes.Length, 
                                          MEM_COMMIT | MEM_RESERVE, PAGE_READWRITE);
                if (pDllPath == IntPtr.Zero)
                {
                    uint error = GetLastError();
                    Console.WriteLine($"[!] VirtualAllocEx failed! Error: {error}");
                    return false;
                }

                Console.WriteLine("[+] Memory allocated for DLL path");

                // Step 4: Write DLL path
                IntPtr bytesWritten;
                bool writeSuccess = WriteProcessMemory(hProcess, pDllPath, dllBytes, 
                                                      (uint)dllBytes.Length, out bytesWritten);
                if (!writeSuccess)
                {
                    uint error = GetLastError();
                    Console.WriteLine($"[!] WriteProcessMemory failed! Error: {error}");
                    return false;
                }

                Console.WriteLine("[+] DLL path written to process memory");

                // Step 5: Create remote thread
                IntPtr threadId;
                hThread = CreateRemoteThread(hProcess, IntPtr.Zero, 0, pLoadLibrary, 
                                              pDllPath, 0, out threadId);
                if (hThread == IntPtr.Zero)
                {
                    uint error = GetLastError();
                    Console.WriteLine($"[!] CreateRemoteThread failed! Error: {error}");
                    return false;
                }

                Console.WriteLine("[+] Remote thread created");

                // Step 6: Wait for thread to complete
                uint waitResult = WaitForSingleObject(hThread, 5000); // 5 second timeout
                if (waitResult != WAIT_OBJECT_0)
                {
                    Console.WriteLine("[!] Thread did not complete within timeout");
                    // Don't return false - DLL may still load
                }

                Console.WriteLine("[+] Thread execution completed");

                // Step 7: Clean up
                if (pDllPath != IntPtr.Zero)
                {
                    VirtualFreeEx(hProcess, pDllPath, 0, MEM_RELEASE);
                }

                if (hThread != IntPtr.Zero)
                {
                    CloseHandle(hThread);
                }

                if (hProcess != IntPtr.Zero)
                {
                    CloseHandle(hProcess);
                }

                Console.WriteLine("[+] Cleanup completed");
                return true;

            }
            catch (Exception ex)
            {
                Console.WriteLine($"[!] InjectDLL exception: {ex.Message}");
                
                // Cleanup on error
                if (pDllPath != IntPtr.Zero)
                {
                    try { VirtualFreeEx(hProcess, pDllPath, 0, MEM_RELEASE); } catch { }
                }
                if (hThread != IntPtr.Zero)
                {
                    try { CloseHandle(hThread); } catch { }
                }
                if (hProcess != IntPtr.Zero)
                {
                    try { CloseHandle(hProcess); } catch { }
                }
                
                return false;
            }
        }
    }
}

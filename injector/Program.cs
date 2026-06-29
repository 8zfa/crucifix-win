using System;
using System.Diagnostics;
using System.IO;
using System.Runtime.InteropServices;
using System.Threading;
using System.Windows.Forms;

namespace CrucifixInjector
{
    public class Program
    {
        // WinAPI imports
        [DllImport("kernel32.dll", SetLastError = true)]
        static extern IntPtr OpenProcess(uint dwDesiredAccess, bool bInheritHandle, int dwProcessId);

        [DllImport("kernel32.dll", SetLastError = true)]
        static extern IntPtr VirtualAllocEx(IntPtr hProcess, IntPtr lpAddress, uint dwSize, uint flAllocationType, uint flProtect);

        [DllImport("kernel32.dll", SetLastError = true)]
        static extern bool WriteProcessMemory(IntPtr hProcess, IntPtr lpBaseAddress, byte[] lpBuffer, uint nSize, out IntPtr lpNumberOfBytesWritten);

        [DllImport("kernel32.dll", SetLastError = true)]
        static extern IntPtr CreateRemoteThread(IntPtr hProcess, IntPtr lpThreadAttributes, uint dwStackSize, IntPtr lpStartAddress, IntPtr lpParameter, uint dwCreationFlags, IntPtr lpThreadId);

        [DllImport("kernel32.dll", SetLastError = true)]
        static extern IntPtr GetProcAddress(IntPtr hModule, string lpProcName);

        [DllImport("kernel32.dll", SetLastError = true)]
        static extern IntPtr LoadLibraryA(string lpFileName);

        [DllImport("kernel32.dll", SetLastError = true)]
        static extern bool CloseHandle(IntPtr hObject);

        [DllImport("kernel32.dll", SetLastError = true)]
        static extern uint GetLastError();

        // Constants
        const uint PROCESS_ALL_ACCESS = 0x1F0FFF;
        const uint MEM_COMMIT = 0x1000;
        const uint MEM_RESERVE = 0x2000;
        const uint PAGE_READWRITE = 0x04;
        const uint PAGE_EXECUTE_READWRITE = 0x40;

        static void Main(string[] args)
        {
            Console.Title = "CRUCIFIX Injector v1.0";
            Console.ForegroundColor = ConsoleColor.Magenta;
            Console.WriteLine("╔══════════════════════════════════════════════════════════╗");
            Console.WriteLine("║                   CRUCIFIX INJECTOR v1.0               ║");
            Console.WriteLine("╚══════════════════════════════════════════════════════════╝");
            Console.ResetColor();
            Console.WriteLine();

            try
            {
                // Get DLL path
                string dllPath = Path.Combine(AppDomain.CurrentDomain.BaseDirectory, "CrucifixDLL.dll");
                
                if (!File.Exists(dllPath))
                {
                    Console.ForegroundColor = ConsoleColor.Red;
                    Console.WriteLine("[ERROR] CrucifixDLL.dll not found!");
                    Console.WriteLine($"Looking for: {dllPath}");
                    Console.ResetColor();
                    Console.WriteLine("\nPress any key to exit...");
                    Console.ReadKey();
                    return;
                }

                Console.WriteLine($"[*] DLL found: {dllPath}");

                // Find Lunar Client process
                Console.WriteLine("[*] Searching for Lunar Client...");
                Process[] processes = Process.GetProcessesByName("javaw");

                if (processes.Length == 0)
                {
                    Console.ForegroundColor = ConsoleColor.Red;
                    Console.WriteLine("[ERROR] Lunar Client not found!");
                    Console.WriteLine("Make sure Lunar Client is running.");
                    Console.ResetColor();
                    Console.WriteLine("\nPress any key to exit...");
                    Console.ReadKey();
                    return;
                }

                Process target = null;
                foreach (Process p in processes)
                {
                    try
                    {
                        // Check if it's Lunar Client by window title or modules
                        if (p.MainWindowTitle.Contains("Minecraft") || p.MainWindowTitle.Contains("Lunar"))
                        {
                            target = p;
                            break;
                        }
                    }
                    catch { }
                }

                if (target == null)
                {
                    // Fallback: take first javaw process
                    target = processes[0];
                }

                int pid = target.Id;
                Console.WriteLine($"[+] Found Lunar Client (PID: {pid})");
                Console.WriteLine($"[+] Window Title: {target.MainWindowTitle}");

                // Inject DLL
                Console.WriteLine("[*] Attempting to inject DLL...");
                bool success = InjectDLL(dllPath, pid);

                if (success)
                {
                    Console.ForegroundColor = ConsoleColor.Green;
                    Console.WriteLine("[SUCCESS] DLL injected successfully!");
                    Console.ResetColor();
                    Console.WriteLine("\n[+] Press RSHIFT in-game to open the ClickGUI");
                }
                else
                {
                    Console.ForegroundColor = ConsoleColor.Red;
                    Console.WriteLine("[ERROR] Injection failed!");
                    Console.ResetColor();
                }
            }
            catch (Exception ex)
            {
                Console.ForegroundColor = ConsoleColor.Red;
                Console.WriteLine($"[ERROR] Unexpected error: {ex.Message}");
                Console.WriteLine($"Stack: {ex.StackTrace}");
                Console.ResetColor();
            }

            Console.WriteLine("\nPress any key to exit...");
            Console.ReadKey();
        }

        static bool InjectDLL(string dllPath, int pid)
        {
            try
            {
                // Step 1: Open process
                IntPtr hProcess = OpenProcess(PROCESS_ALL_ACCESS, false, pid);
                if (hProcess == IntPtr.Zero)
                {
                    uint error = GetLastError();
                    Console.WriteLine($"[!] OpenProcess failed! Error: {error}");
                    Console.WriteLine("[!] Run the injector as Administrator!");
                    return false;
                }

                // Step 2: Get LoadLibraryA address
                IntPtr hKernel32 = LoadLibraryA("kernel32.dll");
                if (hKernel32 == IntPtr.Zero)
                {
                    Console.WriteLine("[!] Failed to load kernel32.dll");
                    return false;
                }

                IntPtr pLoadLibrary = GetProcAddress(hKernel32, "LoadLibraryA");
                if (pLoadLibrary == IntPtr.Zero)
                {
                    Console.WriteLine("[!] Failed to find LoadLibraryA");
                    return false;
                }

                // Step 3: Allocate memory for DLL path
                byte[] dllBytes = System.Text.Encoding.ASCII.GetBytes(dllPath + "\0");
                IntPtr pDllPath = VirtualAllocEx(hProcess, IntPtr.Zero, (uint)dllBytes.Length, MEM_COMMIT | MEM_RESERVE, PAGE_READWRITE);
                if (pDllPath == IntPtr.Zero)
                {
                    Console.WriteLine("[!] VirtualAllocEx failed!");
                    return false;
                }

                // Step 4: Write DLL path
                IntPtr bytesWritten;
                bool writeSuccess = WriteProcessMemory(hProcess, pDllPath, dllBytes, (uint)dllBytes.Length, out bytesWritten);
                if (!writeSuccess)
                {
                    Console.WriteLine("[!] WriteProcessMemory failed!");
                    return false;
                }

                // Step 5: Create remote thread
                IntPtr hThread = CreateRemoteThread(hProcess, IntPtr.Zero, 0, pLoadLibrary, pDllPath, 0, IntPtr.Zero);
                if (hThread == IntPtr.Zero)
                {
                    Console.WriteLine("[!] CreateRemoteThread failed!");
                    return false;
                }

                // Wait a bit for DLL to load
                Thread.Sleep(1000);

                // Close handles
                CloseHandle(hThread);
                CloseHandle(hProcess);

                return true;
            }
            catch (Exception ex)
            {
                Console.WriteLine($"[!] InjectDLL exception: {ex.Message}");
                return false;
            }
        }
    }
}

using System;
using System.Diagnostics;
using System.Linq;

namespace CrucifixInjector
{
    public class ProcessDetector
    {
        public static Process FindMinecraftProcess()
        {
            // Try to find Lunar Client first
            var lunarProcess = Process.GetProcessesByName("LunarClient").FirstOrDefault();
            if (lunarProcess != null)
                return lunarProcess;

            // Try vanilla Minecraft
            var minecraftProcess = Process.GetProcessesByName("Minecraft").FirstOrDefault();
            if (minecraftProcess != null)
                return minecraftProcess;

            // Try javaw process (common for Minecraft)
            var javawProcesses = Process.GetProcessesByName("javaw");
            if (javawProcesses.Length > 0)
                return javawProcesses[0];

            return null;
        }

        public static bool IsLunarClient(Process process)
        {
            return process != null && (process.ProcessName.Equals("LunarClient", StringComparison.OrdinalIgnoreCase) ||
                                      process.MainModule?.FileName.Contains("LunarClient") == true);
        }
    }
}

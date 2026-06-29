# CRUCIFIX.WIN - Setup and Build Guide

## Prerequisites Installation

### Option 1: Visual Studio (Recommended - Easiest)
1. Download Visual Studio Community 2022: https://visualstudio.microsoft.com/downloads/
2. Run installer and select these workloads:
   - **Desktop development with C++**
   - **.NET desktop development**
3. This installs MSBuild, CMake, C++ compiler, and .NET Framework

### Option 2: Individual Tools
1. **MSBuild** (for C#)
   - Install Visual Studio Build Tools: https://visualstudio.microsoft.com/visual-build-tools/
   - Or .NET Framework 4.8 Developer Pack

2. **CMake** (for C++)
   - Download: https://cmake.org/download/
   - Install and check "Add CMake to system PATH"

3. **Gradle** (for Java)
   - Download: https://gradle.org/install/
   - Extract and add bin folder to PATH
   - Or use the provided javac script (no Gradle needed)

4. **JDK 8** (for Java)
   - Download: https://adoptium.net/temurin/releases/?version=8
   - Install and set JAVA_HOME environment variable

## Quick Start (After Installing Tools)

### Automatic Build
```batch
cd C:\Users\raw\Desktop\crucifix.win
BUILD.bat
```

### Manual Build Steps

#### 1. Build Java Payload
**With Gradle:**
```batch
cd java
gradlew build
```

**With javac (no Gradle needed):**
```batch
cd java
build_javac.bat
```

#### 2. Build C# Injector
```batch
cd injector
msbuild CrucifixInjector.csproj /p:Configuration=Release
```

#### 3. Build C++ DLL
```batch
cd dll
cmake -B build -A x64
cmake --build build --config Release
```

## Project Structure After Build

```
crucifix.win/
├── injector/bin/Release/
│   └── CrucifixInjector.exe          # Main injector
├── dll/build/Release/
│   └── CrucifixDLL.dll                # Injected DLL
├── java/build/
│   └── CrucifixPayload.jar            # Java payload
└── Crucifix/                          # Runtime configs (auto-created)
    └── configs/
        └── default.json
```

## Usage

1. **Copy files to output directory:**
   - `injector/bin/Release/CrucifixInjector.exe`
   - `dll/build/Release/CrucifixDLL.dll`
   - `java/build/CrucifixPayload.jar`

2. **Run the injector:**
   ```
   CrucifixInjector.exe
   ```

3. **Launch Minecraft 1.8.9**
   - Vanilla Minecraft or Lunar Client

4. **Inject:**
   - Wait for "Minecraft detected" status
   - Click "Inject" button

5. **Use the client:**
   - Press Right Shift (RSHIFT) to open ClickGUI
   - Configure modules and settings
   - HUD elements display automatically

## Troubleshooting

### "msbuild not found"
- Install Visual Studio Build Tools or Visual Studio Community
- Or add MSBuild to PATH: `C:\Program Files\Microsoft Visual Studio\2022\Community\MSBuild\Current\Bin`

### "cmake not found"
- Install CMake from cmake.org
- Restart command prompt after installation

### "gradle not found"
- Use the provided `build_javac.bat` script instead
- Or install Gradle and add to PATH

### "JAVA_HOME not set"
- Install JDK 8
- Set environment variable: `setx JAVA_HOME "C:\Program Files\Eclipse Adoptium\jdk-8.0.###-hotspot"`

### Injection fails
- Run injector as Administrator
- Ensure Minecraft is running before injecting
- Check antivirus isn't blocking the DLL

## Development Notes

### Adding New Modules
1. Create module class in appropriate category folder
2. Extend `Module` base class
3. Add settings in constructor
4. Register in `ModuleManager.initializeModules()`

### Adding New Themes
1. Create class implementing `Theme` interface
2. Implement all color methods
3. Register in ClickGUI theme selector

### Adding New HUD Components
1. Create class extending `HUDComponent`
2. Implement update() and render() methods
3. Register in `HUDManager.initializeComponents()`

## Compatibility

- **Minecraft Version:** 1.8.9 only
- **Java Version:** 8 required
- **Operating System:** Windows 10/11
- **Clients:** Vanilla, Lunar Client, Badlion Client, PVP Lounge

## Support

For issues or questions, refer to the code comments and README.md.

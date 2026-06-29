# CRUCIFIX.WIN

A complete, polished, and production-ready Minecraft 1.8.9 injectable client.

## Features

### Injector
- Clean, minimalistic injector .exe with Vape v4-inspired dark theme
- Automatic process detection for Lunar Client and vanilla Minecraft
- Smooth animations and status indicators
- Error handling with retry functionality

### DLL Payload
- JNI integration for Java payload loading
- Support for Lunar Client's custom classloader
- ASM bytecode transformation for method hooks
- Thread-safe JNI calls

### Java Payload

#### Core Systems
- **EventBus**: Annotation-driven event system with priority support
- **ModuleManager**: Registry and state management for all modules
- **ConfigManager**: JSON serialization with auto-save and profile support
- **AnimationEngine**: Spring physics and multiple easing functions

#### Modules

**Combat (9 modules)**
- KillAura, AimAssist, Velocity, Reach, HitBoxes, AutoClicker, WTap, RodAura, BowAim

**Movement (10 modules)**
- Speed, Fly, BHop, Strafe, Step, LongJump, NoSlow, Sprint, WaterWalk, FastLadder

**Render (9 modules)**
- ESP, Chams, Nametags, FullBright, XRay, Tracers, Glow, NoHurtCam, CameraClip

**Player (8 modules)**
- AutoEat, AutoSoup, AutoPearl, FastPlace, NoFall, AntiFire, AutoRespawn, AutoGapple

**Misc (7 modules)**
- AntiBot, AutoGG, AutoTip, ChatFilter, MiddleClickPearl, TimeChanger, ScoreboardCleaner

**Exploit (8 modules)**
- AntiVelocity, NoRotate, Disabler, Timer, Phase, FastUse, Scaffold, Tower

#### GUI
- Category-based panels with drag support
- Spring animations for toggles and transitions
- Multiple themes: CrucifixDark, CrucifixLight, BloodRose, VoidWalker, Nebula
- Animated toggle switches with physics-based movement

#### HUD
- Watermark with gradient text
- Arraylist of active modules
- Keystrokes visualizer
- ArmorStatus with durability
- PotionStatus with timers
- Direction compass

## Building

### Prerequisites
- Windows 10/11
- .NET Framework 4.8
- Visual Studio 2019 or later (for C++ DLL)
- JDK 8
- Gradle 7.0+

### Build Steps

#### 1. Build Injector (C#)
```bash
cd injector
msbuild CrucifixInjector.csproj /p:Configuration=Release
```

#### 2. Build DLL Payload (C++)
```bash
cd dll
cmake -B build -A x64
cmake --build build --config Release
```

#### 3. Build Java Payload
```bash
cd java
gradlew build
```

#### 4. Package
Copy the built files:
- `injector/bin/Release/CrucifixInjector.exe` → `CrucifixInjector.exe`
- `dll/build/Release/CrucifixDLL.dll` → `CrucifixDLL.dll`
- `java/build/libs/java-1.0.0.jar` → Embed in DLL or place alongside

## Usage

1. Run `CrucifixInjector.exe`
2. Launch Minecraft 1.8.9 (vanilla or Lunar Client)
3. Wait for the injector to detect the process
4. Click "Inject"
5. Open ClickGUI with Right Shift (RSHIFT)
6. Configure modules and settings

## Configuration

Configs are stored in `./Crucifix/configs/` as JSON files.
- Auto-save every 30 seconds
- Support for multiple profiles
- Import/Export via clipboard

## Compatibility

- Minecraft 1.8.9 (vanilla via Java agent)
- Lunar Client 1.8.9 (via DLL injection)
- Badlion Client 1.8.9 (via Java agent)
- PVP Lounge 1.8.9

## Technical Details

### Package Structure
```
com.crucifix.client/
├── Crucifix.java (entry point)
├── core/ (EventBus, ModuleManager, ConfigManager, AnimationEngine)
├── events/ (Event, UpdateEvent, RenderEvent, KeyEvent, PacketEvent)
├── modules/ (Module, Category, Setting, all module implementations)
├── gui/ (ClickGUI, components, animations, themes)
├── hud/ (HUDManager, HUDComponent, all HUD implementations)
└── utils/ (RenderUtils, RotationUtils, InventoryUtils, etc.)
```

### Injection Mechanism
- **Vanilla Minecraft**: Java Instrumentation API (-javaagent)
- **Lunar Client**: JNI DLL injection with CreateRemoteThread + LoadLibraryA
- **Method Hooks**: ASM bytecode transformation
- **Field Access**: Reflection

## License

This project is for educational purposes only.

## Credits

CRUCIFIX.WIN - Clean, Minimalistic, Powerful

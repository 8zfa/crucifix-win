cmake_minimum_required(VERSION 3.15)
project(CrucifixDLL)

set(CMAKE_CXX_STANDARD 17)
set(CMAKE_CXX_STANDARD_REQUIRED ON)

# Set x64 architecture
set(CMAKE_GENERATOR_PLATFORM x64)

# Add compiler flags for exception handling and static runtime
if(MSVC)
    add_compile_options(/EHa)  # Structured exception handling
    add_compile_options(/MT)   # Static runtime library
endif()

# Add JNI include directories
set(JAVA_HOME "C:/Program Files/Java/jdk-17")
if(EXISTS "${JAVA_HOME}")
    include_directories("${JAVA_HOME}/include")
    include_directories("${JAVA_HOME}/include/win32")
    link_directories("${JAVA_HOME}/lib")
else()
    message(WARNING "JAVA_HOME not found at ${JAVA_HOME}")
endif()

# Add Detours include directory (stub only, no linking)
set(DETOURS_DIR "${CMAKE_SOURCE_DIR}/detours")
if(EXISTS "${DETOURS_DIR}")
    include_directories("${DETOURS_DIR}/include")
else()
    message(WARNING "Detours not found at ${DETOURS_DIR}")
endif()

# Add ImGui include directory
set(IMGUI_DIR "${CMAKE_SOURCE_DIR}/imgui")
if(EXISTS "${IMGUI_DIR}")
    include_directories("${IMGUI_DIR}")
    include_directories("${IMGUI_DIR}/backends")
else()
    message(WARNING "ImGui not found at ${IMGUI_DIR}")
endif()

# Add MinHook include directory
set(MINHOOK_DIR "${CMAKE_SOURCE_DIR}/minhook")
if(EXISTS "${MINHOOK_DIR}")
    include_directories("${MINHOOK_DIR}/include")
else()
    message(WARNING "MinHook not found at ${MINHOOK_DIR}")
endif()

# Add source files
add_library(CrucifixDLL SHARED
    dllmain.cpp
    JNIHelper.cpp
    JVMTIHelper.cpp
    HookManager.cpp
    ResourceLoader.cpp
    keyboard_hook.cpp
    hotspot/hotspot.cpp
    modules/ModuleManager.cpp
    gui/ClickGUI.cpp
    gui/hooks/wglSwapBuffers.cpp
    imgui/imgui.cpp
    imgui/imgui_draw.cpp
    imgui/imgui_widgets.cpp
    imgui/imgui_tables.cpp
    imgui/backends/imgui_impl_win32.cpp
    imgui/backends/imgui_impl_opengl2.cpp
    minhook/src/buffer.c
    minhook/src/hook.c
    minhook/src/trampoline.c
    minhook/src/hde/hde32.c
    minhook/src/hde/hde64.c
)

# Link libraries
target_link_libraries(CrucifixDLL jvm.lib kernel32.lib user32.lib opengl32.lib gdi32.lib)

# Set output directory
set_target_properties(CrucifixDLL PROPERTIES
    RUNTIME_OUTPUT_DIRECTORY "${CMAKE_BINARY_DIR}/Release"
    LIBRARY_OUTPUT_DIRECTORY "${CMAKE_BINARY_DIR}/Release"
    ARCHIVE_OUTPUT_DIRECTORY "${CMAKE_BINARY_DIR}/Release"
)

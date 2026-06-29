cmake_minimum_required(VERSION 3.15)
project(CrucifixPayload)

set(CMAKE_CXX_STANDARD 14)
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

# Add source files
add_library(CrucifixPayload SHARED
    dllmain.cpp
)

# Link libraries
target_link_libraries(CrucifixPayload jvm.lib kernel32.lib user32.lib)

# Set output directory
set_target_properties(CrucifixPayload PROPERTIES
    RUNTIME_OUTPUT_DIRECTORY "${CMAKE_BINARY_DIR}/Release"
    LIBRARY_OUTPUT_DIRECTORY "${CMAKE_BINARY_DIR}/Release"
    ARCHIVE_OUTPUT_DIRECTORY "${CMAKE_BINARY_DIR}/Release"
)

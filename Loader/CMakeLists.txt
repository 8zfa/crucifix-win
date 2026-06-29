cmake_minimum_required(VERSION 3.15)
project(CrucifixLoader)

set(CMAKE_CXX_STANDARD 14)
set(CMAKE_CXX_STANDARD_REQUIRED ON)

# Set x64 architecture
set(CMAKE_GENERATOR_PLATFORM x64)

# Add compiler flags
if(MSVC)
    add_compile_options(/EHa)  # Structured exception handling
    add_compile_options(/MT)   # Static runtime library
endif()

# Add source files
add_executable(CrucifixLoader
    main.cpp
)

# Link libraries
target_link_libraries(CrucifixLoader kernel32.lib user32.lib)

# Set output directory
set_target_properties(CrucifixLoader PROPERTIES
    RUNTIME_OUTPUT_DIRECTORY "${CMAKE_BINARY_DIR}/Release"
)

@echo off
echo Building CRUCIFIX.WIN...

echo.
echo [1/3] Building Injector (C#)...
cd injector
msbuild CrucifixInjector.csproj /p:Configuration=Release /p:Platform=x64
if %errorlevel% neq 0 (
    echo Failed to build Injector

)
cd ..

echo.
echo [2/3] Building DLL Payload (C++)...
cd dll
if not exist build mkdir build
cd build
cmake .. -A x64
cmake --build . --config Release
if %errorlevel% neq 0 (
    echo Failed to build DLL
    cd ..\..
    exit /b 1
)
cd ..\..

echo.
echo [3/3] Building Java Payload...
cd java
call gradlew build
if %errorlevel% neq 0 (
    echo Failed to build Java payload
    cd ..
    exit /b 1
)
cd ..

echo.
echo ============================================
echo Build Complete!
echo ============================================
echo.
echo Output files:
echo - injector\bin\Release\CrucifixInjector.exe
echo - dll\build\Release\CrucifixDLL.dll
echo - java\build\libs\java-1.0.0.jar
echo.
echo Copy these files to your output directory.
echo.

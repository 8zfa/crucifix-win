@echo off
echo Building Java payload with javac (Java 17)...

set SRC_DIR=src\main\java
set BUILD_DIR=build\classes
set JAR_FILE=build\CrucifixPayload.jar

echo Creating build directories...
if not exist %BUILD_DIR% mkdir %BUILD_DIR%
if not exist build mkdir build

echo Compiling Java sources with Java 17...
"C:\Program Files\Java\jdk-17\bin\javac.exe" -d %BUILD_DIR% -cp %BUILD_DIR% %SRC_DIR%\com\crucifix\client\Crucifix.java %SRC_DIR%\com\crucifix\client\core\*.java %SRC_DIR%\com\crucifix\client\events\*.java %SRC_DIR%\com\crucifix\client\modules\*.java %SRC_DIR%\com\crucifix\client\modules\combat\*.java %SRC_DIR%\com\crucifix\client\modules\movement\*.java %SRC_DIR%\com\crucifix\client\modules\render\*.java %SRC_DIR%\com\crucifix\client\modules\player\*.java %SRC_DIR%\com\crucifix\client\modules\misc\*.java %SRC_DIR%\com\crucifix\client\modules\exploit\*.java %SRC_DIR%\com\crucifix\client\gui\*.java %SRC_DIR%\com\crucifix\client\gui\components\*.java %SRC_DIR%\com\crucifix\client\gui\animations\*.java %SRC_DIR%\com\crucifix\client\gui\themes\*.java %SRC_DIR%\com\crucifix\client\hud\*.java

if %errorlevel% neq 0 (
    echo Compilation failed
    exit /b 1
)

echo Creating JAR file...
cd %BUILD_DIR%
"C:\Program Files\Java\jdk-17\bin\jar.exe" cvfe ..\%JAR_FILE% com.crucifix.client.Crucifix com\crucifix\client\*.class com\crucifix\client\core\*.class com\crucifix\client\events\*.class com\crucifix\client\modules\*.class com\crucifix\client\modules\combat\*.class com\crucifix\client\modules\movement\*.class com\crucifix\client\modules\render\*.class com\crucifix\client\modules\player\*.class com\crucifix\client\modules\misc\*.class com\crucifix\client\modules\exploit\*.class com\crucifix\client\gui\*.class com\crucifix\client\gui\components\*.class com\crucifix\client\gui\animations\*.class com\crucifix\client\gui\themes\*.class com\crucifix\client\hud\*.class
cd ..\..

echo.
echo Build Complete!
echo Output: %JAR_FILE%

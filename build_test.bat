@echo off
echo 开始构建DaysMatter项目...
echo.

REM 检查Java环境
java -version
if %errorlevel% neq 0 (
    echo 错误: 未找到Java环境，请确保JAVA_HOME已正确设置
    pause
    exit /b 1
)

echo.
echo Java环境检查通过，开始构建...
echo.

REM 清理项目
gradlew clean
if %errorlevel% neq 0 (
    echo 错误: 清理项目失败
    pause
    exit /b 1
)

echo.
echo 项目清理完成，开始编译...
echo.

REM 构建Debug版本
gradlew assembleDebug
if %errorlevel% neq 0 (
    echo 错误: 构建失败
    pause
    exit /b 1
)

echo.
echo 构建成功！APK文件位于: app/build/outputs/apk/debug/app-debug.apk
echo.
pause 
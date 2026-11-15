@echo off
cd /d "%~dp0"
if not exist out mkdir out
javac -d out src\model\*.java src\service\*.java src\app\*.java
if errorlevel 1 (
  echo Compile failed. Check the errors above.
  pause
  exit /b 1
)
start "" javaw -cp out app.StudentGUI

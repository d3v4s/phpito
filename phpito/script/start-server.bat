@echo off
set ADDRESS=%1
set DIRECTORY=%2

echo ""
echo "[*] PHPito starting server at %date% %time%"
echo "[*] Starting PHP server on: %ADDRESS%"
echo "[*] Directory root: %DIRECTORY%"

@echo on
cd %directory% && php -S %address%

REM for /f "tokens=2 delims==; " %%a in (' wmic process call create "php -S %address% -t %directory%" ^| find "ProcessId" ') do set PID=%%a
REM echo "[*] Process ID: %PID%"
REM timeout /t 30

REM taskkill /PID %PID% /F
REM echo "[*] Finish"

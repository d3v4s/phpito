@echo off
set ADDRESS=%2
set DIRECTORY=%3

echo "[*] PHPito start server at %date% %time%"
echo "[*] Starting server php on: %ADDRESS%"
echo "[*] Directory root: %DIRECTORY%"

REM @echo on
for /f "tokens=2 delims==; " %%a in (' wmic process call create "php -S %address% -t %directory%" ^| find "ProcessId" ') do set PID=%%a
echo "[*] Process ID: %PID%"
timeout /t 30

taskkill /PID %PID% /F
echo "[*] Finish"

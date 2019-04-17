@echo off
set PID_KILL=%1;

echo "[*] Kill PID: %PID_KILL%"
echo "[*] Stopping server..."
@echo on
taskkill /PID "%PID_KILL%" && echo "[*] PHPito stopped server at %date% %time%" || echo "[!] Error!!! Fail to stop server."
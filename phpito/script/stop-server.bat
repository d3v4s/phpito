REM @echo off
set PID=%2;

echo "[*] PHPito start server - Date: %date% %time%";
echo "[*] Starting server php on: %address%";
echo "[*] Directory root: %directory%";

REM @echo on
php -S %address% -t %directory%;  
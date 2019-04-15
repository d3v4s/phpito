REM @echo off
set address=%2;
set directory=%3;

echo "[*] PHPito start server - Date: %date% %time%";
echo "[*] Starting server php on: %address%";
echo "[*] Directory root: %directory%";

REM @echo on
php -S %address% -t %directory%;  
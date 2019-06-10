@echo off
set ADDRESS=%1
set DIRECTORY=%2

echo [*] PHPito starting server at %date% %time%
echo [*] Starting PHP server on: %ADDRESS%
echo [*] Directory root: %DIRECTORY%

cd "%DIRECTORY%" && php -S %ADDRESS%
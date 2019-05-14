@echo off
set ADDRESS=%1
set DIRECTORY=%2
set INI=%3

echo [*] PHPito starting server at %date% %time%
echo [*] Starting PHP server on: %ADDRESS%
echo [*] Directory root: %DIRECTORY%

cd %DIRECTORY% && php -c %INI% -S %ADDRESS%
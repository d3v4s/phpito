@echo off
set ADDRESS=%1
set PID=%2

netstat -naop tcp | findstr /r ".*%ADDRESS%.*LISTENING.*%PID%"
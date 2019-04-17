@echo off
set ADDRESS=%1
set PID=%2

@echo on
netstat -naobp tcp | findstr /r ".*%ADDRESS%.*LISTENING.*%PID%"
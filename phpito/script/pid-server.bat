@echo off
set ADDRESS=%1

@echo on
netstat -naop tcp | findstr /r ".*%ADDRESS%.*LISTENING.*"
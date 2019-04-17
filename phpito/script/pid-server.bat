@echo off
set ADDRESS=%1

@echo on
netstat -naobp tcp | findstr /r ".*%ADDRESS%.*LISTENING.*"
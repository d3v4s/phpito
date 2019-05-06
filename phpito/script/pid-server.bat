@echo off
set ADDRESS=%1

netstat -naop tcp | findstr /r ".*%ADDRESS%.*LISTENING.*"
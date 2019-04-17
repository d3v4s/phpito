#!/bin/bash

DATE=`date '+%Y-%m-%d %H:%M:%S'`
PID_KILL=$1

echo "[*] Kill PID: $PID_KILL"
echo "[*] Stopping server..."
kill "$PID_KILL" && echo "[*] PHPito stopped server at $DATE" || echo "[!] Error!!! Fail to stop server." && exit 1

exit 0
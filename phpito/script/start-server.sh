#!/bin/bash

DATE=`date '+%Y-%m-%d %H:%M:%S'`
ADDRESS=$1
DIRECTORY=$2

echo
echo "[*] PHPito starting server at $DATE"
echo "[*] Starting PHP server on: $ADDRESS"
echo "[*] Directory root: $DIRECTORY"

cd $DIRECTORY && php -S $ADDRESS  # || echo "[!] Error!!! Server is not started." && exit 1
# PID_SERVER="$!"
# sleep 2
# if ps -p $PID_SERVER > /dev/null
# then
#	echo "[*] Server is running"
#	echo "[*] Process ID: $PID_SERVER"
# else
#	echo "[!] Error!!! Server is not started."
#	exit 1
# fi

exit 0
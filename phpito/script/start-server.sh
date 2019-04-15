#!/bin/bash

DATE=`date '+%Y-%m-%d %H:%M:%S'`
ADDRESS=$1
DIRECTORY=$2

echo "[*] PHPito start server at $DATE"
echo "[*] Starting server php on: $ADDRESS"
echo "[*] Directory root: $DIRECTORY"

# mkfifo capture
php -S $ADDRESS -t $DIRECTORY & 
PID_SERVER="$!"
sleep 2
if ps -p $PID_SERVER > /dev/null
then
	echo "[*] Server is running"
	echo "[*] Process ID: $PID_SERVER"
else
	echo "[!] Error!!! Server is not started."
	exit 1
fi
sleep 30
kill $PID_SERVER
echo "[*] Finish"
# wait
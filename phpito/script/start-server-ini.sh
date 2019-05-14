#!/bin/bash

DATE=`date '+%Y-%m-%d %H:%M:%S'`
ADDRESS=$1
DIRECTORY=$2
INI=$3

echo ""
echo "[*] PHPito starting server at $DATE"
echo "[*] Starting PHP server on: $ADDRESS"
echo "[*] Directory root: $DIRECTORY"

cd $DIRECTORY && php -c $3 -S $ADDRESS  # || echo "[!] Error!!! Server is not started." && exit 1

exit 0
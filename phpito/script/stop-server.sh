#!/bin/bash

DATE=`date '+%Y-%m-%d %H:%M:%S'`;
ADDRESS=$1;
DIRECTORY=$2;

echo "[*] PHPito start server - Date: $DATE";
echo "[*] Starting server php on: $ADDRESS";
echo "[*] Directory root: $DIRECTORY";

php -S $ADDRESS -t $DIRECTORY;
#!/bin/bash 

ADDRESS=$1
PORT=$2
PID=$3

# netstat -ltnp | grep -E '.*('$ADDRESS').*LISTEN.*('$PID')/php.*'
ss -Hlptn | grep $ADDRESS:$PORT

exit 0
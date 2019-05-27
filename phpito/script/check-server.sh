#!/bin/bash 

ADDRESS=$1
PORT=$2
PID=$3

# netstat -ltnp | grep -E '.*('$ADDRESS').*LISTEN.*('$PID')/php.*'
ss -Hlptn "src = $ADDRESS and sport = $PORT" 

exit 0
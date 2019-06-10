#!/bin/bash

ADDRESS=$1
PORT=$2

#netstat -ltnp | grep -E '.*('$ADDRESS').*LISTEN.*/php.*'
ss -Hlptn "src = $ADDRESS and sport = $PORT" 

exit 0
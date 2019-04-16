#!/bin/bash 

ADDRESS=$1
PID=$2

netstat -ltnp | grep -E '.*('$ADDRESS').*LISTEN.*('$PID')/php.*'
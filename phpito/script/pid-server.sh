#!/bin/bash

ADDRESS=$1

netstat -ltnp | grep -E '.*('$ADDRESS').*LISTEN.*/php.*'

exit 0
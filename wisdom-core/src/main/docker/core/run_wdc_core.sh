#!/bin/bash
if [ "$WDC_MINER_COINBASE" = "" ]; then
	echo "!!! ERROR !!! ENV WDC_MINER_COINBASE not set."
	exit 1
fi
java -jar /app/app.jar

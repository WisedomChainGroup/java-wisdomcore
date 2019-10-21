#!/usr/bin/env bash
if [ "$WDC_MINER_COINBASE" = "" ]; then
	echo "!!! ERROR !!! ENV WDC_MINER_COINBASE not set."
	exit 1
fi
java -Xss8m -Xms512m -Xmx4096m -XX:MaxNewSize=1024m -XX:MaxPermSize=1024m -jar /app/app.jar

#!/usr/bin/env bash
if [ "$WDC_MINER_COINBASE" = "" ]; then
	echo "!!! ERROR !!! ENV WDC_MINER_COINBASE not set."
	exit 1
fi
java -Xss8m -Xms512m -Xmx4096m -XX:MaxPermSize=128m -XX:-UseGCOverheadLimit -XX:+UseConcMarkSweepGC -jar /app/app.jar $JPAGENT_PATH
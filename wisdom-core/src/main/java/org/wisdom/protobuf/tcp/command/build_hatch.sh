#!/usr/bin/env bash
CUR=`dirname $0`
protoc $CUR/Hatch.proto -I=$CUR  --java_out="../../../../../"

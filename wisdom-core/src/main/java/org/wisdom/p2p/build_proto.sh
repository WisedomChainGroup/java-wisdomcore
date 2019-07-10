#!/usr/bin/env bash
CUR=`dirname $0`
protoc $CUR/wisdom.proto -I=$CUR  --java_out="../../../"

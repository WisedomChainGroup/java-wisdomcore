#!/usr/bin/env bash
CUR=`dirname $0`
protoc $CUR/Protocol.proto -I=$CUR  --java_out="../../../../"

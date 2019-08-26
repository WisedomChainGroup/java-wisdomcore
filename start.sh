#!/usr/bin/env bash
set -e
set -u
set -o pipefail

CUR=`dirname $0`

ENV_FILE=${CUR=}/local.env
env $(cat ${ENV_FILE} | grep -v '^#'| xargs) bash ./gradlew run

#!/usr/bin/env bash

IMAGE=wisdomchain/wdc_core
CUR=$(dirname $0)

# parse -t -tag arguments as $TAG
source $CUR/../parse_tag.sh

PROJECT_ROOT=$CUR/../../../..
PROJECT_ROOT=`cd $PROJECT_ROOT; pwd`
bash $PROJECT_ROOT/../gradlew jar
cp $PROJECT_ROOT/build/libs/wisdom*.jar $CUR/build

SUFFIX=:$TAG
if [[ $SUFFIX == ':' ]]; then
  SUFFIX=''
fi

docker build -f $CUR/Dockerfile -t $IMAGE$SUFFIX $CUR

rm -rf $CUR/build/*
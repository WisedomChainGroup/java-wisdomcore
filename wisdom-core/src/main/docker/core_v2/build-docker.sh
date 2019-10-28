#!/usr/bin/env bash

IMAGE=wisdomchain/wdc_core

# directory of this file
CUR=$(dirname $0)
CUR=`cd $CUR; pwd`

rm -rf $CUR/build
mkdir $CUR/build

# parse -t -tag arguments as $TAG
source $CUR/arg_parse.sh

# project root path java-wisdomcore/wisdomcore
PROJECT_ROOT=$CUR/../../../..
PROJECT_ROOT=`cd $PROJECT_ROOT; pwd`


cd $PROJECT_ROOT/..
bash $PROJECT_ROOT/../gradlew jar

cp $PROJECT_ROOT/build/libs/wisdom*.jar $CUR/build

SUFFIX=:$TAG
if [[ $SUFFIX == ':' ]]; then
  SUFFIX=''
fi

docker build -f $CUR/Dockerfile -t $IMAGE$SUFFIX $CUR

rm -rf $CUR/build/*
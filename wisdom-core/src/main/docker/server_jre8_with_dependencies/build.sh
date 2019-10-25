#!/usr/bin/env bash

# java-wisdomcore/wisdom-core/src/main/docker/server_jre8_with_dependencies

IMAGE=wisdomchain/server_jre8_with_dependencies

# directory of this file
CUR=$(dirname $0)

# parse -t -tag arguments as $TAG
source $CUR/arg_parse.sh

# project root path java-wisdomcore/wisdomcore
PROJECT_ROOT=$CUR/../../../..
PROJECT_ROOT=`cd $PROJECT_ROOT; pwd`

bash $PROJECT_ROOT/../gradlew copyDependencies

# copy dependencies to tempory directory
cp $PROJECT_ROOT/build/libs/lib/*.jar $CUR/build/
cp -r $PROJECT_ROOT/build/resources/wisdom-genesis-generator.json $CUR/build/
SUFFIX=:$TAG
if [[ $SUFFIX == ':' ]]; then
  SUFFIX=''
fi

docker build -f $CUR/Dockerfile -t $IMAGE$SUFFIX $CUR

# clean tempory files
rm -rf $CUR/build/*
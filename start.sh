#!/usr/bin/env bash
./gradlew jar
env -S `cat local.env` java -jar wisdom-core/build/libs/wisdom-core-1.13.0-RELEASE.jar

#!/usr/bin/env bash
./gradlew jar
env `cat local.env | tr '\n' ' '` java -jar wisdom-core/build/libs/wisdom-core-1.13.0-RELEASE.jar

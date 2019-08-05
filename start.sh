#!/usr/bin/env bash

env -S "$(tr '\n' ' ' < local.env)" ./gradlew run

#!/usr/bin/env bash

find . -type f | grep -E '^.*?.sh$' | xargs chmod +x
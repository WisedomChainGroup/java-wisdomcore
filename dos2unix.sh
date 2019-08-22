#!/usr/bin/env bash

find . -type f | grep -E '^.*?.(java|sh|yml)$' | xargs dos2unix
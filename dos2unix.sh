#!/usr/bin/env bash

find . -type f | grep -E '^.*?.(java|sh|py|js|yml)$' | xargs dos2unix

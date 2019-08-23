#!/usr/bin/env bash

# download protoc-gen-grpc-java depends on your platform https://repo1.maven.org/maven2/io/grpc/protoc-gen-grpc-java/1.22.1/
protoc \
  --grpc-java_out="../java" wisdom.proto

protoc --java_out="../java" wisdom.proto

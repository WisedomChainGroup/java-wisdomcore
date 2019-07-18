#!/usr/bin/env bash

protoc --plugin=/usr/local/bin/protoc-gen-grpc-java \
  --grpc-java_out="../java" wisdom.proto

protoc --java_out="../java" wisdom.proto

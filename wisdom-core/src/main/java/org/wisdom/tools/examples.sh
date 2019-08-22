#!/usr/bin/env bash

node block-interval-test.js --last_block_height -1 --blocks 20 # 平均查看出块时间

node count-orphans.js --host localhost --port 5432 --username '' --password '' --database 'postgres'  # 检查孤块数量


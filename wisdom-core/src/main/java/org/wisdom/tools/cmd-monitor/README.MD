# wisdom 节点监控

## Quick start

- install dependencied

```bash
yarn 
```

- 计算平均出块时间

```bash
node cmd.js interval --from '1' --to '-1' --rpc_host_port localhost:8888
```

- 统计节点出块数量

```bash
node cmd.js count_proposals -f 1 -t '-1' -r localhost:8888
```

- 如果节点最近50个区块不出块则发短信报警

```bash
 node cmd.js alert_offline  -i  'id' -k 'sk'  -p '10086' -s 'company' -t 'code' -r localhost:8888
```

- 查看帮助

```bash
node cmd.js --help
```

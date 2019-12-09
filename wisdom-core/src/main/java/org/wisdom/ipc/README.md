## IPC说明文档

### ipc的安装

* ubuntu系统上需要装python2,一般从aliyun或aws上购买的服务器都是自带python2

* 在docker容器中才可以使用ipc,需要在`wdc_core_v0.0.3`的volumes中添加`-/opt/ipc:/root/ipc`

* 请向开发人员索要`ipc_client.py`脚本，使用`python2 ipc_client.py`运行

### ipc的使用

* Send transaction 发送广播
* Modify the operating parameters 修改运行参数
  * Modify version 修改版本
  * Modify whether only native clients can connect 修改是否只允许本地客户端连接
  * Whether to support json-rpc 是否支持rpc连接(切换后不支持grpc)
  * Whether to support grpc 是否支持grpc连接(切换后不支持rpc)
  * Modify the transaction limit of the queued queue  修改queued中最大的事务数量
  * Modify the transaction limit of the pending queue  修改pending中最大的事务数量
  * Modify the transaction into the memory pool minimum fee 修改事务进入内存池的最低手续费
  * Queued to pending write cycle 修改queued到pending的写入周期
  * Queued and pending cleaning cycles  修改queued和pending的清理周期
* Get node information 获取节点信息
* Node book information  节点帐簿信息 
  * Get Nonce 获取账户在节点中的nonce
  * Get Balance 获取账户余额
  * Query the current block height 查询当前的区块高度
  * Get the hash and height of the block based on the transaction hash 根据事务hash获取区块高度和hash
  * Get block acknowledgment status based on transaction hash 根据事务hash获取区块确认状态
  * Get the transaction list based on the block height 根据区块高度获取事务列表
  * Get transaction through transaction hash 根据事务hash获取事务
  * Get the list of transactions by block hash 根据区块hash获取事务列表
* Generate JWT Token 生成JWT Token
# WisdomCore-J

WisdomChain core procedure with java version

The Wisdom Chain is a brand new intelligent contract and communication platform, based on Block Chain, faced to smart city, achieving the Internet of Things for living facilities and interconnection for living information.

It aims to provide distributed data connection services for many intelligent items of human being, in order to share the data conveniently between personal terminal and the city intelligence terminal, and to share the Identity (ID) authentication, and can provide a consistent access ability of privilege data.

As a full set of solution to build an intelligent city information interconnection, it becomes a connector of a massive number of intelligent systems and devices.

The Wisdom Chain, as a bottom supporting system, will be released in the form of Main Chain, supporting parallel running structure of multi-chains and multi level communication protocol, including device interconnection protocol, terminal data sharing, ID authentication and instant communication protocol etc, achieving the network autonomy with the synchronization of node data through the mixture consensus mechanism by the combination of PoW (proof of work) and PoS (proof of stake), finally achieves the main chain faced to the living information interconnection.  

# WisdomCore-J Deploy





## 1.	Requirements

### 1.1.	Config network firewall


The default ports of Wisdom Chain's P2P and RPC is 19585. When docker container ports are mapped, they can be modified as required.  
Please modify the network firewall settings to determine whether to open the port.

### 1.2.	hardware

For fullnode operation facilities, the hardware conditions are suggested as follows:
Disk space recommended more than 500GB, memory 16GB, CPU-8 core.

### 1.3.	Install docker、docker-compose

#### 1.3.1.	Ubuntu
```
apt install -y docker-compose
```

#### 1.3.2.	CentOS
```
yum install -y docker

sudo curl -L "https://github.com/docker/compose/releases/download/1.24.0/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose

sudo chmod +x /usr/local/bin/docker-compose
```


See also：[https://docs.docker.com/compose/install/](https://docs.docker.com/compose/install/)


## 2.	YML file

### 2.1.	Example format：


```
version: '3.1'

services:

  wdc_pgsql:
    image: wisdomchain/wdc_pgsql
    restart: always
    container_name: wdc_pgsql
    privileged: true
    volumes:
      - /opt/wdc_pgsql:/var/lib/postgresql/data
    ports:
      - 127.0.0.1:5432:5432
    environment:
      POSTGRES_USER: wdcadmin
      POSTGRES_PASSWORD: PqR_m03VaTsB1M/hyKY
      WDC_POSTGRES_USER: replica
      WDC_POSTGRES_PASSWORD: replica

  wdc_core:
    image: wisdomchain/wdc_core
    restart: always
container_name: wdc_core
    privileged: true
    volumes:
      - /opt/wdc_logs:/logs
    ports:
      - 19585:19585
    environment:
      DATA_SOURCE_URL: 'jdbc:postgresql://wdc_pgsql:5432/postgres'
      DB_USERNAME: 'replica'
      DB_PASSWORD: 'replica'
      ENABLE_MINING: 'false'
      WDC_MINER_COINBASE: ''
      
```

### 2.2.	volumes mapping： 

It can be mapped to different directories as needed.
Among them, wdc_pgsql volumes maps the PostgreSql database data directory. After the docker container is deleted, the directory will not be deleted automatically, and the node data will still be retained.   
If you want to start WDC Core completely, please backup the directory and delete or empty it.


Wdc_core volumes map the WDC Core node program log directory.

### 2.3.	ports mapping：

Port mapping of wdc_pgsql is recommended to be mapped to IP address 127.0.0.1 in order to ensure security, and only local access is allowed.  
If you do not want to access the database through an external client, you can also remove the port mapping.  
The external port numbers of wdc_pgsql and wdc_core can be modified as required.

### 2.4.	environment：

The database username password can be customized, but to ensure that WDC_POSTGRES_USER is consistent with DB_USERNAME, WDC_POSTGRES_PASSWORD is consistent with DB_PASSWORD.

ENABLE_MINING indicates whether to start mining.

WDC_MINER_COINBASE must be set for mining coinbase address, otherwise the node cannot start. 

For fullnode that do not participate in mining, an initialization setting is also made.


## 3.	Start docker image

### 3.1.	start command：
```
docker-compose -f wdc.yml up -d
```
（See section 2, YML file for wdc.yml）

The final output of the command is as follows. The table shows that the docker container started successfully.

```
Creating wdc_core ...   

Creating wdc_pgsql ...  
 
Creating wdc_core  
  
Creating wdc_pgsql ... done  
```

### 3.2.	Node Running Check
Command ```docker ps ``` View container status  

The status field is "Up..." and the container is working properly. 

### 3.3.	Log Check

Command ```docker logs -f <CONTAINER ID> ```   
View Node Program Console Output 
/opt/wdc_logs The directory is the directory of log files of node programs. If YML files are mapped to other directories, please go to the corresponding directory to see.

### 3.4.	Upgrade

```
stop and delete container
docker-compose -f wdc.yml down

get latest image
docker pull wisdomchain/wdc_core
```
modify wdc.yml，if necessary。  

```  
enable image
docker-compose -f wdc.yml up -d
```

## 4.	参考文档
[https://docs.docker.com/](https://docs.docker.com/)  
[https://docs.docker.com/compose/](https://docs.docker.com/compose/)





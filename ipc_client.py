#!/usr/bin/python
# -*- coding: utf-8 -*-

import os
import json
import time

write_path = "/opt/ipc/pipe.in"
read_path = "/opt/ipc/pipe.out"

wf = os.open(write_path, os.O_SYNC | os.O_CREAT | os.O_RDWR)
print("欢迎使用IPC客户端!!!")

def select():
    print("请选择一下操作：")
    print("A:发送事务 \nB:修改运行参数 \nC:获得节点信息 \nD:节点的账本信息")
    choice = raw_input('input A, B, C or D :')
    if choice not in ('A','B','C','D'):
        return select()
    return choice

def DA():
    print("请输入publicKeyHash")
    publicKeyHash = raw_input('input publicKeyHash:')
    message = json.dumps({'type':'sendNonce','message':publicKeyHash})
    os.write(wf, message)
    print ("sent msg: %s" % message)
    rf = os.open(read_path, os.O_RDONLY)
    s = os.read(rf, 1024)
    print ("received msg: %s" % s)
    time.sleep(1)
    os.close(rf)    

def DB():
    print("请输入publicKeyHash")
    publicKeyHash = raw_input('input publicKeyHash:')
    message = json.dumps({'type':'getBalance','message':publicKeyHash})
    os.write(wf, message)
    print ("sent msg: %s" % message)
    rf = os.open(read_path, os.O_RDONLY)
    s = os.read(rf, 1024)
    print ("received msg: %s" % s)
    time.sleep(1)
    os.close(rf)

def DC():
    message = json.dumps({'type':'height','message':''})
    os.write(wf, message)
    print ("sent msg: %s" % message)
    rf = os.open(read_path, os.O_RDONLY)
    s = os.read(rf, 1024)
    print ("received msg: %s" % s)
    time.sleep(1)
    os.close(rf)

def DD():
    print("请输入txHash")
    txHash = raw_input('input txHash:')
    message = json.dumps({'type':'blockHash','message':txHash})
    os.write(wf, message)
    print ("sent msg: %s" % message)
    rf = os.open(read_path, os.O_RDONLY)
    s = os.read(rf, 1024)
    print ("received msg: %s" % s)
    time.sleep(1)
    os.close(rf)

def DE():
    print("请输入txHash")
    txHash = raw_input('input txHash:')
    message = json.dumps({'type':'transactionConfirmed','message':txHash})
    os.write(wf, message)
    print ("sent msg: %s" % message)
    rf = os.open(read_path, os.O_RDONLY)
    s = os.read(rf, 1024)
    print ("received msg: %s" % s)
    time.sleep(1)
    os.close(rf)

def DF():
    print("请输入height")
    height = input('input height:')
    print("请输入事物类型,转账事物是1")
    ty = input('input type:')
    mes = json.dumps({'height':height,'type':ty})
    message = json.dumps({'type':'getTransactionHeight','message':mes})
    os.write(wf, message)
    print ("sent msg: %s" % message)
    rf = os.open(read_path, os.O_RDONLY)
    s = os.read(rf, 1024)
    print ("received msg: %s" % s)
    time.sleep(1)
    os.close(rf)

def DG():
    print("请输入txHash")
    txHash = raw_input('input txHash:')
    message = json.dumps({'type':'transaction','message':txHash})
    os.write(wf, message)
    print ("sent msg: %s" % message)
    rf = os.open(read_path, os.O_RDONLY)
    s = os.read(rf, 1024)
    print ("received msg: %s" % s)
    time.sleep(1)
    os.close(rf)

def DH():
    print("请输入blockHash")
    blockHash = raw_input('input blockHash:')
    print("请输入事物类型,转账事物是1")
    ty = input('input type:')
    mes = json.dumps({'blockHash':blockHash,'type':ty})
    message = json.dumps({'type':'getTransactionBlock','message':mes})
    os.write(wf, message)
    print ("sent msg: %s" % message)
    rf = os.open(read_path, os.O_RDONLY)
    s = os.read(rf, 1024)
    print ("received msg: %s" % s)
    time.sleep(1)
    os.close(rf)

def BA():
    print("请输入version")
    version = raw_input('input version:')
    message = json.dumps({'type':'modifyVersion','message':version})
    os.write(wf, message)
    print ("sent msg: %s" % message)
    rf = os.open(read_path, os.O_RDONLY)
    s = os.read(rf, 1024)
    print ("received msg: %s" % s)
    time.sleep(1)
    os.close(rf)

def BB():
    print("BB")    

def BC():
    print("请输入是否只有本机客户端才能连接false/true,true的话会禁掉rpc")
    isLocalOnly = raw_input('input false or true:')
    message = json.dumps({'type':'setIsLocalOnly','message':isLocalOnly})
    os.write(wf, message)
    print ("sent msg: %s" % message)
    rf = os.open(read_path, os.O_RDONLY)
    s = os.read(rf, 1024)
    print ("received msg: %s" % s)
    time.sleep(1)
    os.close(rf)

def BD():
    print("BD")

def BE():
    print("BE")

def BF():
    print("BF")

def BG():
    print("BG")

def BH():
    print("BH")

def BI():
    print("BI")

def BJ():
    print("BJ") 

def BK():
    print("请输入queued到pending的写入周期，格式如：*/5 * * * * ?")
    queuedToPendingCycle = raw_input('input queuedToPendingCycle:')
    message = json.dumps({'type':'modifyQueuedToPendingCycle','message':queuedToPendingCycle})
    os.write(wf, message)
    print ("sent msg: %s" % message)
    rf = os.open(read_path, os.O_RDONLY)
    s = os.read(rf, 1024)
    print ("received msg: %s" % s)
    time.sleep(1)
    os.close(rf)


def BL():
    print("queued与pending的清理周期，格式如：0 */1 * * * ?")
    clearCycle = raw_input('input clearCycle:')
    message = json.dumps({'type':'modifyClearCycle','message':clearCycle})
    os.write(wf, message)
    print ("sent msg: %s" % message)
    rf = os.open(read_path, os.O_RDONLY)
    s = os.read(rf, 1024)
    print ("received msg: %s" % s)
    time.sleep(1)
    os.close(rf)                        


def A():
    print("请输入tranInfo")
    tranInfo = raw_input('input tranInfo:')
    message = json.dumps({'type':'sendTranInfo','message':tranInfo})
    os.write(wf, message)
    print ("sent msg: %s" % message)
    rf = os.open(read_path, os.O_RDONLY)
    s = os.read(rf, 1024)
    print ("received msg: %s" % s)
    time.sleep(1)
    os.close(rf)

def B():
    print("请选择一下操作：")
    print("A:修改版本 \nB:修改最大允许连接的网络节点的数量 \nC:修改是否只有本机客户端才能连接 \nD:修改同步区块时，一次性请求获得的区块数 \nE:是否支持json-rpc \nF:是否支持grpc")
    print("G:修改queued队列的事务上限 \nH:修改pending队列的事务上限 \nI:修改事务的过期时间 \nJ:修改事务进入内存池最低手续费 \nK:queued到pending的写入周期 \nL:queued与pending的清理周期")
    choice = raw_input('input A ~ L :')
    if choice not in ('A','B','C','D','E','F','G','H','I','J','K','L'):
        B()
    if choice == 'A':
        BA()
    if choice == 'B':
        BB()
    if choice == 'C':
        BC()
    if choice == 'D':
        BD()    
    if choice == 'E':
        BE()
    if choice == 'F':
        BF()
    if choice == 'G':
        BG()
    if choice == 'H':
        BH()
    if choice == 'I':
        BI()
    if choice == 'J':
        BJ()
    if choice == 'K':
        BK()
    if choice == 'L':
        BL()    


def C():
    message = json.dumps({'type':'getNodeInfo','message':""})
    os.write(wf, message)
    print ("sent msg: %s" % message)
    rf = os.open(read_path, os.O_RDONLY)
    s = os.read(rf, 1024)
    print ("received msg: %s" % s)
    time.sleep(1)
    os.close(rf)

def D():
    print("请选择一下操作：")
    print("A:获取Nonce \nB:获取余额 \nC:查询当前区块高度 \nD:根据事务哈希获得所在区块哈希以及高度 \nE:根据事务哈希获得区块确认状态 \nF:根据区块高度获取事务列表 \nG:通过事务哈希获取事务 \nH:通过区块哈希获取事务列表")
    choice = raw_input('input A ~ H :')
    if choice not in ('A','B','C','D','E','F','G','H'):
        D()
    if choice == 'A':
        DA()
    if choice == 'B':
        DB()
    if choice == 'C':
        DC()
    if choice == 'D':
        DD()    
    if choice == 'E':
        DE()
    if choice == 'F':
        DF()
    if choice == 'G':
        DG()
    if choice == 'H':
        DH()

value = select()

switch = {
    "A":A,
    "B":B,
    "C":C,
    "D":D
}

try:
   switch[value]()
except Exception as e:
   pass

os.close(wf)

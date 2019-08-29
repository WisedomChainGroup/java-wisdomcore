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
    print("A:发送事务 B:修改运行参数 C:获得节点信息 D:节点的账本信息")
    choice = raw_input('input A, B, C or D :')
    if choice not in ('A','B','C','D'):
        select()
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
    print("此功能未完成，敬请期待!!!")

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
    print("A:获取Nonce B:获取余额 C:查询当前区块高度 D:根据事务哈希获得所在区块哈希以及高度 E:根据事务哈希获得区块确认状态 F:根据区块高度获取事务列表 G:通过事务哈希获取事务 H:通过区块哈希获取事务列表")
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
except KeyError as e:
   pass

os.close(wf)

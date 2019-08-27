#!/usr/bin/python
# -*- coding: utf-8 -*-

import os
import json
import time

write_path = "/opt/ipc/pipe.in"
read_path = "/opt/ipc/pipe.out"

wf = os.open(write_path, os.O_SYNC | os.O_CREAT | os.O_RDWR)
print("欢迎!!!")

def select():
    print("请选择一下操作：")
    print("A:发送事务 B:修改运行参数 C:获得节点信息 D:节点的账本信息")
    choice = raw_input('input A, B, C or D :')
    if choice not in ('A','B','C','D'):
        select()
    return choice

def A():
    print("请输入traninfo")
    traninfo = raw_input('input traninfo:')
    message = json.dumps({'type':'sendTranInfo','message':traninfo})
    os.write(wf, message)
    print ("sent msg: %s" % message)
    rf = os.open(read_path, os.O_RDONLY)
    s = os.read(rf, 1024)
    print ("received msg: %s" % s)
    time.sleep(1)
    os.close(rf)

def B():
    print("B")

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
    print("D")

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

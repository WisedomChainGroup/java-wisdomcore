#!/usr/bin/python
# -*- coding: utf-8 -*-

import os
import json
import time

write_path = "/opt/ipc/pipe.in"
read_path = "/opt/ipc/pipe.out"

wf = os.open(write_path, os.O_SYNC | os.O_CREAT | os.O_RDWR)
rf = os.open(read_path, os.O_RDONLY)
print("Welcome to the IPC client!!!")

def select():
    print("Please choose the operation：")
    print("A:Send transaction \nB:Modify the operating parameters \nC:Get node information \nD:Node book information \nE:Generate JWT Token")
    choice = raw_input('input A, B, C , D or E:')
    if choice not in ('A','B','C','D','E'):
        return select()
    return choice

def DA():
    publicKeyHash = raw_input('input publicKeyHash:')
    message = json.dumps({'type':'sendNonce','message':publicKeyHash})
    os.write(wf, message)
    print ("sent msg: %s" % message)
    s = os.read(rf, 1024)
    print ("received msg: %s" % s)
    time.sleep(1)
    os.close(rf)

def DB():
    publicKeyHash = raw_input('input publicKeyHash:')
    message = json.dumps({'type':'getBalance','message':publicKeyHash})
    os.write(wf, message)
    print ("sent msg: %s" % message)
    s = os.read(rf, 1024)
    print ("received msg: %s" % s)
    time.sleep(1)
    os.close(rf)

def DC():
    message = json.dumps({'type':'height','message':''})
    os.write(wf, message)
    print ("sent msg: %s" % message)
    s = os.read(rf, 1024)
    print ("received msg: %s" % s)
    time.sleep(1)
    os.close(rf)

def DD():
    txHash = raw_input('input txHash:')
    message = json.dumps({'type':'blockHash','message':txHash})
    os.write(wf, message)
    print ("sent msg: %s" % message)
    s = os.read(rf, 1024)
    print ("received msg: %s" % s)
    time.sleep(1)
    os.close(rf)

def DE():
    txHash = raw_input('input txHash:')
    message = json.dumps({'type':'transactionConfirmed','message':txHash})
    os.write(wf, message)
    print ("sent msg: %s" % message)
    s = os.read(rf, 1024)
    print ("received msg: %s" % s)
    time.sleep(1)
    os.close(rf)

def DF():
    height = input('input height:')
    ty = input('input type:')
    mes = json.dumps({'height':height,'type':ty})
    message = json.dumps({'type':'getTransactionHeight','message':mes})
    os.write(wf, message)
    print ("sent msg: %s" % message)
    s = os.read(rf, 1024)
    print ("received msg: %s" % s)
    time.sleep(1)
    os.close(rf)

def DG():
    txHash = raw_input('input txHash:')
    message = json.dumps({'type':'transaction','message':txHash})
    os.write(wf, message)
    print ("sent msg: %s" % message)
    s = os.read(rf, 1024)
    print ("received msg: %s" % s)
    time.sleep(1)
    os.close(rf)

def DH():
    blockHash = raw_input('input blockHash:')
    ty = input('input type:')
    mes = json.dumps({'blockHash':blockHash,'type':ty})
    message = json.dumps({'type':'getTransactionBlock','message':mes})
    os.write(wf, message)
    print ("sent msg: %s" % message)
    s = os.read(rf, 1024)
    print ("received msg: %s" % s)
    time.sleep(1)
    os.close(rf)

def BA():
    version = raw_input('input version:')
    message = json.dumps({'type':'modifyVersion','message':version})
    os.write(wf, message)
    print ("sent msg: %s" % message)
    s = os.read(rf, 1024)
    print ("received msg: %s" % s)
    time.sleep(1)
    os.close(rf)

def BB():
    print("Please enter whether only the native client can connect to false/true, if true, rpc will be disabled")
    isLocalOnly = raw_input('input false or true:')
    message = json.dumps({'type':'setIsLocalOnly','message':isLocalOnly})
    os.write(wf, message)
    print ("sent msg: %s" % message)
    s = os.read(rf, 1024)
    print ("received msg: %s" % s)
    time.sleep(1)
    os.close(rf)

def BC():
    print("Whether to support json-rpc, true/false, true switch to rpc, false switch to grpc")
    isRpc = bool(raw_input('input true or false:'))
    global mode
    if isRpc:
        mode = 'rest'
    else:
        mode = 'grpc'
    message = json.dumps({'type':'setP2PMode','message':mode})
    os.write(wf, message)
    print ("sent msg: %s" % message)
    s = os.read(rf, 1024)
    print ("received msg: %s" % s)
    time.sleep(1)
    os.close(rf)

def BD():
    print("Whether to support gpc, true / false, true switch to grpc, false switch to rpc")
    isGrpc = bool(raw_input('input true or false:'))
    global mode
    if isGrpc:
        mode = 'grpc'
    else:
        mode = 'rest'
    message = json.dumps({'type':'setP2PMode','message':mode})
    os.write(wf, message)
    print ("sent msg: %s" % message)
    s = os.read(rf, 1024)
    print ("received msg: %s" % s)
    time.sleep(1)
    os.close(rf)

def BE():
    print("Modify the transaction limit of the queued queue. The default is 60000.")
    queuedLimit = raw_input('input queued limit:')
    message = json.dumps({'type':'setQueuedMaxSize','message':queuedLimit})
    os.write(wf, message)
    print ("sent msg: %s" % message)
    s = os.read(rf, 1024)
    print ("received msg: %s" % s)
    time.sleep(1)
    os.close(rf)

def BF():
    print("Modify the transaction limit of the pending queue. The default is 30000.")
    pendingLimit = raw_input('input pending limit:')
    message = json.dumps({'type':'setPendingMaxSize','message':pendingLimit})
    os.write(wf, message)
    print ("sent msg: %s" % message)
    s = os.read(rf, 1024)
    print ("received msg: %s" % s)
    time.sleep(1)
    os.close(rf)

def BG():
    print("Please enter the transaction into the memory pool minimum fee, the default is 200000")
    feeLimit = raw_input('input feeLimit:')
    message = json.dumps({'type':'modifyFeeLimit','message':feeLimit})
    os.write(wf, message)
    print ("sent msg: %s" % message)
    s = os.read(rf, 1024)
    print ("received msg: %s" % s)
    time.sleep(1)
    os.close(rf)

def BH():
    print("Please enter a write cycle that is queued to pending, in the format: */5 * * * * ?")
    queuedToPendingCycle = raw_input('input queuedToPendingCycle:')
    message = json.dumps({'type':'modifyQueuedToPendingCycle','message':queuedToPendingCycle})
    os.write(wf, message)
    print ("sent msg: %s" % message)
    s = os.read(rf, 1024)
    print ("received msg: %s" % s)
    time.sleep(1)
    os.close(rf)


def BI():
    print("The cleanup cycle of queued and pending, in the format: 0 */1 * * * ?")
    clearCycle = raw_input('input clearCycle:')
    message = json.dumps({'type':'modifyClearCycle','message':clearCycle})
    os.write(wf, message)
    print ("sent msg: %s" % message)
    s = os.read(rf, 1024)
    print ("received msg: %s" % s)
    time.sleep(1)
    os.close(rf)


def A():
    tranInfo = raw_input('input tranInfo:')
    message = json.dumps({'type':'sendTranInfo','message':tranInfo})
    os.write(wf, message)
    print ("sent msg: %s" % message)
    s = os.read(rf, 1024)
    print ("received msg: %s" % s)
    time.sleep(1)
    os.close(rf)

def B():
    print("Please choose the operation：")
    print("A:Modify version \nB:Modify whether only native clients can connect")
    print("C:Whether to support json-rpc \nD:Whether to support grpc \nE:Modify the transaction limit of the queued queue \nF:Modify the transaction limit of the pending queue")
    print("G:Modify the transaction into the memory pool minimum fee \nH:Queued to pending write cycle \nI:Queued and pending cleaning cycles")
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

def C():
    message = json.dumps({'type':'getNodeInfo','message':""})
    os.write(wf, message)
    print ("sent msg: %s" % message)
    s = os.read(rf, 1024)
    print ("received msg: %s" % s)
    time.sleep(1)
    os.close(rf)

def D():
    print("Please choose the operation：")
    print("A:Get Nonce \nB:Get Balance  \nC:Query the current block height \nD:Get the hash and height of the block based on the transaction hash \nE:Get block acknowledgment status based on transaction hash")
    print("F:Get the transaction list based on the block height \nG:Get transaction through transaction hash \nH:Get the list of transactions by block hash")
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

def E():
    print("Please enter expiration time,One hour is 1000 * 60 * 60 ,in the format:3600000")
    milliseconds = raw_input('input expiration time in milliseconds:')
    message = json.dumps({'type':'getJWTToken','message':milliseconds})
    os.write(wf, message)
    print ("sent msg: %s" % message)
    s = os.read(rf, 1024)
    print ("received msg: %s" % s)
    time.sleep(1)
    os.close(rf)

value = select()

switch = {
    "A":A,
    "B":B,
    "C":C,
    "D":D,
    "E":E
}

try:
   switch[value]()
except Exception as e:
   pass

os.close(wf)

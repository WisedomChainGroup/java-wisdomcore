"use strict";

const bs58 = require('./base58');
const keccak512 = require('./sha3').keccak512;
const keccak256 = require('./sha3').keccak256;
const hash = require('./hashes.js');
const nacl = require('./nacl.min.js');
const crypto = require('crypto');
const ripemd160 = crypto.createHash('ripemd160');

const NetType = {
    'Public_Net': 1,
    'Test_Net': 2,
    'RegTest_Net': 3
};

class Index {
    buf2hex(buffer) { // buffer is an ArrayBuffer
        return Array.prototype.map.call(new Uint8Array(buffer), x => ('00' + x.toString(16)).slice(-2)).join('');
    }

    constructor() {
    }

    createAccount() {
        const keyPair = this.createKeyPair();
        const s2 = this.publicKeyToAddress(keyPair.publicKey);
        return {
            'secretKey': keyPair.secretKey,
            'publicKey': keyPair.publicKey,
            'addr': s2
        }
    }

    createAccountWithPubKey(pubKey, netType = NetType.Public_Net) {
        let s1 = keccak512(pubKey);

        let s2 = new hash.RMD160({'utf8': false}).hex(this.Hex2Str(s1));
        let s3 = '00' + s2;
        if (netType === NetType.Test_Net) {
            s3 = 'S'.charCodeAt(0) + s2;
        }

        let v = keccak512(this.Hex2Array(s3)).substring(0, 8);
        let s4 = s3 + v;

        let addr = new bs58().encode(this.Hex2Array(s4));
        return {
            'secretKey': keyPair.secretKey,
            'publicKey': keyPair.publicKey,
            'addr': addr
        }
    }

    // 从私钥生成密钥对
    keyPairFromPrivateKey(privateKey) {
        return new nacl.sign.keyPair.fromSeed(privateKey);
    }

    // 创建私钥
    createPrivateKey() {
        while (true) {
            const secretKey = crypto.randomBytes(32);
            if (this.verifyPrivateKey(secretKey)) {
                return secretKey
            }
        }
    }

    // 私钥验证
    verifyPrivateKey(privateKey) {
        if (privateKey.length !== 32) return false;
        for (let i = 0; i < 32; i++) {
            if (typeof privateKey[i] !== 'number') return false;
            if (privateKey[i] > 0xff) return false;
        }
        const MaxVal = Buffer.from('1000000000000000000000000000000014def9dea2f79cd65812631a5cf5d3ec', 'hex');
        for (let i = 0; i < MaxVal.length; i++) {
            if (MaxVal[i] < privateKey[i]) {
                return false;
            } else if (MaxVal[i] > privateKey[i]) {
                return true;
            }
        }
        return true;
    }

    Hex2Str(hex) {
        // let ret = '';
        // for(let i=0; i<hex.length; i+=2) {
        //     ret += String.fromCharCode(parseInt(hex.substr(i,2), 16));
        // }
        // return ret;

        const obj = Buffer.from(hex, 'hex');
        return obj.toString('latin1');
    }

    Hex2Array(hex) {
        // let ret = new Array();
        // for(let i=0; i<hex.length; i+=2) {
        //     ret.push(parseInt(hex.substr(i,2), 16));
        // }
        // return ret;
        return Buffer.from(hex, 'hex');
    }

    createKeyPair() {
        const secretKey = this.createPrivateKey();
        return this.keyPairFromPrivateKey(secretKey);
    }

    // 公钥转地址 string/Buffer -> string
    publicKeyToAddress(publicKey) {
        //     1）、对公钥进行SHA3-256哈希，再进行RIPEMD-160哈希，
        //         得到哈希值r1
        //    3）、在r1前面附加一个字节的版本号:0x00
        //         得到结果r2
        //    4）、将r1进行两次SHA3-256计算，得到结果r3，
        //         获得r3的前面4个字节，称之为b4
        //    5）、将b4附加在r2的后面，得到结果r5
        //    6）、将r5进行base58编码，得到结果r6
        //    7）、r6就是地址

        //    同时提供方法将r6恢复为r1
        if (typeof publicKey === 'string') {
            publicKey = Buffer.from(publicKey, 'hex')
        }

        let pub256 = keccak256(publicKey);
        let bufPub256 = Buffer.from(pub256, 'hex');
        let r1 = crypto.createHash('ripemd160').update(bufPub256).digest('hex');
        let r2 = "00" + r1;
        let a = Buffer.from(r1, 'hex');
        let b = keccak256(a);
        let c = Buffer.from(b, 'hex');
        let r3 = keccak256(c);
        let b4 = r3.substring(0, 8);
        let r5 = r2 + b4;
        return new bs58().encode(this.Hex2Array(r5));
    }

    // 地址转公钥哈希 string -> string
    addressToPublicKeyHash(address) {
        let _r5 = new bs58().decode(address);
        let r5 = this.buf2hex(_r5);
        let r2 = r5.substring(0, r5.length - 8);
        return r2.substring(2, r2.length)
    }

    // 公钥转公钥哈希 string/Buffer -> Buffer
    publicKeyToHash(publicKey) {
        if (typeof publicKey === 'string') {
            publicKey = Buffer.from(publicKey, 'hex')
        }
        const addr = this.publicKeyToAddress(
            publicKey
        )
        return this.addressToPublicKeyHash(addr)
    }

}

module.exports = new Index();

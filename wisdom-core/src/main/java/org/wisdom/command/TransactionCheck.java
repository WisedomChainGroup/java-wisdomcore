/*
 * Copyright (c) [2018]
 * This file is part of the java-wisdomcore
 *
 * The java-wisdomcore is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The java-wisdomcore is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with the java-wisdomcore. If not, see <http://www.gnu.org/licenses/>.
 */

package org.wisdom.command;

import com.google.protobuf.InvalidProtocolBufferException;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.wisdom.ApiResult.APIResult;
import org.wisdom.crypto.ed25519.Ed25519PublicKey;
import org.wisdom.encoding.BigEndian;
import org.wisdom.keystore.crypto.RipemdUtility;
import org.wisdom.keystore.crypto.SHA3Utility;
import org.wisdom.protobuf.tcp.command.HatchModel;
import org.wisdom.util.ByteUtil;
import org.wisdom.core.WisdomBlockChain;
import org.wisdom.core.account.AccountDB;
import org.wisdom.core.account.Transaction;
import org.wisdom.core.incubator.Incubator;
import org.wisdom.core.incubator.IncubatorDB;
import org.wisdom.core.incubator.RateTable;
import org.springframework.scheduling.annotation.Async;


import java.util.Arrays;

@Async
public class TransactionCheck {

    public static APIResult TransactionVerifyResult(byte[] transfer, WisdomBlockChain wisdomBlockChain, Configuration configuration, AccountDB accountDB, IncubatorDB incubatorDB, RateTable rateTable, long nowheight, boolean b){
        APIResult apiResult=new APIResult();
        //version
        byte[] version = ByteUtil.bytearraycopy(transfer,0,1);
        if (version[0] != 0x01) {
            apiResult.setCode(-1);
            apiResult.setMessage("Failed to verify version number");
            return apiResult;
        }

        //sha3-256
        byte[] transha = ByteUtil.bytearraycopy(transfer, 1, 32);
        byte[] tranlast = ByteUtil.bytearraycopy(transfer, 33, transfer.length - 33);
        byte[] trannew = ByteUtil.prepend(tranlast, version[0]);
        if (!Arrays.equals(transha, SHA3Utility.keccak256(trannew))) {
            apiResult.setCode(-1);
            apiResult.setMessage("Failed to check hash value");
            return apiResult;
        }

        //type
        byte[] type = ByteUtil.bytearraycopy(tranlast, 0, 1);
        tranlast= ByteUtil.bytearraycopy(tranlast,1,tranlast.length-1);
        //nonce
        byte[] noncebyte=ByteUtil.bytearraycopy(tranlast,0,8);
        long nonce=BigEndian.decodeUint64(noncebyte);
        tranlast=ByteUtil.bytearraycopy(tranlast,8,tranlast.length-8);
        //frompubkey
        byte[] frompubkey=ByteUtil.bytearraycopy(tranlast,0,32);
        tranlast=ByteUtil.bytearraycopy(tranlast,32,tranlast.length-32);
        //nownonce
        byte[] frompubhash=RipemdUtility.ripemd160(SHA3Utility.keccak256(frompubkey));
        String s=Hex.encodeHexString(frompubhash);
        long maxnonce=accountDB.getNonce(frompubhash);
        if((maxnonce+1)!=nonce){
            apiResult.setCode(-1);
            apiResult.setMessage("Nonce don't match");
            return apiResult;
        }

        //gasPrice
        byte[] gasbyte=ByteUtil.bytearraycopy(tranlast,0,8);
        long gasPrice=BigEndian.decodeUint64(gasbyte);
        //gas
        long gas=0;
        if(12>=type[0] && type[0]>=0){
            gas= Transaction.GAS_TABLE[type[0]];
        }else{
            apiResult.setCode(-1);
            apiResult.setMessage("Not matching the right type of thing");
            return apiResult;
        }
        //fee
        if(b){
            if((gasPrice*gas)<configuration.getMin_procedurefee()){
                apiResult.setCode(-1);
                apiResult.setMessage("Low commission");
                return apiResult;
            }
        }
        tranlast=ByteUtil.bytearraycopy(tranlast,8,tranlast.length-8);
        //amount
        byte[] amountbyte=ByteUtil.bytearraycopy(tranlast,0,8);
        long amount=ByteUtil.byteArrayToLong(amountbyte);
        if(type[0]==0x03){//存证
            if(amount!=0){
                apiResult.setCode(-1);
                apiResult.setMessage("The amount of the deposit transaction is 0");
                return apiResult;
            }
        }
        long nowbalance=accountDB.getBalance(frompubhash);
        if(type[0]==0x01 || type[0]==0x09 ){
            if((amount+gasPrice*gas)>nowbalance){
                apiResult.setCode(-1);
                apiResult.setMessage("Not sufficient funds");
                return apiResult;
            }
        }else if(type[0]==0x03 || type[0]==0x0a || type[0]==0x0b || type[0]==0x0c){
            if(gasPrice*gas>nowbalance){
                apiResult.setCode(-1);
                apiResult.setMessage("Not sufficient funds");
                return apiResult;
            }
        }else if(type[0]==0x02){//vote
            if((amount+gasPrice*gas)>nowbalance){
                apiResult.setCode(-1);
                apiResult.setMessage("Not sufficient funds");
                return apiResult;
            }
            //求余
            int remainder=(int)amount % 100000000;
            if(remainder!=0){
                apiResult.setCode(-1);
                apiResult.setMessage("Illegal number of votes");
                return apiResult;
            }
        }
        tranlast=ByteUtil.bytearraycopy(tranlast,8,tranlast.length-8);
        //sig
        byte[] sigdate=ByteUtil.bytearraycopy(tranlast,0,64);
        tranlast=ByteUtil.bytearraycopy(tranlast,64,tranlast.length-64);
        //topubkeyhash
        byte[] topubkeyhash=ByteUtil.bytearraycopy(tranlast,0,20);
        if( type[0]==0x09 || type[0]==0x0a || type[0]==0x0b || type[0]==0x0c){
            if(!Arrays.equals(frompubhash,topubkeyhash)){
                apiResult.setCode(-1);
                apiResult.setMessage("From and To address inconsistency");
                return apiResult;
            }
        }
        tranlast=ByteUtil.bytearraycopy(tranlast,20,tranlast.length-20);
        //bytelength
        byte[] date=ByteUtil.bytearraycopy(tranlast,0,4);
        int legnth=ByteUtil.byteArrayToInt(date);
        if(type[0]!=0x01 && type[0]!=0x02){
            if(legnth==0){
                apiResult.setCode(-1);
                apiResult.setMessage("Payload is not null");
                return apiResult;
            }
        }
        if(legnth>0){
            tranlast=ByteUtil.bytearraycopy(tranlast,4,tranlast.length-4);
            byte[] Payload=ByteUtil.bytearraycopy(tranlast,0,legnth);
            boolean result=PayloadCheck(Payload,type,amount,wisdomBlockChain,configuration,accountDB,incubatorDB,rateTable,nowheight);
            if(!result){
                apiResult.setCode(-1);
                apiResult.setMessage("Payload data illegal");
                return apiResult;
            }
            date=ByteUtil.merge(date,Payload);
        }
        //sigcheck
        byte[] nullsig=new byte[64];
        byte[] nosig=ByteUtil.merge(version,type,noncebyte,frompubkey,gasbyte,amountbyte,nullsig,topubkeyhash,date);
        Ed25519PublicKey ed25519PublicKey=new Ed25519PublicKey(frompubkey);
        boolean result=ed25519PublicKey.verify(nosig,sigdate);
        if(!result){
            apiResult.setCode(-1);
            apiResult.setMessage("Signature verification failed");
            return apiResult;
        }
        apiResult.setCode(1);
        apiResult.setMessage("SUCCESS");
        return apiResult;
    }


    public static boolean PayloadCheck(byte[] payload, byte[] type, long amount, WisdomBlockChain wisdomBlockChain, Configuration configuration, AccountDB accountDB, IncubatorDB incubatorDB, RateTable rateTable, long nowheight){
        try {
            if(type[0]==0x09){//孵化器
                //本金校验
                if(amount<30000000000L){
                    return false;
                }
                String s=Hex.encodeHexString(payload);
                HatchModel.Payload payloadproto=HatchModel.Payload.parseFrom(payload);
                int days=payloadproto.getType();
                if(days!=120 && days!=365){
                    return false;
                }
                double nowrate=rateTable.selectrate(nowheight,days);
                //利息和分享收益
                long interest=(long)(amount*days*nowrate);
                String sharpub=payloadproto.getSharePubkeyHash();
                if(sharpub!=null && sharpub!=""){
                    if(Hex.decodeHex(sharpub.toCharArray()).length!=20){
                        return false;
                    }else{
                        long sharIncome=(long)(interest*0.1);
                        interest=interest+sharIncome;
                    }
                }
                //查询总地址余额
                long total=accountDB.getBalance(IncubatorAddress.resultpubhash());
                if(total<interest){
                    return false;
                }
            }else if(type[0]==0x0a || type[0]==0x0b){//提取利息、提取分享收益
                if(payload.length!=32){//事务哈希
                    return false;
                }
               Transaction transaction=wisdomBlockChain.getTransaction(payload);
                if(transaction==null){
                    return false;
                }
                byte[] tranpayload=transaction.payload;
                HatchModel.Payload payloadproto=HatchModel.Payload.parseFrom(tranpayload);
                int days=payloadproto.getType();
                double rate=rateTable.selectrate(transaction.height,days);
                long capital=transaction.amount;
                long totalrate=(long)(capital*rate);
                Incubator incubator=incubatorDB.selectIncubator(payload);
                if(incubator==null){
                    return false;
                }
                //最后提取时间
                long inheight=0;
                if(type[0]==0x0b){//提取分享收益
                    if(incubator.getLast_blockheight_interest()==0 || incubator.getInterest_amount()<amount){
                        return false;
                    }
                    totalrate=(long)(totalrate*0.1);
                    inheight=incubator.getLast_blockheight_share();
                }else{//提取利息
                    if(incubator.getLast_blockheight_interest()==0 || incubator.getShare_amount()<amount){
                        return false;
                    }
                    inheight=incubator.getLast_blockheight_interest();
                }

                //天数
                int mul=(int)(amount/totalrate);
                if(mul==0){
                    return false;
                }
                int blockcount=mul*configuration.getDay_count();

                if((inheight+blockcount)>nowheight){
                    return false;
                }
            }else if(type[0]==0x03){//存证
                if(payload.length>1000){
                    return false;
                }
            }else if(type[0]==0x0c){//提取本金
                if(payload.length!=32){//事务哈希
                    return false;
                }

                Incubator incubator=incubatorDB.selectIncubator(payload);
                if(incubator==null){
                    return false;
                }
                if(incubator.getInterest_amount()!=0 || incubator.getCost()==0){
                    return false;
                }
                if(amount!=incubator.getCost() || amount==0){
                    return false;
                }
            }
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
            return false;
        } catch (DecoderException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
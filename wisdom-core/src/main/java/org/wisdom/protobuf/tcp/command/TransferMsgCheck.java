package org.wisdom.protobuf.tcp.command;

import org.apache.commons.codec.binary.Hex;
import org.wisdom.ApiResult.APIResult;
import org.wisdom.command.Configuration;
import org.wisdom.command.TransactionCheck;
import org.wisdom.core.TransactionPool;
import org.wisdom.protobuf.tcp.ProtocolModel;
import org.wisdom.core.WisdomBlockChain;
import org.wisdom.core.account.AccountDB;
import org.wisdom.core.account.Transaction;
import org.wisdom.core.incubator.IncubatorDB;
import org.wisdom.core.incubator.RateTable;


public class TransferMsgCheck {

    public static boolean TransferDepositCheck(WisdomBlockChain wisdomBlockChain, TransactionPool transactionPool, ProtocolModel.Transaction msg, Configuration configuration, AccountDB accountDB, IncubatorDB incubatorDB, RateTable rateTable, long nowheight){
        //事务已在内存池中
        if (!transactionPool.has(Hex.encodeHexString(msg.getHash().toByteArray()))) {
            Transaction tran=Transaction.fromProto(msg);
            APIResult apiResult= TransactionCheck.TransactionVerifyResult(tran.toRPCBytes(), wisdomBlockChain, configuration,accountDB, incubatorDB, rateTable, nowheight, true);
            if(apiResult.getStatusCode()==-1){
                return false;
            }
            return true;
        }else{
            return false;
        }
    }




   /* public Transaction TransferDepositCheck(ProtocolModel.CommandMessage msg, UTXOSets state, long procedurefee, long height) {
        //事务已在内存池中
        if (!TransactionPoolState.poolexists(new String(msg.getHash().toByteArray()))) {
            try {
                //version
                byte[] version = msg.getVersion().toByteArray();
                if (version[0] != 0x01) {
                    return null;
                }
                //type
                byte[] type = new byte[1];
                type[0] = (byte)msg.getType().getNumber();
                //sha3-256
                byte[] transha = msg.getHash().toByteArray();
                //inscript
                byte[] inscript = msg.getInPoints().toByteArray();
                //outsrcirpt
                byte[] outscript = msg.getOutPoints().toByteArray();
                //locktime
                long locktime = msg.getLockTime();
                byte[] locktimebyte = BigEndian.encodeUint32(locktime);
                byte[] trantext = ByteUtil.merge(version, type, inscript, outscript, locktimebyte);
                if (!Arrays.equals(transha, SHA3Utility.sha3256(trantext))) {
                    return null;
                }

                //inpoint sig checkout
                byte[] tranlast = ByteUtil.merge(type, inscript, outscript, locktimebyte);
                boolean result=false;
                if(type[0]==0x01 || type[0]==0x03 || type[0]==0x02 || type[0]==0x06){
                    result= new TransactionCheck().publicAffairs(tranlast, type[0],version[0],transha,state,procedurefee,0,0);
                }else if(type[0]==0x04 || type[0]==0x05){
                    result=new TransactionCheck().signatureCheck(tranlast,type[0],version[0],transha,state,procedurefee);
                }else if(type[0]==0x09){
                    result= new TransactionCheck().publicAffairs(tranlast, type[0],version[0],transha,state,procedurefee,120,height);
                }else if(type[0]==0x0a){
                    result= new TransactionCheck().publicAffairs(tranlast, type[0],version[0],transha,state,procedurefee,365,height);
                }else if(type[0]==0x0b || type[0]==0x0c){
                    result= new TransactionCheck().publicAffairs(tranlast, type[0],version[0],transha,state,procedurefee,0,height);
                }
                if (!result) {
                    return null;
                }
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }else{
            return null;
        }
        //放入内存池中
        Transaction t = new Transaction().fromProto(msg);
        state.updateUTXOcite(t);
        TransactionPoolState.addpool(new String(msg.getHash().toByteArray()), t);
        return t;
    }

    public Transaction CoinbaserCheck(ProtocolModel.CommandMessage msg){
        //version
        byte[] version = msg.getVersion().toByteArray();
        if (version[0] != 0x01) {
            return null;
        }
        //type
        byte[] type = new byte[1];
        type[0] = (byte)msg.getType().getNumber();
        if(type[0]!=0x00){
            return null;
        }
        //sha3-256
        byte[] transha = msg.getHash().toByteArray();
        //inscript
        byte[] inscript = msg.getInPoints().toByteArray();
        //outsrcirpt
        byte[] outscript = msg.getOutPoints().toByteArray();
        //locktime
        long locktime = msg.getLockTime();
        byte[] locktimebyte = BigEndian.encodeUint32(locktime);
        byte[] trantext = ByteUtil.merge(version, type, inscript, outscript, locktimebyte);
        if (!Arrays.equals(transha, SHA3Utility.sha3256(trantext))) {
            return null;
        }
        //inpoint sig checkout
        byte[] tranlast = ByteUtil.merge(type, inscript, outscript, locktimebyte);
        byte[] traninbyte = ByteUtil.bytearraycopy(tranlast, 1, tranlast.length - 1);
        //输入数量
        int incount = traninbyte[0];
        if (incount !=1) {
            return null;
        }
        byte[] inlist = ByteUtil.bytearraycopy(traninbyte, 1, 136);
        //前置交易哈希
        byte[] befortransha = ByteUtil.bytearraycopy(inlist, 0, 32);
        if(!ByteUtil.checkZero(befortransha)){
            return null;
        }
        //前置输出索引
        byte[] beforindexbyte = ByteUtil.bytearraycopy(inlist, 32, 4);
        int beforindex = ByteUtil.byteArrayToInt(beforindexbyte);
        if(beforindex!=1){
            return null;
        }
        //脚本大小
        int insrciplength = ByteUtil.byteArrayToInt(ByteUtil.bytearraycopy(inlist, 36, 4));
        if (insrciplength != 96) {
//                 "Validation input script length is invalid, valid byte length is 96";
            return null;
        }
        //输入后面
        byte[] traninlast = ByteUtil.bytearraycopy(traninbyte, 136 + 1, traninbyte.length - 1 - 136);
        boolean result= new TransactionCheck().outcoinbaseCheck(befortransha,traninlast);
        if(!result){
            return null;
        }
        Transaction t = new Transaction().fromProto(msg);
        return t;
    }*/

}

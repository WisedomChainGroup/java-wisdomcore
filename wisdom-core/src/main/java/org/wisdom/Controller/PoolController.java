package org.wisdom.Controller;

import com.alibaba.fastjson.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.wisdom.ApiResult.APIResult;
import org.wisdom.core.TransactionPool;
import org.wisdom.core.account.Transaction;
import org.wisdom.keystore.crypto.RipemdUtility;
import org.wisdom.keystore.crypto.SHA3Utility;
import org.wisdom.keystore.wallet.KeystoreAction;
import org.wisdom.pool.AdoptTransPool;
import org.wisdom.pool.PeningTransPool;
import org.wisdom.pool.TransPool;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@RestController
public class PoolController {

    @Autowired
    TransactionPool transactionPool;

    @Autowired
    AdoptTransPool adoptTransPool;

    @Autowired
    PeningTransPool peningTransPool;

    @RequestMapping(value="/getPoolAddress",method = RequestMethod.GET)
    public Object getPoolAddress(@RequestParam("address") String address){
        JSONArray jsonArray = new JSONArray();
        int check=KeystoreAction.verifyAddress(address);
        if(check==0){
            byte[] pubkeyhash=KeystoreAction.addressToPubkeyHash(address);
            List<TransPool> adoptpool=adoptTransPool.getAllFrom(Hex.encodeHexString(pubkeyhash));
            for(TransPool transPool:adoptpool){
                Transaction transaction=transPool.getTransaction();
                JSONObject json = new JSONObject();
                json.put("pool","AdoptTransPool");
                json.put("tranhaxh",Hex.encodeHexString(transaction.getHash()));
                json.put("type",transaction.type);
                json.put("nonce",transaction.nonce);
                json.put("from",transaction.from);
                json.put("fromhash", Hex.encodeHexString(RipemdUtility.ripemd160(SHA3Utility.keccak256(transaction.from))));
                json.put("amount",transaction.amount);
                json.put("to",Hex.encodeHexString(transaction.to));
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date date = new Date(transPool.getDatetime());
                json.put("datatime",sdf.format(date));
                jsonArray.add(json);
            }
            List<TransPool> pendingpool=peningTransPool.getAll();
            for(TransPool transPool:pendingpool){
                Transaction transaction=transPool.getTransaction();
                if(Arrays.equals(RipemdUtility.ripemd160(SHA3Utility.keccak256(transaction.from)),pubkeyhash)){
                    JSONObject json = new JSONObject();
                    json.put("pool","PendingTransPool");
                    json.put("tranhaxh",Hex.encodeHexString(transaction.getHash()));
                    json.put("type",transaction.type);
                    json.put("nonce",transaction.nonce);
                    json.put("from",transaction.from);
                    json.put("fromhash", Hex.encodeHexString(RipemdUtility.ripemd160(SHA3Utility.keccak256(transaction.from))));
                    json.put("amount",transaction.amount);
                    json.put("to",Hex.encodeHexString(transaction.to));
                    json.put("state",transPool.getState());
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date date = new Date(transPool.getDatetime());
                    json.put("datatime",sdf.format(date));
                    json.put("height",transPool.getHeight());
                    jsonArray.add(json);
                }
            }
            return APIResult.newFailResult(2000,"SUCCESS",jsonArray);
        }else{
            return APIResult.newFailResult(5000,"Address check error");
        }
    }

    @RequestMapping(value="/getPoolTranhash",method = RequestMethod.GET)
    public Object getPoolTranhash(@RequestParam("tranhash") String tranhash){
        try {
            byte[] txhash=Hex.decodeHex(tranhash.toCharArray());
            TransPool adoptpool=adoptTransPool.getPoolTranHash(txhash);
            if(adoptpool!=null){
                Transaction transaction=adoptpool.getTransaction();
                JSONObject json = new JSONObject();
                json.put("pool","AdoptTransPool");
                json.put("tranhaxh",Hex.encodeHexString(transaction.getHash()));
                json.put("type",transaction.type);
                json.put("nonce",transaction.nonce);
                json.put("from",transaction.from);
                json.put("fromhash", Hex.encodeHexString(RipemdUtility.ripemd160(SHA3Utility.keccak256(transaction.from))));
                json.put("amount",transaction.amount);
                json.put("to",Hex.encodeHexString(transaction.to));
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date date = new Date(adoptpool.getDatetime());
                json.put("datatime",sdf.format(date));
                return APIResult.newFailResult(2000,"SUCCESS",json);
            }
            List<TransPool> pendingpool=peningTransPool.getAll();
            for(TransPool transPool:pendingpool){
                Transaction transaction=transPool.getTransaction();
                if(Arrays.equals(transaction.getHash(),txhash)){
                    JSONObject json = new JSONObject();
                    json.put("pool","PendingTransPool");
                    json.put("tranhaxh",Hex.encodeHexString(transaction.getHash()));
                    json.put("type",transaction.type);
                    json.put("nonce",transaction.nonce);
                    json.put("from",transaction.from);
                    json.put("fromhash", Hex.encodeHexString(RipemdUtility.ripemd160(SHA3Utility.keccak256(transaction.from))));
                    json.put("amount",transaction.amount);
                    json.put("to",Hex.encodeHexString(transaction.to));
                    json.put("state",transPool.getState());
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date date = new Date(transPool.getDatetime());
                    json.put("datatime",sdf.format(date));
                    json.put("height",transPool.getHeight());
                    return APIResult.newFailResult(2000,"SUCCESS",json);
                }
            }
            return APIResult.newFailResult(2000,"Not in memory pool");
        } catch (DecoderException e) {
            return APIResult.newFailResult(5000,"Exception error");
        }
    }

    @RequestMapping(value="/getPoolCount",method = RequestMethod.GET)
    public Object getPoolCount(){
        int adoptcount=adoptTransPool.getAllFull().size();
        List<Transaction> pengdingcount=peningTransPool.compare();
        int pengcount=pengdingcount.size();
        JSONObject json = new JSONObject();
        json.put("adoptcount",adoptcount);
        json.put("pengcount",pengcount);
        return APIResult.newFailResult(2000,"SUCCESS",json);
    }
}

package org.wisdom.Controller;

import net.sf.json.JSONArray;
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
import org.wisdom.pool.PendingNonce;
import org.wisdom.pool.PeningTransPool;
import org.wisdom.pool.TransPool;

import java.text.SimpleDateFormat;
import java.util.*;

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
                json.put("traninfo",Hex.encodeHexString(transaction.toRPCBytes()));
                json.put("tranhaxh",Hex.encodeHexString(transaction.getHash()));
                json.put("type",transaction.type);
                json.put("nonce",transaction.nonce);
                json.put("fromhash", Hex.encodeHexString(RipemdUtility.ripemd160(SHA3Utility.keccak256(transaction.from))));
                json.put("amount",transaction.amount);
                json.put("fee",transaction.getFee());
                json.put("to",Hex.encodeHexString(transaction.to));
                if(transaction.payload==null){
                    json.put("payload",null);
                }else{
                    json.put("payload",Hex.encodeHexString(transaction.payload));
                }
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date date = new Date(transPool.getDatetime());
                json.put("datatime",sdf.format(date));
                jsonArray.add(json);
            }
            List<TransPool> pendingpool=peningTransPool.getAllFrom(Hex.encodeHexString(pubkeyhash));
            for(TransPool transPool:pendingpool){
                Transaction transaction=transPool.getTransaction();
                JSONObject json = new JSONObject();
                json.put("pool","PendingTransPool");
                json.put("traninfo",Hex.encodeHexString(transaction.toRPCBytes()));
                json.put("tranhaxh",Hex.encodeHexString(transaction.getHash()));
                json.put("type",transaction.type);
                json.put("nonce",transaction.nonce);
                json.put("fromhash", Hex.encodeHexString(RipemdUtility.ripemd160(SHA3Utility.keccak256(transaction.from))));
                json.put("amount",transaction.amount);
                json.put("fee",transaction.getFee());
                json.put("to",Hex.encodeHexString(transaction.to));
                if(transaction.payload==null){
                    json.put("payload",null);
                }else{
                    json.put("payload",Hex.encodeHexString(transaction.payload));
                }
                json.put("state",transPool.getState());
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date date = new Date(transPool.getDatetime());
                json.put("datatime",sdf.format(date));
                json.put("height",transPool.getHeight());
                jsonArray.add(json);
            }
            List<JSONObject> jsonValues = new ArrayList<JSONObject>();
            for (int i = 0; i < jsonArray.size(); i++) {
                jsonValues.add(jsonArray.getJSONObject(i));
            }
            Collections.sort(jsonValues, new Comparator<JSONObject>() {
                @Override
                public int compare(JSONObject o1, JSONObject o2) {
                    long nonce1=o1.getLong("nonce");
                    long nonce2=o2.getLong("nonce");
                    return (int)(nonce1-nonce2);
                }
            });
            JSONArray jsonArray1=JSONArray.fromObject(jsonValues.toString());
            return APIResult.newFailResult(2000,"SUCCESS",jsonArray1);
        }else{
            return APIResult.newFailResult(5000,"Address check error");
        }
    }

    @RequestMapping(value="/getPoolTranhash",method = RequestMethod.GET)
    public Object getPoolTranhash(@RequestParam("tranhash") String tranhash){
        try {
            if(tranhash!=null && !tranhash.equals("")){
                byte[] txhash=Hex.decodeHex(tranhash.toCharArray());
                TransPool adoptpool=adoptTransPool.getPoolTranHash(txhash);
                if(adoptpool!=null){
                    Transaction transaction=adoptpool.getTransaction();
                    JSONObject json = new JSONObject();
                    json.put("pool","AdoptTransPool");
                    json.put("traninfo",Hex.encodeHexString(transaction.toRPCBytes()));
                    json.put("tranhaxh",Hex.encodeHexString(transaction.getHash()));
                    json.put("type",transaction.type);
                    json.put("nonce",transaction.nonce);
                    json.put("fromhash", Hex.encodeHexString(RipemdUtility.ripemd160(SHA3Utility.keccak256(transaction.from))));
                    json.put("amount",transaction.amount);
                    json.put("fee",transaction.getFee());
                    json.put("to",Hex.encodeHexString(transaction.to));
                    if(transaction.payload==null){
                        json.put("payload",null);
                    }else{
                        json.put("payload",Hex.encodeHexString(transaction.payload));
                    }
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date date = new Date(adoptpool.getDatetime());
                    json.put("datatime",sdf.format(date));
                    return APIResult.newFailResult(2000,"SUCCESS",json);
                }
                TransPool pendingpool=peningTransPool.getPoolTranHash(txhash);
                if(pendingpool!=null){
                    Transaction transaction=pendingpool.getTransaction();
                    JSONObject json = new JSONObject();
                    json.put("pool","PendingTransPool");
                    json.put("traninfo",Hex.encodeHexString(transaction.toRPCBytes()));
                    json.put("tranhaxh",Hex.encodeHexString(transaction.getHash()));
                    json.put("type",transaction.type);
                    json.put("nonce",transaction.nonce);
                    json.put("fromhash", Hex.encodeHexString(RipemdUtility.ripemd160(SHA3Utility.keccak256(transaction.from))));
                    json.put("amount",transaction.amount);
                    json.put("fee",transaction.getFee());
                    json.put("to",Hex.encodeHexString(transaction.to));
                    if(transaction.payload==null){
                        json.put("payload",null);
                    }else{
                        json.put("payload",Hex.encodeHexString(transaction.payload));
                    }
                    json.put("state",pendingpool.getState());
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date date = new Date(pendingpool.getDatetime());
                    json.put("datatime",sdf.format(date));
                    json.put("height",pendingpool.getHeight());
                    return APIResult.newFailResult(2000,"SUCCESS",json);
                }
                return APIResult.newFailResult(2000,"Not in memory pool");
            }else{
                return APIResult.newFailResult(5000,"The parameter address cannot be empty or null");
            }
        } catch (DecoderException e) {
            return APIResult.newFailResult(5000,"Exception error");
        }
    }

    @RequestMapping(value="/getPoolCount",method = RequestMethod.GET)
    public Object getPoolCount(){
        int adoptcount=adoptTransPool.getAllFull().size();
        List<TransPool> pengdingcount=peningTransPool.getAllnostate();
        int pengcount=pengdingcount.size();
        JSONObject json = new JSONObject();
        json.put("adoptcount",adoptcount);
        json.put("pengcount",pengcount);
        return APIResult.newFailResult(2000,"SUCCESS",json);
    }

    @RequestMapping(value="/getPtNonce",method = RequestMethod.GET)
    public Object getPtNonce(@RequestParam("address") String address){
        try{
            byte[] pubkeyhash=KeystoreAction.addressToPubkeyHash(address);
            PendingNonce pendingNonce=peningTransPool.findptnonce(Hex.encodeHexString(pubkeyhash));
            return APIResult.newFailResult(2000,"SUCCESS",pendingNonce);
        }catch (Exception e){
            return APIResult.newFailResult(5000,"Address error");
        }
    }

    @RequestMapping(value="/deletePendpool",method = RequestMethod.POST)
    public Object deletePendpool(@RequestParam("tokenhash") String tokenhash,
                                 @RequestParam("txhash") String txhash){
        try {
            byte[] hash=Hex.decodeHex(tokenhash.toCharArray());
            byte[] shahash=SHA3Utility.sha3256(hash);
            String token=Hex.encodeHexString(shahash);
            if(!token.equals("a772c260ae19e8972f1da3af77492fdb6b40f34a9b34b4a9021ecfd900f21e53")){
                return APIResult.newFailResult(5000,"Token check but");
            }
            TransPool transPool=peningTransPool.getPoolTranHash(Hex.decodeHex(txhash.toCharArray()));
            if(transPool!=null){
                Transaction transaction=transPool.getTransaction();
                String fromhash=Hex.encodeHexString(RipemdUtility.ripemd160(SHA3Utility.keccak256(transaction.from)));
                peningTransPool.removeOne(fromhash,transaction.nonce);
                peningTransPool.removePendingnonce(fromhash);
                return APIResult.newFailResult(2000,"SUCCESS");
            }else{
                return  APIResult.newFailResult(5000,"Transaction hash query not found");
            }
        } catch (DecoderException e) {
            return APIResult.newFailResult(5000,"Token conversions are problematic 16");
        }
    }

    @RequestMapping(value="/updatePtNonce",method = RequestMethod.POST)
    public Object updatePtNonce(@RequestParam("tokenhash") String tokenhash, @RequestParam("address") String address,
                                @RequestParam("nonce") long nonce, @RequestParam("state") int state){
        try{
            byte[] hash=Hex.decodeHex(tokenhash.toCharArray());
            byte[] shahash=SHA3Utility.sha3256(hash);
            String token=Hex.encodeHexString(shahash);
            if(!token.equals("a772c260ae19e8972f1da3af77492fdb6b40f34a9b34b4a9021ecfd900f21e53")){
                return APIResult.newFailResult(5000,"Token check but");
            }
            byte[] pubkeyhash=KeystoreAction.addressToPubkeyHash(address);
            PendingNonce pendingNonce=new PendingNonce(nonce,state);
            peningTransPool.updatePtNone(Hex.encodeHexString(pubkeyhash),pendingNonce);
            return APIResult.newFailResult(2000,"SUCCESS");
        }catch (Exception e){
            return APIResult.newFailResult(5000,"Address error");
        }
    }
}

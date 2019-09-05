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
                json.put("from",Hex.encodeHexString(transaction.from));
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
                    json.put("traninfo",Hex.encodeHexString(transaction.toRPCBytes()));
                    json.put("tranhaxh",Hex.encodeHexString(transaction.getHash()));
                    json.put("type",transaction.type);
                    json.put("nonce",transaction.nonce);
                    json.put("from",Hex.encodeHexString(transaction.from));
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
                        json.put("traninfo",Hex.encodeHexString(transaction.toRPCBytes()));
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
        List<Transaction> pengdingcount=peningTransPool.compare();
        int pengcount=pengdingcount.size();
        JSONObject json = new JSONObject();
        json.put("adoptcount",adoptcount);
        json.put("pengcount",pengcount);
        return APIResult.newFailResult(2000,"SUCCESS",json);
    }

    @RequestMapping(value="/deletePendpool",method = RequestMethod.POST)
    public Object deletePendpool(@RequestParam("tokenhash") String tokenhash,
                                 @RequestParam("from") String from,
                                 @RequestParam("nonce") long nonce,
                                 @RequestParam("txhash") String txhash){
        try {
            byte[] hash=Hex.decodeHex(tokenhash.toCharArray());
            byte[] shahash=SHA3Utility.sha3256(hash);
            String token=Hex.encodeHexString(shahash);
            if(!token.equals("a772c260ae19e8972f1da3af77492fdb6b40f34a9b34b4a9021ecfd900f21e53")){
                return APIResult.newFailResult(5000,"Token check but");
            }
            String key=from+nonce;
            byte[] txhashbyte=Hex.decodeHex(txhash.toCharArray());
            if(!peningTransPool.hasExist(key)){
                Map<String, TransPool> pendingmap=peningTransPool.getPtpool();
                TransPool transPool=pendingmap.get(key);
                if(Arrays.equals(transPool.getTransaction().getHash(),txhashbyte)){
                    peningTransPool.removeOne(key,from,nonce);
                    return APIResult.newFailResult(2000,"SUCCESS");
                }else{
                    return APIResult.newFailResult(5000,"Transaction hash query not found");
                }
            }else{
                return APIResult.newFailResult(5000,"The address does not exist in the event pending pool");
            }
        } catch (DecoderException e) {
            return APIResult.newFailResult(5000,"Token conversions are problematic 16");
        }
    }

    @RequestMapping(value="/updatePtNonce",method = RequestMethod.GET)
    public Object getPtNonce(@RequestParam("key") String key){
        PendingNonce pendingNonce=peningTransPool.getpt(key);
        if(pendingNonce==null){
            return APIResult.newFailResult(5000,"Query does not exist");
        }
        return APIResult.newFailResult(2000,"SUCCESS",pendingNonce);
    }

    @RequestMapping(value="/updatePtNonce",method = RequestMethod.POST)
    public Object updatePtNonce(@RequestParam("tokenhash") String tokenhash, @RequestParam("pubkey") String pubkey,
                                @RequestParam("nonce") long nonce, @RequestParam("state") int state){
        try{
            byte[] hash=Hex.decodeHex(tokenhash.toCharArray());
            byte[] shahash=SHA3Utility.sha3256(hash);
            String token=Hex.encodeHexString(shahash);
            if(!token.equals("a772c260ae19e8972f1da3af77492fdb6b40f34a9b34b4a9021ecfd900f21e53")){
                return APIResult.newFailResult(5000,"Token check but");
            }
            PendingNonce pendingNonce=peningTransPool.getpt(pubkey);
            if(pendingNonce!=null){
                pendingNonce.setState(state);
                pendingNonce.setNonce(nonce);
            }else{
                pendingNonce=new PendingNonce(nonce,state);
            }
            peningTransPool.updatePtNone(pubkey,pendingNonce);
            return APIResult.newFailResult(2000,"SUCCESS");
        }catch (Exception e){
            return APIResult.newFailResult(5000,"Address error");
        }
    }
}

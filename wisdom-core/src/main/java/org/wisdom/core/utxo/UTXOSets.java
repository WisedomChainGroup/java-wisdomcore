package org.wisdom.core.utxo;

import org.apache.commons.codec.binary.Hex;
import org.wisdom.core.WisdomBlockChain;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.*;

@EnableScheduling
public class UTXOSets {

    @Autowired
    WisdomBlockChain wisdomBlockChain;

    @Autowired
    private JdbcTemplate tmpl;

    // key -> out point
    private HashMap<String, UTXO> utxos;
    // owner address -> keys
    private HashMap<String, Set<String>> utxoKeys;

    public UTXOSets() {
        this.utxos = new HashMap<>();
        this.utxoKeys = new HashMap<>();
    }

    // key = owneraddress + transaction hash + index
    private String getUTXOKey(String address, byte[] txHash, int index) {
        return address + Hex.encodeHexString(txHash) + index;
    }

    private void deleteUTXO(String key,UTXO utxo) {
        utxos.remove(key);
        Set<String> keys = utxoKeys.get(utxo.getAddress());
        if(keys!=null){
            keys.remove(key);
            utxoKeys.put(utxo.getAddress(),keys);
        }
        String id=getUTXOKey(utxo.getAddress(),utxo.getHash(),utxo.getIndex());
        tmpl.update("delete from utxo where id=?", new Object[]{id});
    }

    private void addOutPoint(String owner, byte[] txHash, int index, UTXO utxo) {
        Set<String> keys = utxoKeys.get(owner);
        if (keys == null) {
            utxoKeys.put(owner, new HashSet<>());
            keys=utxoKeys.get(owner);
        }
        String key = getUTXOKey(owner, txHash, index);
        keys.add(key);
        utxos.put(key, utxo);
    }

    public UTXO getUTXO(String owner, byte[] txHash, int index) {
        if(utxos.containsKey(getUTXOKey(owner,txHash,index))){
            return utxos.get(getUTXOKey(owner,txHash,index));
        }else{
            return null;
        }
    }

    public List<UTXO> getUTXOs(String owner){
        List<UTXO> result = new ArrayList<>();
        Set<String> keys = utxoKeys.get(owner);
        if (keys == null || keys.size() == 0) {
            return null;
        }
        for(String key: keys){
            result.add(utxos.get(key));
        }
        return result;
    }

    public void updateUTXOcite(Transaction tx){
        for(InPoint in : tx.getInPoints()){
            String id=getUTXOKey(in.getTransferOwner(), in.getPreviousTransactionHash(), in.getOutPointIndex());
            UTXO oldutxo=getUTXO(in.getTransferOwner(), in.getPreviousTransactionHash(), in.getOutPointIndex());
            if(oldutxo!=null){
                oldutxo.setIs_reference(true);
                utxos.put(id,oldutxo);
                tmpl.update("update utxo u set u.is_reference=true where u.id=?", new Object[]{id});
            }
        }
    }

    public void updateUTXOcite(UTXO utxo){
        String id=getUTXOKey(utxo.getAddress(),utxo.getHash(),utxo.getIndex());
        UTXO oldutxo=getUTXO(utxo.getAddress(),utxo.getHash(),utxo.getIndex());
        if(oldutxo!=null){
            oldutxo.setIs_reference(false);
            utxos.put(id,oldutxo);
            tmpl.update("update utxo u set u.is_reference=false where u.id=?", new Object[]{id});
        }
    }

    public void addUTXO(Transaction tx){
        for(InPoint in:tx.getInPoints()){
            String id=getUTXOKey(in.getTransferOwner(), in.getPreviousTransactionHash(), in.getOutPointIndex());
            UTXO oldutxo=getUTXO(in.getTransferOwner(), in.getPreviousTransactionHash(), in.getOutPointIndex());
            if(oldutxo!=null){
                oldutxo.setIs_reference(true);
                oldutxo.setIs_confirm(true);
                utxos.put(id,oldutxo);
                tmpl.update("update utxo u set u.is_reference=true,u.is_confirm=true where u.id=?", new Object[]{id});
            }
        }
        int index=0;
        for(OutPoint out:tx.getOutPoints()){
            if(out.getAmount()>0){
                UTXO utxo=new UTXO();
                utxo.setTxtype(tx.getType());
                utxo.setHash(tx.getHash());
                utxo.setIndex(index);
                utxo.setAmount(out.getAmount());
                utxo.setHeight(tx.getBlockHeight());
                utxo.setAddress(out.getAddress());
                utxo.setOutscript(out.getScript());
                utxo.setDatascript(out.getDataScript());
                utxo.setIs_reference(false);
                utxo.setIs_confirm(false);
                addOutPoint(out.getAddress(),tx.getHash(),index,utxo);

                tmpl.update("insert into utxo values(?,?,?,?,?,?,?,?,?)",new Object[]{getUTXOKey(out.getAddress(),tx.getHash(),index),
                        tx.getType(),tx.getHash(),index,out.getAmount(),tx.getBlockHeight(),out.getAddress(),out.getScript(),out.getDataScript()});
                index++;
            }
        }
    }

    @Scheduled(cron="0 0 */1 * * ?")
    public void UTXOTask(){
        long height=wisdomBlockChain.currentHeader().nHeight;
        //引用默认2个礼拜
        long restheight=14*5760;
        for (Map.Entry<String, UTXO> entry : utxos.entrySet()) {
            UTXO utxo=entry.getValue();
            long utxoheight=utxo.getHeight();
            if(utxo.isIs_reference()){//被引用
                if(!utxo.isIs_confirm()){//未被确认
                    if((utxoheight+restheight)>=height){//超过两礼拜
                        updateUTXOcite(utxo);
                    }
                }
            }
            if(utxo.isIs_confirm()){//被确认
                //确认默认20个区块
                if((utxoheight+20)>height){
                    deleteUTXO(entry.getKey(),utxo);
                }
            }

        }
    }

}

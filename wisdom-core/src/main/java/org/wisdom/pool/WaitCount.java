package org.wisdom.pool;

import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

@Component
public class WaitCount {

    private ConcurrentHashMap<String,WaitNonce> waitMap;

    public WaitCount(){
        waitMap=new ConcurrentHashMap<>();
    }

    public void add(String pubhash,long nonce){
        waitMap.put(pubhash,new WaitNonce(nonce));
    }

    public boolean IsExist(String pubhash,long nonce){
        if(waitMap.containsKey(pubhash)){
            if(waitMap.get(pubhash).getNonce()==nonce){
                return true;
            }
        }
        return false;
    }

    public boolean updateNonce(String pubhash){
        if(waitMap.containsKey(pubhash)){
            WaitNonce waitNonce=waitMap.get(pubhash);
            int waitcount=waitNonce.getWaitcount();
            waitcount++;
            if(waitcount<=7){
                waitNonce.setWaitcount(waitcount);
                waitMap.put(pubhash,waitNonce);
                return false;
            }
            //满足单个节点最长旷工数量的7个区块，删除
            waitMap.remove(pubhash);
            return true;
        }
        return false;
    }

    private class WaitNonce{
        private long nonce;
        private int waitcount;

        public WaitNonce(long nonce) {
            this.nonce = nonce;
            this.waitcount = 1;
        }

        public long getNonce() {
            return nonce;
        }

        public void setNonce(long nonce) {
            this.nonce = nonce;
        }

        public int getWaitcount() {
            return waitcount;
        }

        public void setWaitcount(int waitcount) {
            this.waitcount = waitcount;
        }
    }
}

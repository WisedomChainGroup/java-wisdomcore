package org.wisdom.controller;

import org.apache.commons.codec.binary.Hex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.wisdom.ApiResult.APIResult;
import org.wisdom.core.WisdomBlockChain;
import org.wisdom.db.StateDB;
import org.wisdom.pool.AdoptTransPool;
import org.wisdom.pool.PeningTransPool;

@RestController
public class ExplorerController {

    public static class ExploreResult {
        // 每日出块数
        public long blocksCount;
        // 当前难度
        public String target;
        // 出块平均时间
        public double averageBlockInterval;
        // 最近十个区块平均手续费
        public long averageFee;
        // 内存池大小
        public long pendingTransactions;
        // 内存池大小
        public long queuedTransactions;
        // 主账本高度
        public long lastConfirmedHeight;
        // forkdb 的最新高度
        public long bestHeight;

        public ExploreResult() {
        }

        public ExploreResult(long blocksCount, String target, double averageBlockInterval, long averageFee, long pendingTransactions, long queuedTransactions, long lastConfirmedHeight, long bestHeight) {
            this.blocksCount = blocksCount;
            this.target = target;
            this.averageBlockInterval = averageBlockInterval;
            this.averageFee = averageFee;
            this.pendingTransactions = pendingTransactions;
            this.queuedTransactions = queuedTransactions;
            this.lastConfirmedHeight = lastConfirmedHeight;
            this.bestHeight = bestHeight;
        }
    }

    @Autowired
    private StateDB stateDB;

    @Autowired
    AdoptTransPool adoptTransPool;

    @Autowired
    PeningTransPool peningTransPool;

    @Autowired
    WisdomBlockChain wisdomBlockChain;

    @GetMapping(value = "/WisdomCore/ExplorerInfo")
    public Object getExplorerInfo() {
        try{
            long blocksCount = stateDB.countBlocksAfter(System.currentTimeMillis() / 1000 - 24 * 60 * 60);
            String target = Hex.encodeHexString(stateDB.getBestBlock().nBits);
            double avgInterval = stateDB.averageBlocksInterval();
            long averageFee=stateDB.AverageFee();
            int adoptcount=adoptTransPool.size();
            int pengcount=peningTransPool.Unpacksize();
            long lastConfirmedHeight=wisdomBlockChain.getCurrentTotalWeight();
            long bestHeight=stateDB.getBestBlock().nHeight;
            return APIResult.newFailResult(2000,"SUCCESS",new ExploreResult(blocksCount,target,avgInterval,averageFee,adoptcount,pengcount,lastConfirmedHeight,bestHeight));
        }catch (Exception e){
            return APIResult.newFailResult(5000, "Exception error");
        }
    }

}

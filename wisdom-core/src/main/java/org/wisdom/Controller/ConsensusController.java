package org.wisdom.Controller;

import org.apache.commons.codec.binary.Hex;
import org.bouncycastle.util.Arrays;
import org.wisdom.p2p.entity.GetBlockQuery;
import org.wisdom.p2p.entity.Status;
import org.wisdom.encoding.JSONEncodeDecoder;
import org.wisdom.core.Block;
import org.wisdom.core.TransactionPool;
import org.wisdom.core.WisdomBlockChain;
import org.wisdom.core.account.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.List;

import static org.wisdom.Controller.ConsensusResult.ERROR;
import static org.wisdom.Controller.ConsensusResult.SUCCESS;

@RestController
public class ConsensusController {
    static final int MAX_BLOCKS_IN_TRANSIT_PER_PEER = 50;
    private static final Logger logger = LoggerFactory.getLogger(ConsensusController.class);

    @Value("${wisdom.consensus.enable-mining}")
    boolean enableMining;


    @Autowired
    private JSONEncodeDecoder codec;

    @Autowired
    private WisdomBlockChain bc;

    @Autowired
    private ConsensusClient consensusClient;

    @Autowired
    private Block genesis;

    @Autowired
    TransactionPool pool;

    @GetMapping(value = "/consensus/blocks")
    public Object handleGetBlocks(
            @RequestParam(name = "start") long start,
            @RequestParam(name = "stop") long stop,
            @RequestParam(name = "clipFromStop") boolean clipFromStop
    ) {
        // clip interval
        GetBlockQuery query = new GetBlockQuery(start, stop).clip(MAX_BLOCKS_IN_TRANSIT_PER_PEER, clipFromStop);

        logger.info("get blocks received start height = " + start + " stop height = " + stop);

        List<Block> blocksToSend = bc.getBlocks(query.start, query.stop, MAX_BLOCKS_IN_TRANSIT_PER_PEER, clipFromStop);
        if (blocksToSend != null && blocksToSend.size() > 0) {
            return new String(codec.encodeBlocks(blocksToSend));
        }
        return ERROR("blocks not founded between " + start + " " + stop);
    }

    @GetMapping(value = "/consensus/status", produces = "application/json")
    public Object getStatus() {
        ConsensuEntity.Status status = new ConsensuEntity.Status();
        Block best = bc.currentHeader();
        status.version = best.nVersion;
        status.currentHeight = best.nHeight;
        status.bestBlockHash = best.getHash();
        status.genesisHash = genesis.getHash();
        return codec.encode(status);
    }

    @PostMapping(value = "/consensus/status", produces = "application/json")
    public Object handleStatus(@RequestBody byte[] body, HttpServletRequest request) {
        Block header = bc.currentHeader();
        Status status = codec.decode(body, Status.class);
        if (status == null){
            logger.error("invalid request accepted from " + request.getRemoteAddr());
            return ERROR("invalid request incountered");
        }
        logger.info("receive message type = status, height = " + status.currentHeight + " hash = " + Hex.encodeHexString(status.bestBlockHash));
        if (!Arrays.areEqual(status.genesisHash, genesis.getHash())){
            return ERROR("genesis hash not equal");
        }
        if (header.nHeight > status.currentHeight) {
            return SUCCESS("received");
        }
        if (header.nHeight == status.currentHeight && bc.hasBlock(status.bestBlockHash)) {
            return SUCCESS("received");
        }

        // clip interval
        GetBlockQuery query = new GetBlockQuery(
                header.nHeight, status.currentHeight
        ).clip(
                MAX_BLOCKS_IN_TRANSIT_PER_PEER, false
        );

        consensusClient.getBlocks(query.start, query.stop, false);
        return SUCCESS("received, start synchronizing");
    }

    @PostMapping(value = "/consensus/blocks", produces = "application/json")
    public Object handleProposal(@RequestBody byte[] body, HttpServletRequest request) {
        Block b = codec.decodeBlock(body);
        if(b == null){
            logger.error("invalid request accepted from " + request.getRemoteAddr());
            return ERROR("invalid block proposal");
        }
        consensusClient.receiveBlocks(Collections.singletonList(b));
        return SUCCESS("proposal received");
    }

    @PostMapping(value = "/consensus/transactions", produces = "application/json")
    public Object handleTransactions(@RequestBody byte[] body, HttpServletRequest request) {
        Transaction[] received = codec.decode(body, Transaction[].class);
        if(received == null){
            logger.error("invalid request accepted from " + request.getRemoteAddr());
            return ERROR("invalid transactions found");
        }
        for(Transaction tran:received){
            pool.add(tran);
        }
        return SUCCESS("transaction received successful");
    }
}

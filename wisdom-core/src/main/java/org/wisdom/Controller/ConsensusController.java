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

package org.wisdom.Controller;

import org.apache.commons.codec.binary.Hex;
import org.bouncycastle.util.Arrays;
import org.wisdom.ApiResult.APIResult;
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
import org.wisdom.service.Impl.CommandServiceImpl;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
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

    @Value("${wisdom.version}")
    String version;

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

    @Autowired
    CommandServiceImpl commandService;

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
        if (status == null) {
            logger.error("invalid request accepted from " + request.getRemoteAddr());
            return ERROR("invalid request incountered");
        }
        logger.info("receive message type = status, height = " + status.currentHeight + " hash = " + Hex.encodeHexString(status.bestBlockHash));
        if (!Arrays.areEqual(status.genesisHash, genesis.getHash())) {
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
        if (b == null) {
            logger.error("invalid request accepted from " + request.getRemoteAddr());
            return ERROR("invalid block proposal");
        }
        consensusClient.receiveBlocks(Collections.singletonList(b));
        return SUCCESS("proposal received");
    }

    @PostMapping(value = "/consensus/transactions", produces = "application/json")
    public Object handleTransactions(@RequestBody byte[] body, HttpServletRequest request) {
        ConsensusClient.TransactionsPacket packet = codec.decode(body, ConsensusClient.TransactionsPacket.class);
        if (packet != null) {
            if (packet.ttl == 0) {
                return ERROR("ttl timeout");
            }
            if (packet.txs == null || packet.txs.size() == 0) {
                return SUCCESS("transaction received successful");
            }
            for (Transaction tran : packet.txs) {
                byte[] traninfo = tran.toRPCBytes();
                commandService.verifyTransfer(traninfo);
            }
            consensusClient.relayTransactions(packet);
            return SUCCESS("transaction received successful");
        }
        return ERROR("empty transactions packet received " + new String(body));
    }

    @GetMapping(value = "/version", produces = "application/json")
    public Object getVersion() {
        return APIResult.newFailResult(2000, "SUCCESS", version);
    }
}

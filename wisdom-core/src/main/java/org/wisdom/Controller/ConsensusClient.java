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

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.wisdom.p2p.entity.GetBlockQuery;
import org.wisdom.p2p.entity.Status;
import org.wisdom.consensus.pow.ConsensusConfig;
import org.wisdom.encoding.JSONEncodeDecoder;
import org.wisdom.core.*;
import org.wisdom.core.account.Transaction;
import org.wisdom.core.validate.BasicRule;
import org.wisdom.core.validate.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class ConsensusClient {
    private static final int MAX_BLOCKS_IN_TRANSIT_PER_PEER = 50;

    private volatile int counter;

    @Value("${wisdom.consensus.enable-mining}")
    boolean enableMining;

    @Autowired
    private ConsensusConfig consensusConfig;

    @Autowired
    private JSONEncodeDecoder codec;

    @Autowired
    private BasicRule rule;

    @Autowired
    private OrphanBlocksManager orphanBlocksManager;

    @Autowired
    private PendingBlocksManager pendingBlocksManager;

    @Autowired
    private Block genesis;

    @Autowired
    private WisdomBlockChain bc;

    private static final Logger logger = LoggerFactory.getLogger(ConsensusClient.class);

    private static byte[] getBody(final HttpResponse response) {
        int status = response.getStatusLine().getStatusCode();
        if (status < 200 || status >= 300) {
            return null;
        }
        try {
            HttpEntity entity = response.getEntity();
            return entity != null ? EntityUtils.toByteArray(entity) : null;
        } catch (Exception e) {
            return null;
        }
    }

    private static Object noop(Object o) {
        return null;
    }

    private void post(String url, byte[] body, Callback<byte[], Object> cb) {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        try {
            URI urio = new URI(url);

            HttpPost httppost = new HttpPost(urio);
            httppost.setEntity(new ByteArrayEntity(body, ContentType.APPLICATION_JSON));
            // Create a custom response handler

            byte[] responseBody = httpclient.execute(httppost, ConsensusClient::getBody);
            if (responseBody != null) {
                logger.info("response received = " + new String(responseBody));
            } else {
                logger.error("post " + url + " fail");
            }
            cb.call(responseBody);
        } catch (Exception e) {
            logger.error("post " + url + " fail");
        } finally {
            try {
                httpclient.close();
            } catch (Exception e) {
                logger.error("close client fail");
            }
        }
    }

    private void broadcast(String path, byte[] body) {
        for (String hostPort : consensusConfig.getPeers()) {
            post("http://" + hostPort + path, body, ConsensusClient::noop);
        }
    }

    private void get(String uri, Map<String, String> query, Callback<byte[], Object> cb) {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        try {
            URI urio = new URI(uri);
            URIBuilder builder = new URIBuilder()
                    .setScheme(urio.getScheme())
                    .setHost(urio.getHost())
                    .setPort(urio.getPort())
                    .setPath(urio.getPath());
            for (String k : query.keySet()) {
                builder.setParameter(k, query.get(k));
            }
            urio = builder.build();
            uri = urio.toString();
            HttpGet httpget = new HttpGet(urio);
            // Create a custom response handler

            byte[] responseBody = httpclient.execute(httpget, ConsensusClient::getBody);
            if (responseBody != null) {
                logger.info("response received = " + new String(responseBody));
            } else {
                logger.error("get " + uri + " fail");
            }
            cb.call(responseBody);
        } catch (Exception e) {
            logger.error("get " + uri + " fail");
        } finally {
            try {
                httpclient.close();
            } catch (Exception e) {
                logger.info("close client fail");
            }
        }
    }

    @Async
    public void getBlocks(long start, long stop, boolean clipFromStop) {
        Map<String, String> params = new HashMap<>();
        params.put("start", Long.toString(start));
        params.put("stop", Long.toString(stop));
        params.put("clipFromStop", Boolean.toString(clipFromStop));
        for (String hostPort : consensusConfig.getPeers()) {
            get("http://" + hostPort + "/consensus/blocks", params, (byte[] body) -> {
                List<Block> blocks = codec.decodeBlocks(body);
                if(blocks == null){
                    logger.error("get blocks from " + hostPort + " failed, consider correct your boot nodes");
                    return null;
                }
                receiveBlocks(blocks);
                return null;
            });
        }
    }

    @Async
    public Object receiveBlocks(List<Block> blocks) {
        List<Block> validBlocks = new ArrayList<>();
        for (Block b : blocks) {
            if (b == null || b.nHeight == 0) {
                continue;
            }
            Result res = rule.validateBlock(b);
            if (!res.isSuccess()) {
                logger.error("invalid block received reason = " + res.getMessage());
                continue;
            }
            logger.info("receive block = " + new String(codec.encode(b)));
            validBlocks.add(b);
        }
        if (validBlocks.size() > 0) {
            logger.info("receive blocks start from " + validBlocks.get(0).nHeight + " stop at " + validBlocks.get(validBlocks.size() - 1).nHeight);
            BlocksCache blocksWritable = orphanBlocksManager.removeAndCacheOrphans(validBlocks);
            pendingBlocksManager.addPendingBlocks(blocksWritable);
        }
        return null;
    }

    @Scheduled(fixedRate = 15 * 1000)
    public void sendStatus() {
        if (!enableMining) {
            return;
        }
        ConsensuEntity.Status status = new ConsensuEntity.Status();
        Block best = bc.currentHeader();
        status.version = best.nVersion;
        status.currentHeight = best.nHeight;
        status.bestBlockHash = best.getHash();
        status.genesisHash = genesis.getHash();
        broadcast("/consensus/status", codec.encode(status));
    }

    @Scheduled(fixedRate = 30 * 1000)
    public void getStatus() {
        if (enableMining) {
            return;
        }
        counter++;
        counter = counter % consensusConfig.getPeers().size();
        String hostPort = consensusConfig.getPeers().get(counter);
        get("http://" + hostPort + "/consensus/status", new HashMap<>(), (byte[] resp) -> {
            Status status = codec.decode(resp, Status.class);
            if(status == null){
                logger.error("invalid status received " + new String(resp));
                return null;
            }
            Block header = bc.currentHeader();
            if (status.currentHeight <= header.nHeight) {
                return null;
            }
            // clip interval
            GetBlockQuery query = new GetBlockQuery(header.nHeight, status.currentHeight)
                    .clip(ConsensusController.MAX_BLOCKS_IN_TRANSIT_PER_PEER, false);
            getBlocks(query.start, query.stop, false);
            return null;
        });
    }

    @Async
    public void proposalBlock(Block block) {
        broadcast("/consensus/blocks", codec.encodeBlock(block));
    }

    @Scheduled(fixedRate = 30 * 1000)
    public void syncOrphan() {
        for (Block b : orphanBlocksManager.getInitials()) {
            logger.info("try to sync orphans");
            long startHeight = b.nHeight - MAX_BLOCKS_IN_TRANSIT_PER_PEER + 1;
            if (startHeight <= 0) {
                startHeight = 1;
            }
            getBlocks(startHeight, b.nHeight, true);
        }
    }

    // broadcast transactions
    @Async
    public void broascastTransactions(List<Transaction> txs) {
        broadcast("/consensus/transactions", codec.encodeTransactions(txs));
    }
}

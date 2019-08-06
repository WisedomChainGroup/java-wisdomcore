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

import com.sun.org.apache.regexp.internal.RE;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.wisdom.consensus.pow.ConsensusConfig;
import org.wisdom.encoding.JSONEncodeDecoder;
import org.wisdom.core.account.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.*;

@Component
public class RPCClient {
    @Autowired
    private ConsensusConfig consensusConfig;

    @Autowired
    private JSONEncodeDecoder codec;

    private static final int HTTP_TIMEOUT = 5000;

    private CloseableHttpClient httpclient;

    private static final Logger logger = LoggerFactory.getLogger(RPCClient.class);

    public RPCClient() {
        httpclient = HttpClients.custom()
                .setConnectionManager(new PoolingHttpClientConnectionManager())
                .setConnectionManagerShared(true)
                .build();
    }

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

    public void post(String url, byte[] body, Callback<byte[], Object> cb) {
        Optional<CloseableHttpResponse> resp = Optional.empty();
        try {
            URI urio = new URI(url);
            HttpPost httppost = new HttpPost(urio);
            httppost.setConfig(RequestConfig.custom().setConnectTimeout(HTTP_TIMEOUT).build());
            httppost.setEntity(new ByteArrayEntity(body, ContentType.APPLICATION_JSON));
            // Create a custom response handler
            resp = Optional.of(httpclient.execute(httppost));
            Optional<byte[]> responseBody = resp.map(RPCClient::getBody);
            if (responseBody.isPresent()) {
                logger.info("response received = " + responseBody.map(String::new));
            } else {
                logger.error("post " + url + " fail");
            }
            responseBody.map(cb::call);
        } catch (Exception e) {
            logger.error("post " + url + " fail");
        } finally {
            resp.map(x -> {
                try {
                    x.close();
                } catch (Exception e) {
                    return false;
                }
                return true;
            });
        }
    }

    public void broadcast(String path, byte[] body) {
        for (String hostPort : consensusConfig.getPeers()) {
            post("http://" + hostPort + path, body, RPCClient::noop);
        }
    }

    public void get(String uri, Map<String, String> query, Callback<byte[], Object> cb) {
        Optional<CloseableHttpResponse> resp = Optional.empty();
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
            httpget.setConfig(RequestConfig.custom().setConnectTimeout(HTTP_TIMEOUT).build());
            // Create a custom response handler

            resp = Optional.of(httpclient.execute(httpget));
            Optional<byte[]> responseBody = resp.map(RPCClient::getBody);
            if (responseBody.isPresent()) {
                logger.info("response received = " + responseBody.map(String::new));
            } else {
                logger.error("get " + uri + " fail");
            }
            responseBody.map(cb::call);
        } catch (Exception e) {
            logger.error("get " + uri + " fail");
        } finally {
            resp.map(x -> {
                try {
                    x.close();
                } catch (Exception e) {
                    return false;
                }
                return true;
            });
        }
    }


    @Async
    public void relayPacket(Packet packet) {
        packet.dec();
        broadcast("/consensus/transactions", codec.encodePacket(packet));
    }

    @Async
    public void broadcastTransactions(List<Transaction> txs) {
        broadcast("/consensus/transactions", codec.encodePacket(new Packet(codec.encodeTransactions(txs), 8)));
    }
}

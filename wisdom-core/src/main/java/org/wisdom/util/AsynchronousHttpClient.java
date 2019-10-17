package org.wisdom.util;

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

import java.net.URI;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class AsynchronousHttpClient {
    private static final int HTTP_TIMEOUT = 5000;
    private static final Executor executor = command -> new Thread(command).start();

    public static CompletableFuture<byte[]> get(final String url, Map<String, String> query) {
        CloseableHttpClient httpclient = HttpClients.custom()
                .setConnectionManager(new PoolingHttpClientConnectionManager())
                .setConnectionManagerShared(true)
                .build();

        return CompletableFuture.supplyAsync(() -> {
            CloseableHttpResponse resp = null;
            try {
                URI uriObject = new URI(url);
                URIBuilder builder = new URIBuilder()
                        .setScheme(uriObject.getScheme())
                        .setHost(uriObject.getHost())
                        .setPort(uriObject.getPort())
                        .setPath(uriObject.getPath());
                for (String k : query.keySet()) {
                    builder.setParameter(k, query.get(k));
                }
                uriObject = builder.build();
                HttpGet httpget = new HttpGet(uriObject);
                httpget.setConfig(RequestConfig.custom().setConnectTimeout(HTTP_TIMEOUT).build());
                // Create a custom response handler
                resp = httpclient.execute(httpget);
                return getBody(resp);
            } catch (Exception e) {
                try {
                    resp.close();
                } catch (Exception ignored) {

                }
                throw new RuntimeException("get " + url + " fail");
            }
        }, executor);
    }

    public static CompletableFuture<byte[]> post(String url, byte[] body) {
        CloseableHttpClient httpclient = HttpClients.custom()
                .setConnectionManager(new PoolingHttpClientConnectionManager())
                .setConnectionManagerShared(true)
                .build();
        return CompletableFuture.supplyAsync(() -> {
            CloseableHttpResponse resp = null;
            try {
                URI uriObject = new URI(url);
                HttpPost httppost = new HttpPost(uriObject);
                httppost.setConfig(RequestConfig.custom().setConnectTimeout(HTTP_TIMEOUT).build());
                httppost.setEntity(new ByteArrayEntity(body, ContentType.APPLICATION_JSON));
                // Create a custom response handler
                resp = httpclient.execute(httppost);
                return getBody(resp);
            } catch (Exception e) {
                try {
                    resp.close();
                } catch (Exception ignored) {
                }
                throw new RuntimeException("post " + url + " fail");
            }
        }, executor);
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
}

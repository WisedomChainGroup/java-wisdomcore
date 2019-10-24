package org.wisdom.util;

import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.wisdom.util.monad.Monad;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class AsynchronousHttpClient {
    private static final int HTTP_TIMEOUT = 5000;
    private static final Executor executor = command -> new Thread(command).start();

    private static Monad<CloseableHttpClient, Exception> newClient(){
        CloseableHttpClient httpclient = HttpClients.custom()
                .setConnectionManager(new PoolingHttpClientConnectionManager())
                .setConnectionManagerShared(true)
                .build();
        return Monad.of(httpclient).onClean(CloseableHttpClient::close);
    }

    private static Monad<URI, Exception> buildURI(final String url, Map<String, String> query){
        return Monad.of(url).map(URI::new).map(u -> new URIBuilder()
                .setScheme(u.getScheme())
                .setHost(u.getHost())
                .setPort(u.getPort())
                .setPath(u.getPath()))
               .ifPresent(b -> {
                   if (query == null) return;
                   for (String k : query.keySet()) {
                       b.setParameter(k, query.get(k));
                   }
               }).map(URIBuilder::build);
    }

    private static void setTimeout(HttpRequestBase b){
        b.setConfig(RequestConfig.custom().setConnectTimeout(HTTP_TIMEOUT).build());
    }

    public static CompletableFuture<byte[]> get(final String url, Map<String, String> query) {
        return CompletableFuture.supplyAsync(() -> buildURI(url, query).map(HttpGet::new)
                .ifPresent(AsynchronousHttpClient::setTimeout)
                .compose(newClient(), (g, c) -> c.execute(g)).onClean(CloseableHttpResponse::close)
                .map(AsynchronousHttpClient::getBody).cleanUp().get(e -> new RuntimeException("get " + url + " failed")), executor);
    }

    public static CompletableFuture<byte[]> post(String url, byte[] body) {
        return CompletableFuture.supplyAsync(() -> buildURI(url, new HashMap<>()).map(HttpPost::new)
                .ifPresent(AsynchronousHttpClient::setTimeout)
                .ifPresent(p -> p.setEntity(new ByteArrayEntity(body, ContentType.APPLICATION_JSON)))
                .compose(newClient(), (g, c) -> c.execute(g)).onClean(CloseableHttpResponse::close)
                .map(AsynchronousHttpClient::getBody).get(e -> new RuntimeException("get " + url + " failed")), executor);
    }

    private static byte[] getBody(final HttpResponse response) throws Exception{
        int status = response.getStatusLine().getStatusCode();
        if (status < 200 || status >= 300) {
            throw new RuntimeException("http response 404");
        }
        return EntityUtils.toByteArray(response.getEntity());
    }
}

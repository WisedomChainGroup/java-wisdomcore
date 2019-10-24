package org.wisdom.util;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
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
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.wisdom.util.monad.Monad;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class AsynchronousHttpClient {
    private static final int HTTP_TIMEOUT = 5000;
    private static final Executor executor = command -> new Thread(command).start();

    private static CloseableHttpClient newClient() {
        return  HttpClients.custom()
                .setConnectionManager(new PoolingHttpClientConnectionManager())
                .setConnectionManagerShared(true)
                .build();
    }

    public static Monad<URI, Exception> buildURI(final String url, String... queries) {
        Map<String, String> q = new HashMap<>();
        if (queries != null) {
            for (int i = 0; i + 1 < queries.length; i += 2) {
                q.put(queries[i], queries[i + 1]);
            }
        }
        return buildURI(url, q);
    }

    public static Monad<URI, Exception> buildURI(final String url, Map<String, String> query) {
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

    private static void setTimeout(HttpRequestBase b) {
        b.setConfig(RequestConfig.custom().setConnectTimeout(HTTP_TIMEOUT).build());
    }

    public static CompletableFuture<byte[]> get(final String url, String ...queries) {
        CloseableHttpClient client = newClient();
        return CompletableFuture.supplyAsync(() -> buildURI(url, queries).map(HttpGet::new)
                .ifPresent(AsynchronousHttpClient::setTimeout)
                .map(client::execute)
                .onClean(CloseableHttpResponse::close)
                .onClean((n) -> client.close())
                .map(AsynchronousHttpClient::getBody).cleanUp().get(e -> new RuntimeException("get " + url + " failed")), executor);
    }

    public static CompletableFuture<byte[]> post(String url, String... parameters) {
        CloseableHttpClient client = newClient();
        List<NameValuePair> params = new ArrayList<>();
        for(int i = 0; i + 1< parameters.length; i+= 2){
            params.add(new BasicNameValuePair(parameters[i], parameters[i+1]));
        }
        return CompletableFuture.supplyAsync(() -> buildURI(url).map(HttpPost::new)
                .ifPresent(AsynchronousHttpClient::setTimeout)
                .ifPresent(p -> p.setEntity(new UrlEncodedFormEntity(params, "UTF-8")))
                .map(client::execute).onClean((resp) -> {resp.close(); client.close();})
                .map(AsynchronousHttpClient::getBody).get(e -> new RuntimeException("post " + url + " failed")), executor);
    }

    private static byte[] getBody(final HttpResponse response) throws Exception {
        int status = response.getStatusLine().getStatusCode();
        if (status < 200 || status >= 300) {
            throw new RuntimeException("http response 404");
        }
        return EntityUtils.toByteArray(response.getEntity());
    }
}

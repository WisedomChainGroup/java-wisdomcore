package org.wisdom.p2p;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.wisdom.Controller.Callback;

import java.net.URI;
import java.util.Map;

@Component
public class RawClient {
    private static final Logger logger = LoggerFactory.getLogger(RawClient.class);

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
        CloseableHttpClient httpclient = HttpClients.createDefault();
        try {
            URI urio = new URI(url);

            HttpPost httppost = new HttpPost(urio);
            httppost.setEntity(new ByteArrayEntity(body, ContentType.APPLICATION_JSON));
            // Create a custom response handler

            byte[] responseBody = httpclient.execute(httppost, RawClient::getBody);
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


    public void get(String uri, Map<String, String> query, Callback<byte[], Object> cb) {
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

            byte[] responseBody = httpclient.execute(httpget, RawClient::getBody);
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

}

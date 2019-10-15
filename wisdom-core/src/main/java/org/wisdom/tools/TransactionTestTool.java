package org.wisdom.tools;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.apache.commons.cli.*;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.io.IOUtils;
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
import org.springframework.core.io.FileSystemResource;
import org.wisdom.ApiResult.APIResult;
import org.wisdom.consensus.pow.EconomicModel;
import org.wisdom.core.account.Transaction;
import org.wisdom.crypto.ed25519.Ed25519PrivateKey;
import org.wisdom.encoding.JSONEncodeDecoder;
import org.wisdom.util.Address;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * 事务发送工具
 * 使用方法 .\gradlew runTransactionTestTool -PappArgs="-c F:\java-wisdomcore\transaction-test-example.jsonc"
 * 命令行参数会覆盖配置文件里的参数配置
 * -c 配置文件路径，尽量用绝对路径
 * -k 十六进制编码的私钥
 * -h --host 主机名或者 ip
 * -p --port 端口
 * -n --nonce 指定起始 nonce
 */
public class TransactionTestTool {
    private static final int HTTP_TIMEOUT = 5000;
    private static final JSONEncodeDecoder codec = new JSONEncodeDecoder();

    private static class TestConfig {
        public String host;
        public int port;
        public byte[] privateKey;
        public long nonce;
        public List<TransactionInfo> transactions;
    }

    private static class PublicKeyHash {
        public byte[] publicKeyHash;

        public PublicKeyHash(byte[] publicKeyHash) {
            this.publicKeyHash = publicKeyHash;
        }
    }

    private static class PublicKeyHashDeserializer extends StdDeserializer<PublicKeyHash> {
        public static class PublicKeyHashDeserializeException extends JsonProcessingException {
            PublicKeyHashDeserializeException(String msg) {
                super(msg);
            }
        }

        public PublicKeyHashDeserializer() {
            super(PublicKeyHash.class);
        }

        @Override
        public PublicKeyHash deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
            JsonNode node = p.getCodec().readTree(p);
            String encoded = node.asText();
            byte[] publicKeyHash = null;
            try {
                publicKeyHash = Hex.decodeHex(encoded);
                if (publicKeyHash.length == Transaction.PUBLIC_KEY_SIZE) {
                    publicKeyHash = Address.publicKeyToHash(publicKeyHash);
                }
            } catch (Exception e) {
                publicKeyHash = Address.addressToPublicKeyHash(encoded);
            }
            if (publicKeyHash == null) {
                throw new PublicKeyHashDeserializeException("invalid to" + encoded);
            }
            return new PublicKeyHash(publicKeyHash);
        }
    }

    private static class TransactionType {
        public TransactionType(int type) {
            this.type = type;
        }

        public int type;
    }

    private static class TransactionTypeDeserializer extends StdDeserializer<TransactionType> {
        public static class TransactionTypeDeserializeException extends JsonProcessingException {
            TransactionTypeDeserializeException(String msg) {
                super(msg);
            }
        }

        public TransactionTypeDeserializer() {
            super(TransactionType.class);
        }

        @Override
        public TransactionType deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
            JsonNode node = p.getCodec().readTree(p);
            String encoded = node.asText();

            // 默认是转账
            if (encoded == null || encoded.equals("")){
                return new TransactionType(Transaction.Type.TRANSFER.ordinal());
            }

            for (Transaction.Type t : Transaction.TYPES_TABLE) {
                if (t.toString().equals(encoded.toUpperCase())) {
                    return new TransactionType(t.ordinal());
                }
            }
            try {
                int type = Integer.parseInt(encoded);
                return new TransactionType(type);
            } catch (Exception e) {
                throw new TransactionTypeDeserializeException("invalid type: " + encoded);
            }
        }
    }

    private static class TransactionInfo {
        public BigDecimal amount;
        public TransactionType type;
        public PublicKeyHash to;
        public byte[] payload;
        public int times;
    }

    public static void main(String[] args) throws Exception {
        CommandLineParser parser = new DefaultParser();
        final Options options = new Options();

        options.addOption("h", "host", true, "host name");
        options.addOption("p", "port", true, "port");
        options.addOption("n", "nonce", true, "start nonce");
        options.addOption("k", "key", true, "private key");
        options.addOption("c", "config", true, "config file");

        CommandLine line = parser.parse(options, args);

        ObjectMapper mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();

        // 解析十六进制编码 byte array
        module.addDeserializer(byte[].class, new JSONEncodeDecoder.BytesDeserializer());

        // 处理 to
        module.addDeserializer(PublicKeyHash.class, new PublicKeyHashDeserializer());

        // 处理枚举 type
        module.addDeserializer(TransactionType.class, new TransactionTypeDeserializer());

        mapper.registerModule(module);
        mapper.enable(JsonParser.Feature.ALLOW_COMMENTS);
        mapper.enable(JsonParser.Feature.ALLOW_TRAILING_COMMA);

        TestConfig testConfig = mapper.readValue(new FileSystemResource(line.getOptionValue("config")).getInputStream(), TestConfig.class);

        // 使用命令行参数进行覆盖
        if (line.getOptionValue("host") != null && !line.getOptionValue("host").equals("")) {
            testConfig.host = line.getOptionValue("host");
        }

        if (line.getOptionValue("port") != null && !line.getOptionValue("port").equals("")) {
            testConfig.port = Integer.parseInt(line.getOptionValue("port"));
        }

        if (line.getOptionValue("key") != null && !line.getOptionValue("key").equals("")) {
            testConfig.privateKey = Hex.decodeHex(line.getOptionValue("key"));
        }

        if (line.getOptionValue("nonce") != null && !line.getOptionValue("nonce").equals("")) {
            testConfig.nonce = Integer.parseInt(line.getOptionValue("nonce"));
        }
        Ed25519PrivateKey privateKey = new Ed25519PrivateKey(testConfig.privateKey);
        String publicKeyHashHex = Hex.encodeHexString(Address.publicKeyToHash(privateKey.generatePublicKey().getEncoded()));

        // 如果 nonce 等于0 获取全局 nonce
        if (testConfig.nonce == 0) {
            testConfig.nonce = getNonce(publicKeyHashHex, testConfig.host, testConfig.port).get() + 1;
        }

        List<CompletableFuture> futures = new ArrayList<>();
        // 发送事务
        for (TransactionInfo info : testConfig.transactions) {
            Transaction tx = new Transaction();
            tx.version = Transaction.DEFAULT_TRANSACTION_VERSION;
            tx.type = info.type.type;

            BigDecimal amount = info.amount.multiply(new BigDecimal(EconomicModel.WDC));

            if (amount.compareTo(new BigDecimal(Long.MAX_VALUE)) > 0 || amount.compareTo(BigDecimal.ZERO) < 0){
                throw new ArithmeticException("amount is negative or amount overflow maximum signed 64 bit integer");
            }

            tx.amount = amount.longValue();

            tx.to = info.to.publicKeyHash;
            tx.from = privateKey.generatePublicKey().getEncoded();
            tx.gasPrice = (long) Math.ceil(
                    0.02 * EconomicModel.WDC / Transaction.GAS_TABLE[tx.type]
            );
            tx.payload = info.payload;
            for (int i = 0; i < info.times; i++) {
                // clear cache
                tx.setHashCache(null);
                tx.nonce = testConfig.nonce;
                tx.signature = privateKey.sign(tx.getRawForSign());
                futures.add(postTransaction(tx, testConfig.host, testConfig.port).thenAcceptAsync(r -> {
                    if (r.code == APIResult.FAIL) {
                        System.out.println("post transaction failed" + r.message);
                    } else {
                        System.out.println(new String(codec.encode(tx)));
                    }
                }));
                testConfig.nonce++;
            }
        }
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[]{})).join();
    }


    private static class Response {
        public int code;
        public String message;
        public Object data;
    }

    private static class GetNonceResponse {
        public int code;
        public String message;
        public long data;
    }

    private static class GetPoolAddressResponse {
        public int code;
        public String message;
        public List<Map<String, Object>> data;
    }


    private static CompletableFuture<byte[]> post(String url, byte[] body) {
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
        });
    }


    private static CompletableFuture<Response> postTransaction(Transaction tx, String host, int port) {
        try {
            URI uri = new URI(
                    "http",
                    null,
                    host,
                    port,
                    "/sendTransaction",
                    "traninfo=" + Hex.encodeHexString(tx.toRPCBytes()), null
            );
            return post(uri.toString(), new byte[]{}).thenApplyAsync(x -> codec.decode(x, Response.class));
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private static CompletableFuture<Long> getNonce(String publicKeyHash, String host, int port) throws Exception {
        URI uri = new URI(
                "http",
                null,
                host,
                port,
                "/sendNonce",
                "pubkeyhash=" + publicKeyHash, ""
        );
        return post(uri.toString(), new byte[]{}).thenApplyAsync((body) -> {
            GetNonceResponse getNonceResponse = codec.decode(body, GetNonceResponse.class);
            return getNonceResponse.data;
        });
    }

    private static CompletableFuture<byte[]> get(final String url, Map<String, String> query) {
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
        });
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

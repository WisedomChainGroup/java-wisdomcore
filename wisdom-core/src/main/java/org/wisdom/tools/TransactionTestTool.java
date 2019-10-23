package org.wisdom.tools;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.google.protobuf.ByteString;
import org.apache.commons.cli.*;
import org.apache.commons.codec.binary.Hex;
import org.springframework.core.io.FileSystemResource;
import org.wisdom.ApiResult.APIResult;
import org.wisdom.consensus.pow.EconomicModel;
import org.wisdom.core.account.Transaction;
import org.wisdom.crypto.ed25519.Ed25519PrivateKey;
import org.wisdom.encoding.JSONEncodeDecoder;
import org.wisdom.p2p.*;
import org.wisdom.protobuf.tcp.command.HatchModel;
import org.wisdom.sync.Utils;
import org.wisdom.util.Address;
import org.wisdom.util.AsynchronousHttpClient;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

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
    private static final JSONEncodeDecoder codec = new JSONEncodeDecoder();
    public static final Executor executor = command -> new Thread(command).start();


    private static class TestConfig {
        public String host;
        public int port;
        @JsonProperty("grpc.port")
        public int grpcPort;

        public byte[] privateKey;
        public long nonce;
        public List<TransactionInfo> transactions;
        public String protocol;
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
            if (encoded == null || encoded.equals("")) {
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

    private static class Payload {
        public byte[] payload;

        public Payload(byte[] payload) {
            this.payload = payload;
        }
    }

    private static class PayloadDeserializer extends StdDeserializer<Payload> {
        public static class PayloadDeserializeException extends JsonProcessingException {
            PayloadDeserializeException(String msg) {
                super(msg);
            }
        }

        public PayloadDeserializer() {
            super(TransactionType.class);
        }

        @Override
        public Payload deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
            JsonNode node = p.getCodec().readTree(p);
            String encoded = node.asText();
            if (encoded.startsWith("`") && encoded.endsWith("`")) {
                encoded = encoded.substring(1);
                encoded = encoded.substring(0, encoded.length() - 1);
                return new Payload(encoded.getBytes(StandardCharsets.UTF_8));
            }
            if (encoded.startsWith("0x")) {
                try {
                    return new Payload(Hex.decodeHex(encoded.substring(2)));
                } catch (Exception e) {
                    throw new PayloadDeserializeException(e.getMessage());
                }
            }
            try {
                return new Payload(Hex.decodeHex(encoded));
            } catch (Exception e) {
                return new Payload(encoded.getBytes(StandardCharsets.UTF_8));
            }
        }
    }

    private static class HatchTypeDeserializer extends StdDeserializer<Integer>{
        public static class HatchTypeDeserializerException extends JsonProcessingException {
            HatchTypeDeserializerException(String msg) {
                super(msg);
            }
        }

        public HatchTypeDeserializer() {
            super(Integer.class);
        }

        @Override
        public Integer deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
            JsonNode node = p.getCodec().readTree(p);
            String encoded = node.asText();
            try {
                int i = Integer.parseInt(encoded);
                if (i != 365 && i != 120) {
                    throw new HatchTypeDeserializerException("hatch type must be 365 or 120");
                }
                return i;
            } catch (Exception e) {
                throw new HatchTypeDeserializerException("hatch type must be 365 or 120");
            }
        }
    }


    private static class TransactionInfo {
        public BigDecimal amount;
        public TransactionType type;
        public PublicKeyHash to;
        public Payload payload;
        public int times;
        @JsonDeserialize(using = HatchTypeDeserializer.class)
        public Integer hatchType;
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

        module.addDeserializer(Payload.class, new PayloadDeserializer());

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

        List<Transaction> transactions = new ArrayList<>();

        // 发送事务
        for (TransactionInfo info : testConfig.transactions) {
            Transaction tx = new Transaction();
            tx.version = Transaction.DEFAULT_TRANSACTION_VERSION;
            tx.type = info.type.type;

            BigDecimal amount = info.amount.multiply(new BigDecimal(EconomicModel.WDC));

            if (amount.compareTo(new BigDecimal(Long.MAX_VALUE)) > 0 || amount.compareTo(BigDecimal.ZERO) < 0) {
                throw new ArithmeticException("amount is negative or amount overflow maximum signed 64 bit integer");
            }

            tx.amount = amount.longValue();

            tx.to = info.to.publicKeyHash;
            tx.from = privateKey.generatePublicKey().getEncoded();
            tx.gasPrice = (long) Math.ceil(
                    0.002 * EconomicModel.WDC / Transaction.GAS_TABLE[tx.type]
            );
            tx.payload = info.payload.payload;

            // 对 payload 进行 protobuf 编码
            if (tx.type == Transaction.Type.INCUBATE.ordinal()){

                byte[] zeroBytes = new byte[32];
                HatchModel.Payload.Builder builder = HatchModel.Payload.newBuilder()
                        .setTxId(ByteString.copyFrom(zeroBytes));
                if (tx.payload != null && tx.payload.length > 0){
                    builder.setSharePubkeyHashBytes(ByteString.copyFrom(tx.payload));
                }
                builder.setType(info.hatchType);
                tx.payload = builder.build().toByteArray();
            }
            for (int i = 0; i < info.times; i++) {
                // clear cache
                Transaction newTx = tx.copy();
                newTx.nonce = testConfig.nonce;
                newTx.signature = privateKey.sign(newTx.getRawForSign());
                transactions.add(newTx);
                testConfig.nonce++;
            }
        }

        if (testConfig.protocol != null && testConfig.protocol.equals("grpc")) {
            final Peer self = Peer.newPeer("wisdom://localhost:9585");
            sendTransactionsByGRPC(transactions, self, testConfig);
        } else {
            sendTransactionsByRPC(transactions, testConfig);
        }
    }

    private static void sendTransactionsByRPC(List<Transaction> transactions, TestConfig testConfig) {
        List<CompletableFuture> futures = new ArrayList<>();
        for (Transaction tx : transactions) {
            futures.add(postTransaction(tx.toRPCBytes(), testConfig.host, testConfig.port).thenAcceptAsync(r -> {
                if (r.code == APIResult.FAIL) {
                    System.err.println("post transaction failed: " + r.message);
                } else {
                    System.out.println(new String(codec.encode(tx)));
                }
            }));
        }
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[]{})).join();
    }

    private static void sendTransactionsByGRPC(List<Transaction> transactions, Peer self, TestConfig testConfig) throws Exception {
        WisdomOuterClass.Transactions.Builder builder = WisdomOuterClass.Transactions.newBuilder();
        transactions.stream().map(Utils::encodeTransaction).forEach(builder::addTransactions);
        GRPCClient client = new GRPCClient(self).withExecutor(executor).withTimeout(Integer.MAX_VALUE);
        List<WisdomOuterClass.Transactions> transactionsList = Util.split(builder.build());
        transactionsList.forEach(li -> System.out.println(li.getSerializedSize() * 1.0 / (1 << 20)));
        CompletableFuture.allOf(
                transactionsList
                .stream()
                .map(o -> client.dialWithTTL(testConfig.host, testConfig.grpcPort, 8, o))
                .toArray(CompletableFuture[]::new)
        ).join()
        ;
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


    private static CompletableFuture<Response> postTransaction(byte[] body, String host, int port) {
        try {
            URI uri = new URI(
                    "http",
                    null,
                    host,
                    port,
                    "/sendTransaction",
                    "traninfo=" + Hex.encodeHexString(body), null
            );
            return AsynchronousHttpClient.post(uri.toString(), new byte[]{}).thenApplyAsync(x -> codec.decode(x, Response.class));
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
        return AsynchronousHttpClient.post(uri.toString(), new byte[]{}).thenApplyAsync((body) -> {
            GetNonceResponse getNonceResponse = codec.decode(body, GetNonceResponse.class);
            return getNonceResponse.data;
        });
    }
}

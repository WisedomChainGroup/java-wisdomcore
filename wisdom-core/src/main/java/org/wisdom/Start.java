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

package org.wisdom;


import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;
import org.tdf.common.serialize.Codec;
import org.tdf.common.store.DatabaseStore;
import org.tdf.common.store.NoDeleteBatchStore;
import org.tdf.common.trie.Trie;
import org.tdf.lotusvm.common.Register;
import org.tdf.lotusvm.runtime.Frame;
import org.wisdom.consensus.pow.ProposersState;
import org.wisdom.controller.WebSocket;
import org.wisdom.core.utxo.UTXOSets;
import org.wisdom.crypto.HashUtil;
import org.wisdom.db.Candidate;
import org.wisdom.db.DatabaseStoreFactory;
import org.wisdom.encoding.JSONEncodeDecoder;
import org.wisdom.genesis.Genesis;
import org.wisdom.p2p.Peer;
import org.wisdom.util.FileUtil;

import java.io.IOException;

// touch 
@SpringBootApplication
@EnableAsync
@EnableScheduling
@Slf4j
public class Start {
    private static final String CODE_ASSERTION_ENV = "ENABLE_CODE_ASSERTION";

    // 开启断言 用于调试
    public static final boolean ENABLE_ASSERTION = System.getenv(CODE_ASSERTION_ENV) != null && System.getenv(CODE_ASSERTION_ENV).equals("true");

    public static void main(String[] args) {
        // 关闭 grpc 日志
        io.netty.util.internal.logging.InternalLoggerFactory.setDefaultFactory(new InternalLoggerFactory() {
            @Override
            protected InternalLogger newInstance(String name) {
                return new NopLogger();
            }
        });
        SpringApplication app = new SpringApplication(Start.class);
        app.addInitializers(applicationContext -> {
            Environment env = applicationContext.getEnvironment();
            String constant = env.getProperty("wisdom.consensus.max-proposers");
            if (constant != null && !constant.isEmpty()) {
                ProposersState.MAXIMUM_PROPOSERS = Integer.parseInt(constant);
            }
            constant = env.getProperty("wisdom.consensus.community-miner-joins-height");
            if (constant != null && !constant.isEmpty()) {
                ProposersState.COMMUNITY_MINER_JOINS_HEIGHT = Integer.parseInt(constant);
            }
            constant = env.getProperty("wisdom.consensus.attenuation-eras");
            if (constant != null && !constant.isEmpty()) {
                Candidate.ATTENUATION_ERAS = Integer.parseInt(constant);
            }
        });

        app.run(args);
    }


    @Bean
    public UTXOSets getUTXOSets() {
        UTXOSets utxoSets = new UTXOSets();
        return utxoSets;
    }

//    @Bean
//    public Env env(@Value("${wisdom.env-config}") String envFile,
//                   @Value("${wisdom.env-selector}") String envSelector,
//                   @Value("${wisdom.consensus.genesis}") String optionalGenesis,
//                   @Value("${miner.validators}") String optionalValidators) throws IOException {
//        Resource resource = new FileSystemResource(envFile);
//        if (!resource.exists()) {
//            resource = new ClassPathResource(envFile);
//        }
//        String genesis;
//        String validators;
//        try {
//            Map<String, Map<String, Map<String, String>>> map = new Yaml().loadAs(resource.getInputStream(), Map.class);
//            genesis = map.get("env").get(envSelector).get("wisdom-consensus-genesis");
//            validators = map.get("env").get(envSelector).get("miner-validators");
//        } catch (Exception e) {
//            genesis = optionalGenesis;
//            validators = optionalValidators;
//        }
//        Env env = new Env();
//        env.setGenesis(genesis);
//        env.setValidatorsFile(validators);
//        return env;
//    }


    @Bean
    public Genesis genesis(JSONEncodeDecoder codec, @Value("${wisdom.consensus.genesis}") String genesis)
            throws Exception {
        Resource resource = FileUtil.getResource(genesis);
        return codec.decodeGenesis(IOUtils.toByteArray(resource.getInputStream()));
    }


    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
        SimpleModule module = new SimpleModule();
        module.addSerializer(byte[].class, new JSONEncodeDecoder.BytesSerializer());
        module.addDeserializer(byte[].class, new JSONEncodeDecoder.BytesDeserializer());
        module.addSerializer(Peer.class, new StdSerializer<Peer>(Peer.class) {
            @Override
            public void serialize(Peer value, JsonGenerator gen, SerializerProvider provider) throws IOException {
                gen.writeString(value.toString());
            }
        });
        mapper.registerModule(module);
        return mapper;
    }

    @Bean
    public DatabaseStore contractCodeStore(DatabaseStoreFactory factory) {
        return factory.create("contract-code", false);
    }

    @Bean
    public Trie<byte[], byte[]> storageTrie(DatabaseStoreFactory factory) {
        DatabaseStore store = factory.create("contract-storage", false);
        return Trie.<byte[], byte[]>builder()
                .hashFunction(HashUtil::keccak256)
                .keyCodec(Codec.identity())
                .valueCodec(Codec.identity())
                .store(new NoDeleteBatchStore<>(store))
                .build();
    }

    @Bean
    public ServerEndpointExporter serverEndpointExporter(ApplicationContext context) {
        WebSocket.ctx = context;
        return new ServerEndpointExporter();
    }

}

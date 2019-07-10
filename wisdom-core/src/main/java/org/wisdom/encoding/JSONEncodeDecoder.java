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

package org.wisdom.encoding;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.apache.commons.codec.binary.Hex;
import org.wisdom.genesis.Genesis;
import org.wisdom.core.Block;
import org.wisdom.core.account.Transaction;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class JSONEncodeDecoder implements CoreTypesEncoder, CoreTypesDecoder {
    public static class BytesSerializer extends StdSerializer<byte[]> {

        private static final long serialVersionUID = -5510353102817291511L;

        public BytesSerializer() {
            super(byte[].class);
        }

        @Override
        public void serialize(byte[] value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonGenerationException {
            jgen.writeString(Hex.encodeHexString(value));
        }
    }

    public static class BytesDeserializer extends StdDeserializer<byte[]> {

        private static final long serialVersionUID = 1514703510863497028L;

        public BytesDeserializer() {
            super(byte[].class);
        }

        @Override
        public byte[] deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
            JsonNode node = p.getCodec().readTree(p);
            String encoded = node.asText();
            try {
                return Hex.decodeHex(encoded.toCharArray());
            } catch (Exception e) {
                throw new IOException("invalid hex encoding");
            }
        }
    }

    public byte[] encode(Object object) {
        try {
            // TODO: cancel json uglify option
            ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
            SimpleModule module = new SimpleModule();
            module.addSerializer(byte[].class, new BytesSerializer());
            mapper.registerModule(module);
            return mapper.writeValueAsBytes(object);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public <T> T decode(byte[] encoded, Class<T> valueType) {
        if (encoded == null) {
            return null;
        }
        try {
            ObjectMapper mapper = new ObjectMapper();
            SimpleModule module = new SimpleModule();
            module.addDeserializer(byte[].class, new BytesDeserializer());
            mapper.registerModule(module);
            return mapper.readValue(encoded, valueType);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public Block decodeBlock(byte[] data) {
        return decode(data, Block.class);
    }

    @Override
    public Transaction decodeTransaction(byte[] data) {
        return decode(data, Transaction.class);
    }

    // big-endian decode
    @Override
    public long decodeBlockNumber(byte[] data) {
        return BigEndian.decodeUint32(data);
    }

    @Override
    public List<Transaction> decodeBlockBody(byte[] body) {
        return Arrays.asList(decode(body, Transaction[].class));
    }

    @Override
    public byte[] encodeBlock(Block block) {
        return encode(block);
    }

    @Override
    public byte[] encodeTransaction(Transaction transaction) {
        return encode(transaction);
    }

    // big-endian encoding
    @Override
    public byte[] encodeBlockNumber(long value) {
        return BigEndian.encodeUint32(value);
    }

    @Override
    public byte[] encodeBlockBody(List<Transaction> body) {
        return encode(body);
    }

    @Override
    public List<byte[]> decodeHashes(byte[] hashes) {
        List<String> tmp = decode(hashes, ArrayList.class);
        List<byte[]> res = new ArrayList<>();
        try {
            for (String s : tmp) {
                res.add(Hex.decodeHex(s.toCharArray()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    @Override
    public byte[] encodeHashes(List<byte[]> hashes) {
        List<String> hs = new ArrayList<>();
        for (byte[] h : hashes) {
            hs.add(Hex.encodeHexString(h));
        }
        return encode(hs);
    }

    public Genesis decodeGenesis(byte[] data){
        return decode(data, Genesis.class);
    }

    public byte[] encodeBlocks(List<Block> blocks){
        return encode(blocks);
    }

    public List<Block> decodeBlocks(byte[] data){
        return Arrays.asList(decode(data, Block[].class));
    }

    public List<Transaction> decodeTransactions(byte[] txs) {
        return Arrays.asList(decode(txs, Transaction[].class));
    }

    public byte[] encodeTransactions(List<Transaction> txs) {
        return encode(txs);
    }
}
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

package org.wisdom.consensus.pow;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.io.IOUtils;
import org.wisdom.Env;
import org.wisdom.encoding.JSONEncodeDecoder;
import org.wisdom.keystore.wallet.KeystoreAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class ConsensusConfig {
    private static Logger logger = LoggerFactory.getLogger(ConsensusConfig.class);

    private List<String> validators;

    private List<String> peers;

    private List<String> validatorPubKeyHashes;

    private String minerPubKeyHash;

    @Value("${wisdom.consensus.enable-mining}")
    private volatile boolean enableMining;

    public boolean isEnableMining() {
        return enableMining;
    }

    public List<String> getValidators() {
        return validators;
    }

    public List<String> getValidatorPubKeyHashes() {
        return validatorPubKeyHashes;
    }

    public String getMinerPubKeyHash() {
        return minerPubKeyHash;
    }

    public List<String> getPeers() {
        return peers;
    }

    public ConsensusConfig(JSONEncodeDecoder codec,
                           @Value("${miner.coinbase}") String coinbase,
                           Env env,
                           @Value("${wisdom.consensus.enable-mining}") boolean enableMining
    ) throws Exception {
        Resource  resource = new FileSystemResource(env.validatorsFile);
        if(!resource.exists()){
            resource = new ClassPathResource(env.validatorsFile);
        }
        if (enableMining) {
            minerPubKeyHash = Hex.encodeHexString(KeystoreAction.addressToPubkeyHash(coinbase));
            logger.info("mining is enabled, your coin base address is " + coinbase);
        }
        validators = Arrays.asList(codec.decode(IOUtils.toByteArray(resource.getInputStream()), String[].class));
        validatorPubKeyHashes = new ArrayList<>();
        peers = new ArrayList<>();
        for (String v : validators) {
            URI uri = new URI(v);
            String pubKeyHashes = Hex.encodeHexString(KeystoreAction.addressToPubkeyHash(uri.getRawUserInfo()));
            validatorPubKeyHashes.add(pubKeyHashes);
            logger.info("initial validator found address = " + uri.getRawUserInfo());
            if (!pubKeyHashes.equals(minerPubKeyHash)) {
                peers.add(uri.getHost() + ":" + uri.getPort());
            }
        }
    }


}

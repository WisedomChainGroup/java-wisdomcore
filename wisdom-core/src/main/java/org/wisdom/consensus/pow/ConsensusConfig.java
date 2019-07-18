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
import org.wisdom.core.account.Transaction;
import org.wisdom.encoding.JSONEncodeDecoder;
import org.wisdom.keystore.wallet.Keystore;
import org.wisdom.keystore.wallet.KeystoreAction;
import org.wisdom.core.Block;
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

    @Value("${wisdom.consensus.pow-wait}")
    private int powWait;

    public void setPowWait(int powWait) {
        this.powWait = powWait;
    }

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
                           @Value("${miner.validators}") String validatorsFile,
                           @Value("${wisdom.consensus.enable-mining}") boolean enableMining
    ) throws Exception {
        Resource resource;
        try {
            resource = new ClassPathResource(validatorsFile);
        } catch (Exception e) {
            resource = new FileSystemResource(validatorsFile);
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
            logger.info("validator found address = " + uri.toASCIIString());
            if (!pubKeyHashes.equals(minerPubKeyHash)) {
                peers.add(uri.getHost() + ":" + uri.getPort());
            }
        }
        for (String p : peers) {
            logger.info("peer loaded from " + validatorsFile + " " + p);
        }
    }

    public static void main(String[] args) throws Exception {
        ConsensusConfig cfg = new ConsensusConfig(new JSONEncodeDecoder(), "1pQfDX4fvz7uzBQuM9FbuoKWohmhg9TmY", "genesis/validators.json", true);

        cfg.setPowWait(90);
        Block p = new Block();
        p.nHeight = 9005;
        p.nTime = 1562875891;
        p.body = new ArrayList<>();
        Transaction tx = new Transaction();
        tx.to = Hex.decodeHex("5b0a4c7e31c3123db40a4c14200b54b8e358294b".toCharArray());
        p.body.add(tx);
        System.out.println(cfg.getProposer(p, 1562875906).pubkeyHash);

    }

    public Proposer getProposer(Block parentBlock, long timeStamp) {
        if (timeStamp <= parentBlock.nTime) {
            return null;
        }
        if (parentBlock.nHeight == 0) {
            return new Proposer(getValidatorPubKeyHashes().get(0), 0, Integer.MAX_VALUE);
        }
        if (parentBlock.nHeight >= 9235) {
            return new Proposer(getValidatorPubKeyHashes().get(0), -1, Integer.MAX_VALUE);
        }
        long step = (timeStamp - parentBlock.nTime)
                / powWait + 1;
        String lastValidator = Hex
                .encodeHexString(
                        parentBlock.body.get(0).to
                );
        int lastValidatorIndex = getValidatorPubKeyHashes()
                .indexOf(lastValidator);
        int currentValidatorIndex = (int) (lastValidatorIndex + step) % getValidatorPubKeyHashes().size();
        long endTime = parentBlock.nTime + step * powWait;
        long startTime = endTime - powWait;
        String validator = getValidatorPubKeyHashes().get(currentValidatorIndex);
        return new Proposer(
                validator,
                startTime,
                endTime
        );
    }
}

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

import org.wisdom.crypto.HashUtil;
import org.wisdom.core.Block;
import org.wisdom.core.account.Transaction;
import org.wisdom.core.state.State;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class ValidatorState implements State<ValidatorState> {
    static final Base64.Encoder encoder = Base64.getEncoder();

    // public key hash base64 -> nonce
    private Map<String, Long> nonce;


    private String getKeyFromPublicKeyHash(byte[] hash) {
        return encoder.encodeToString(hash);
    }

    private String getKeyFromPublicKey(byte[] pubKey) {
        return encoder.encodeToString(HashUtil.ripemd160(pubKey));
    }

    private void incNonce(String key) {
        nonce.putIfAbsent(key, 0L);
        nonce.put(key, nonce.get(key) + 1);
    }

    private void updateNonce(Transaction tx) {
        if (tx.type == Transaction.Type.COINBASE.ordinal()) {
            incNonce(getKeyFromPublicKeyHash(tx.to));
            return;
        }
        incNonce(getKeyFromPublicKey(tx.from));
    }


    @Override
    public ValidatorState updateBlock(Block block) {
        if (block == null || block.body == null) {
            return this;
        }
        for (Transaction tx : block.body) {
            updateTransaction(tx);
        }
        return this;
    }

    @Override
    public ValidatorState updateBlocks(List<Block> blocks) {
        if (blocks == null || blocks.size() == 0) {
            return this;
        }
        for (Block b : blocks) {
            updateBlock(b);
        }
        return this;
    }

    @Override
    public ValidatorState updateTransaction(Transaction transaction) {
        updateNonce(transaction);
        return this;
    }


    public long getNonceFromPublicKeyHash(byte[] publicKeyHash) {
        String key = getKeyFromPublicKeyHash(publicKeyHash);
        nonce.putIfAbsent(key, 0L);
        return nonce.get(key);
    }


    @Override
    public ValidatorState copy() {
        return new ValidatorState(
                new HashMap<>(nonce)
        );
    }


    public ValidatorState(Map<String, Long> nonce) {
        this.nonce = nonce;
    }

    @Autowired
    public ValidatorState(Block genesis) {
        nonce = new HashMap<>();
        updateBlock(genesis);
    }


}

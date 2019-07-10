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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class ValidatorState implements State {

    static final Base64.Encoder encoder = Base64.getEncoder();
    private static final Logger logger = LoggerFactory.getLogger(ValidatorState.class);

    // public key hash base64 -> balance
    private Map<String, Long> balance;

    // public key hash base64 -> nonce
    private Map<String, Long> nonce;

    // public key hash base64 -> votes
    private Map<String,Long> votes;

    private String getKeyFromPublicKeyHash(byte[] hash){
        return encoder.encodeToString(hash);
    }

    private String getKeyFromPublicKey(byte[] pubKey){
        return encoder.encodeToString(HashUtil.ripemd160(pubKey));
    }

    private void putBalance(String key, long amount){
        balance.putIfAbsent(key, 0L);
        balance.put(key, balance.get(key) + amount);
    }

    private void expanseBalance(String key, long amount){
    }

    private void incNonce(String key){
        nonce.putIfAbsent(key, 0L);
        nonce.put(key, nonce.get(key) + 1);
    }

    // TODO: 更新票数
    private void updateVotes(Transaction tx){
        if(tx.type != Transaction.Type.VOTE.ordinal()){
            return;
        }

    }

    private void updateNonce(Transaction tx){
        if(tx.type == Transaction.Type.COINBASE.ordinal()){
            incNonce(getKeyFromPublicKeyHash(tx.to));
            return;
        }
        incNonce(getKeyFromPublicKey(tx.from));
    }

    private void updateBalance(Transaction tx){
        if(tx.type == Transaction.Type.COINBASE.ordinal()){
            putBalance(getKeyFromPublicKeyHash(tx.to), tx.amount);
            return;
        }
        if(tx.type == Transaction.Type.TRANSFER.ordinal()){
            expanseBalance(getKeyFromPublicKey(tx.from), tx.amount + tx.getFee());
            putBalance(getKeyFromPublicKeyHash(tx.to), tx.amount);
            return;
        }
    }

    @Override
    public State updateBlock(Block block) {
        if(block == null || block.body == null){
            return this;
        }
        for(Transaction tx: block.body){
            updateTransaction(tx);
        }
        return this;
    }

    @Override
    public State updateBlocks(List<Block> blocks) {
        if(blocks == null || blocks.size() == 0){
            return this;
        }
        for(Block b: blocks){
            updateBlock(b);
        }
        return this;
    }

    @Override
    public State updateTransaction(Transaction transaction) {
        updateNonce(transaction);
        updateBalance(transaction);
        return this;
    }

    public long getNonceFromPublicKey(byte[] publicKey){
        String key = getKeyFromPublicKey(publicKey);
        nonce.putIfAbsent(key, 0L);
        return nonce.get(key);
    }

    public long getNonceFromPublicKeyHash(byte[] publicKeyHash){
        String key = getKeyFromPublicKeyHash(publicKeyHash);
        nonce.putIfAbsent(key, 0L);
        return nonce.get(key);
    }

    public long getBalanceFromPublicKey(byte[] publicKey){
        return balance.get(getKeyFromPublicKey(publicKey));
    }

    public long getBalanceFromPublickeyHash(byte[] publicKeyHash){
        return balance.get(getKeyFromPublicKeyHash(publicKeyHash));
    }

    @Override
    public State copy() {
        return new ValidatorState(
                new HashMap<>(balance),
                new HashMap<>(nonce),
                new HashMap<>(votes)
        );
    }


    public ValidatorState(Map<String, Long> balance, Map<String, Long> nonce, Map<String, Long> votes) {
        this.balance = balance;
        this.nonce = nonce;
        this.votes = votes;
    }

    @Autowired
    public ValidatorState(Block genesis) {
        balance = new HashMap<>();
        nonce = new HashMap<>();
        votes = new HashMap<>();
        updateBlock(genesis);
    }


}
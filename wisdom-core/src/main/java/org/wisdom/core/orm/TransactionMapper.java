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

package org.wisdom.core.orm;


import org.springframework.util.Assert;
import org.wisdom.Start;
import org.wisdom.core.account.Transaction;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;

public class TransactionMapper implements RowMapper<Transaction> {
    @Override
    public Transaction mapRow(ResultSet rs, int rowNum) throws SQLException {
        Transaction tx = new Transaction();
        tx.version = rs.getInt("version");
        tx.type = rs.getInt("type");
        tx.nonce = rs.getLong("nonce");
        tx.from = rs.getBytes("from");
        tx.gasPrice = rs.getLong("gas_price");
        tx.amount = rs.getLong("amount");
        tx.payload = rs.getBytes("payload");
        tx.signature = rs.getBytes("signature");
        tx.to = rs.getBytes("to");
        tx.height = rs.getLong("height");
        tx.blockHash = rs.getBytes("block_hash");
        if(Start.ENABLE_ASSERTION){
            Assert.isTrue(Arrays.equals(tx.getHash(), rs.getBytes("tx_hash")), "transaction in database had been modified");
            Assert.isTrue(tx.blockHash != null && tx.blockHash.length == 32, "block hash not found");
        }
        return tx;
    }
}
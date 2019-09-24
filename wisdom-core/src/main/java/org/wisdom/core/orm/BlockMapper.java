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


import org.wisdom.core.Block;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;

public class BlockMapper implements RowMapper<Block> {
    @Override
    public Block mapRow(ResultSet rs, int rowNum) throws SQLException {
        Block header = new Block();
        header.nVersion = rs.getLong("version");
        header.hashPrevBlock = rs.getBytes("hash_prev_block");
        header.hashMerkleRoot = rs.getBytes("hash_merkle_root");
        header.hashMerkleState = rs.getBytes("hash_merkle_state");
        header.hashMerkleIncubate = rs.getBytes("hash_merkle_incubate");
        header.nHeight = rs.getLong("height");
        header.nTime = rs.getLong("created_at");
        header.nNonce = rs.getBytes("nonce");
        header.nBits = rs.getBytes("nbits");
        header.blockNotice = rs.getBytes("block_notice");
        header.totalWeight = rs.getLong("total_weight");
        // TODO: remove assertion codes
        assert Arrays.equals(header.getHash(), rs.getBytes("block_hash"));
        return header;
    }
}
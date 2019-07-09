package org.wisdom.core.orm;


import org.wisdom.core.Block;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

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
        header.setHashCache(rs.getBytes("block_hash"));
        return header;
    }
}

package org.wisdom.core.orm;

import org.wisdom.core.utxo.OutPoint;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class OutPointMapper implements RowMapper<OutPoint> {
    @Override
    public OutPoint mapRow(ResultSet rs, int rowNum) throws SQLException {
        OutPoint outPoint = new OutPoint();
        outPoint.setAmount(rs.getLong("value"));
        outPoint.setScript(rs.getBytes("script_pubkey"));
        outPoint.setIndex(rs.getInt("vout_index"));
        outPoint.setScriptLength(outPoint.getScript().length);
        outPoint.setTransactionHash(rs.getBytes("tx_hash"));
        return outPoint;
    }
}

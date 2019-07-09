package org.wisdom.core.orm;

import org.wisdom.core.utxo.InPoint;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class InPointMapper implements RowMapper<InPoint> {
    @Override
    public InPoint mapRow(ResultSet rs, int rowNum) throws SQLException {
        InPoint inPoint = new InPoint();
        inPoint.setOutPointIndex(rs.getInt("vout_index"));
        inPoint.setScript(rs.getBytes("script_sig"));
        inPoint.setScriptLength(inPoint.getScript().length);
        inPoint.setPreviousTransactionHash(rs.getBytes("vout_tx_hash"));
        inPoint.setTransactionHash(rs.getBytes("tx_hash"));
        return inPoint;
    }
}

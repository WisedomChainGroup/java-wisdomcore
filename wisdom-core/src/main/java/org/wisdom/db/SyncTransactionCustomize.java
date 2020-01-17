package org.wisdom.db;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class SyncTransactionCustomize {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<Map<String, Object>> selectlistTran(int height, int type, long gas) {
        try {
            String sql = "select encode(t.tx_hash::bytea,'hex') as \"tranHash\",t.from as \"fromAddress\",t.to as \"coinAddress\",t.amount as \"amount\",h.height as \"coinHeigth\",t.gas_price*? as \"fee\" \n" +
                    "from transaction t left join transaction_index i on t.tx_hash=i.tx_hash \n" +
                    "left join header h on h.block_hash=i.block_hash\n" +
                    "where  h.height=? and TYPE=? order by h.height";
            return jdbcTemplate.queryForList(sql, new Object[]{gas, height, type});
        } catch (Exception e) {
            return null;
        }
    }

    public List<Map<String, Object>> selectlistHacth(int height, int type) {
        try {
            String sql = "select encode(t.tx_hash::bytea,'hex') as \"coinHash\",encode(t.to::bytea,'hex') as \"coinAddress\",t.amount as \"coinAccount\",h.height as \"blockHeight\",encode(t.payload::bytea,'hex')as payload \n" +
                    "from transaction t left join transaction_index i on t.tx_hash=i.tx_hash \n" +
                    "left join header h on h.block_hash=i.block_hash\n" +
                    "where  h.height=? and TYPE=? ";
            return jdbcTemplate.queryForList(sql, new Object[]{height, type});
        } catch (Exception e) {
            return null;
        }
    }

    public List<Map<String, Object>> selectlistInterest(int height, int type) {
        try {
            String sql = "select encode(t.tx_hash::bytea,'hex') as \"tranHash\",t.to as \"coinAddress\",t.amount as \"amount\",h.height as \"coinHeigth\",encode(t.payload::bytea,'hex') as \"coinHash\"\n" +
                    "from transaction t left join transaction_index i on t.tx_hash=i.tx_hash \n" +
                    "left join header h on h.block_hash=i.block_hash\n" +
                    "where  h.height=? and TYPE=? ";
            return jdbcTemplate.queryForList(sql, new Object[]{height, type});
        } catch (Exception e) {
            return null;
        }
    }

    public List<Map<String, Object>> selectlistShare(int height, int type) {
        try {
            String sql = "select encode(t.tx_hash::bytea,'hex') as \"coinHash\",t.to as \"coinAddress\",t.amount as \"amount\",h.height as \"coinHeigth\",encode(r.tx_hash::bytea,'hex') as \"tranHash\",\n" +
                    "r.to as \"inviteAddress\"\n" +
                    "from transaction t \n" +
                    "left join transaction_index i on t.tx_hash=i.tx_hash \n" +
                    "left join header h on h.block_hash=i.block_hash\n" +
                    "left join transaction r on t.payload=r.tx_hash\n" +
                    "where  h.height=? and t.type=? ";
            return jdbcTemplate.queryForList(sql, new Object[]{height, type});
        } catch (Exception e) {
            return null;
        }
    }

    public List<Map<String, Object>> selectlistCost(int height, int type) {
        try {
            String sql = "select encode(t.to::bytea,'hex') as \"coinAddress\",t.amount as \"amount\",encode(t.tx_hash::bytea,'hex') as \"tranHash\",h.height as \"coinHeigth\",encode(t.payload::bytea,'hex') as \"tradeHash\" \n" +
                    "from transaction t\n" +
                    "left join transaction_index i on t.tx_hash=i.tx_hash\n" +
                    "left join header h on h.block_hash=i.block_hash\n" +
                    "where  h.height=? and t.type=? ";
            return jdbcTemplate.queryForList(sql, new Object[]{height, type});
        } catch (Exception e) {
            return null;
        }
    }

    public List<Map<String, Object>> selectlistVote(int height, int type) {
        try {
            String sql = "select encode(t.to::bytea,'hex') as \"toAddress\",t.amount as \"amount\",encode(t.tx_hash::bytea,'hex') as \"coinHash\",h.height as \"coinHeigth\",encode(t.from::bytea,'hex') as \"coinAddress\" \n" +
                    "from transaction t\n" +
                    "left join transaction_index i on t.tx_hash=i.tx_hash\n" +
                    "left join header h on h.block_hash=i.block_hash\n" +
                    "where  h.height=? and t.type=? ";
            return jdbcTemplate.queryForList(sql, new Object[]{height, type});
        } catch (Exception e) {
            return null;
        }
    }

    public List<Map<String, Object>> selectlistCancelVote(int height, int type) {
        try {
            String sql = "select encode(t.to::bytea,'hex') as \"toAddress\",t.amount as \"amount\",encode(t.tx_hash::bytea,'hex') as \"coinHash\",h.height as \"coinHeigth\",encode(t.from::bytea,'hex') as \"coinAddress\",encode(t.payload::bytea,'hex') as \"tradeHash\"\n" +
                    "from transaction t\n" +
                    "left join transaction_index i on t.tx_hash=i.tx_hash\n" +
                    "left join header h on h.block_hash=i.block_hash\n" +
                    "where  h.height=? and t.type=? ";
            return jdbcTemplate.queryForList(sql, new Object[]{height, type});
        } catch (Exception e) {
            return null;
        }
    }

    public List<Map<String, Object>> selectlistMortgage(int height, int type) {
        try {
            String sql = "select encode(t.to::bytea,'hex') as \"coinAddress\",t.amount as \"amount\",encode(t.tx_hash::bytea,'hex') as \"coinHash\",h.height as \"coinHeigth\"\n" +
                    "from transaction t\n" +
                    "left join transaction_index i on t.tx_hash=i.tx_hash\n" +
                    "left join header h on h.block_hash=i.block_hash\n" +
                    "where  h.height=? and t.type=? ";
            return jdbcTemplate.queryForList(sql, new Object[]{height, type});
        } catch (Exception e) {
            return null;
        }
    }

    public List<Map<String, Object>> selectlistCancelMortgage(int height, int type) {
        try {
            String sql = "select encode(t.to::bytea,'hex') as \"coinAddress\",t.amount as \"amount\",encode(t.tx_hash::bytea,'hex') as \"coinHash\",h.height as \"coinHeigth\",encode(t.payload::bytea,'hex') as \"tradeHash\"\n" +
                    "from transaction t\n" +
                    "left join transaction_index i on t.tx_hash=i.tx_hash\n" +
                    "left join header h on h.block_hash=i.block_hash\n" +
                    "where  h.height=? and t.type=? ";
            return jdbcTemplate.queryForList(sql, new Object[]{height, type});
        } catch (Exception e) {
            return null;
        }
    }

    public List<Map<String, Object>> selectlistCoinBase(int height) {
        try {
            String sql = "select encode(t.to::bytea,'hex') as \"coinAddress\",t.amount as \"amount\",encode(t.tx_hash::bytea,'hex') as \"coinHash\",h.height as \"coinHeigth\",t.\"type\"\n" +
                    "from transaction t\n" +
                    "left join transaction_index i on t.tx_hash=i.tx_hash\n" +
                    "left join header h on h.block_hash=i.block_hash\n" +
                    "where  h.height=? ";
            return jdbcTemplate.queryForList(sql, new Object[]{height});
        } catch (Exception e) {
            return null;
        }
    }

    public List<Map<String, Object>> selectTranto(byte[] pubkeyhash) {
        try {
            String sql = "select encode(a.tx_hash::bytea,'hex') as \"tx_hash\",a.amount,h.height,encode(a.from::bytea,'hex') as \"from\",encode(a.to::bytea,'hex') as \"to\",encode(h.block_hash::bytea,'hex') as \"block_hash\",h.created_at as datetime from transaction a\n" +
                    "left join transaction_index i on a.tx_hash=i.tx_hash\n" +
                    "left join header h on i.block_hash=h.block_hash\n" +
                    "where a.type=1 and a.to=?";
            return jdbcTemplate.queryForList(sql, new Object[]{pubkeyhash});
        } catch (Exception e) {
            return null;
        }
    }

    public List<Map<String, Object>> selectTranfrom(byte[] pubkeyhash) {
        try {
            String sql = "select encode(a.tx_hash::bytea,'hex') as \"tx_hash\",a.amount,h.height,encode(a.from::bytea,'hex') as \"from\",encode(a.to::bytea,'hex') as \"to\",encode(h.block_hash::bytea,'hex') as \"block_hash\",h.created_at as datetime from transaction a\n" +
                    "left join transaction_index i on a.tx_hash=i.tx_hash\n" +
                    "left join header h on i.block_hash=h.block_hash\n" +
                    "where a.type=1 and a.to!=?";
            return jdbcTemplate.queryForList(sql, new Object[]{pubkeyhash});
        } catch (Exception e) {
            return null;
        }
    }
}

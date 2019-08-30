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

package org.wisdom.core.account;

import org.wisdom.core.orm.TransactionMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class AccountDB {

    @Autowired
    private JdbcTemplate tmpl;

/*    public Account selectaccount(byte[] pubkeyhash){
        try{
            String sql="select * from account b where b.pubkeyhash=? and b.blockheight=(\n" +
                    "select MAx(a.blockheight) from account a where a.pubkeyhash=? )";
            return tmpl.queryForObject(sql,new Object[] { pubkeyhash,pubkeyhash }, new BeanPropertyRowMapper<Account>(Account.class));
        }catch (Exception e){
            return null;
        }
    }*/

    public Account selectaccount(byte[] pubkeyhash) {
        try {
            String sql = "select * from account b where b.pubkeyhash=? order by b.blockheight desc LIMIT 1";
            return tmpl.queryForObject(sql, new Object[]{pubkeyhash}, new BeanPropertyRowMapper<>(Account.class));
        } catch (Exception e) {
            return null;
        }
    }

    public int count() {
        try {
            String sql = "select count(*) from account";
            return tmpl.queryForObject(sql, Integer.class);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public long getBestHeight() {
        try {
            String sql = "select COALESCE(max(blockheight),0) from account";
            return tmpl.queryForObject(sql, Long.class);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public long getNonce(byte[] pubkeyhash) {
        try {
            String sql = "select COALESCE(MAx(a.nonce),0) from account a where a.pubkeyhash=? ";
            return tmpl.queryForObject(sql, new Object[]{pubkeyhash}, Long.class);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public long getBalance(byte[] pubkeyhash) {
        try {
            String sql = "select b.balance from account b where b.pubkeyhash=? and b.blockheight =(select COALESCE(MAx(a.blockheight),0) from account a where a.pubkeyhash=?)";
            return tmpl.queryForObject(sql, new Object[]{pubkeyhash, pubkeyhash}, Long.class);
        } catch (Exception e) {
            return 0;
        }
    }

    public int insertaccount(Account account) {
        try {
            String sql = "insert into account values(?,?,?,?,?,?,?)";
            return tmpl.update(sql, new Object[]{account.getId(), account.getBlockHeight(), account.getPubkeyHash(), account.getNonce(), account.getBalance(), account.getIncubatecost(), account.getMortgage()});
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public long getHeightBalance(byte[] pubkeyhash, int height) {
        try {
            String sql = "select c.balance from account c where c.pubkeyhash=? and c.blockheight=(\n" +
                    "select max(a.blockheight) from account a where a.pubkeyhash=? and a.blockheight<=?)";
            return tmpl.queryForObject(sql, new Object[]{pubkeyhash, pubkeyhash, height}, Long.class);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public int[] insertAccountList(List<Object[]> Object) {
        try {
            String sql = "insert into account(id,blockheight,pubkeyhash,nonce,balance,incubatecost,mortgage,vote) VALUES(?,?,?,?,?,?,?,?) on conflict(id) do nothing";
            return tmpl.batchUpdate(sql, Object);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<Map<String, Object>> selectlistTran(int height, int type, long gas) {
        try {
            String sql = "select encode(t.tx_hash::bytea,'hex') as \"tranHash\",t.from as \"fromAddress\",t.to as \"coinAddress\",t.amount as \"amount\",h.height as \"coinHeigth\",t.gas_price*? as \"fee\" \n" +
                    "from transaction t left join transaction_index i on t.tx_hash=i.tx_hash \n" +
                    "left join header h on h.block_hash=i.block_hash\n" +
                    "where  h.height=? and TYPE=? order by h.height";
            return tmpl.queryForList(sql, new Object[]{gas, height, type});
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<Transaction> selectTran(byte[] frompublickey) {
        try {
            String sql = "select *,0 as height,0 as block_hash from transaction a where a.to=?";
            return tmpl.query(sql, new Object[]{frompublickey}, new TransactionMapper());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<Transaction> selectt() {
        String sql = "select *, 0 as height,0 as block_hash from transaction a where a.amount=50000000000 and a.type=0";
        return tmpl.query(sql, new Object[]{}, new TransactionMapper());
    }

    public List<Map<String, Object>> selectTranto(byte[] pubkeyhash) {
        try {
            String sql = "select encode(a.tx_hash::bytea,'hex') as \"tx_hash\",a.amount,h.height,encode(a.from::bytea,'hex') as \"from\",encode(a.to::bytea,'hex') as \"to\",encode(h.block_hash::bytea,'hex') as \"block_hash\",h.created_at as datetime from transaction a\n" +
                    "left join transaction_index i on a.tx_hash=i.tx_hash\n" +
                    "left join header h on i.block_hash=h.block_hash\n" +
                    "where a.type=1 and a.to=?";
            return tmpl.queryForList(sql, new Object[]{pubkeyhash});
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<Map<String, Object>> selectTranfrom(byte[] pubkeyhash) {
        try {
            String sql = "select encode(a.tx_hash::bytea,'hex') as \"tx_hash\",a.amount,h.height,encode(a.from::bytea,'hex') as \"from\",encode(a.to::bytea,'hex') as \"to\",encode(h.block_hash::bytea,'hex') as \"block_hash\",h.created_at as datetime from transaction a\n" +
                    "left join transaction_index i on a.tx_hash=i.tx_hash\n" +
                    "left join header h on i.block_hash=h.block_hash\n" +
                    "where a.type=1 and a.to!=?";
            return tmpl.queryForList(sql, new Object[]{pubkeyhash});
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<Map<String, Object>> selectlistHacth(int height, int type) {
        try {
            String sql = "select encode(t.tx_hash::bytea,'hex') as \"coinHash\",encode(t.to::bytea,'hex') as \"coinAddress\",t.amount as \"coinAccount\",h.height as \"blockHeight\",encode(t.payload::bytea,'hex')as payload \n" +
                    "from transaction t left join transaction_index i on t.tx_hash=i.tx_hash \n" +
                    "left join header h on h.block_hash=i.block_hash\n" +
                    "where  h.height=? and TYPE=? order by h.height";
            return tmpl.queryForList(sql, new Object[]{height, type});
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<Map<String, Object>> selectlistInterest(int height, int type) {
        try {
            String sql = "select encode(t.tx_hash::bytea,'hex') as \"tranHash\",t.to as \"coinAddress\",t.amount as \"amount\",h.height as \"coinHeigth\",encode(t.payload::bytea,'hex') as \"coinHash\"\n" +
                    "from transaction t left join transaction_index i on t.tx_hash=i.tx_hash \n" +
                    "left join header h on h.block_hash=i.block_hash\n" +
                    "where  h.height=? and TYPE=? order by h.height";
            return tmpl.queryForList(sql, new Object[]{height, type});
        } catch (Exception e) {
            e.printStackTrace();
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
                    "where  h.height=? and t.type=? order by h.height";
            return tmpl.queryForList(sql, new Object[]{height, type});
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<Map<String, Object>> selectlistCost(int height, int type) {
        try {
            String sql = "select encode(t.to::bytea,'hex') as \"coinAddress\",t.amount as \"amount\",encode(t.tx_hash::bytea,'hex') as \"tranHash\",h.height as \"coinHeigth\",encode(t.payload::bytea,'hex') as \"tradeHash\" \n" +
                    "from transaction t\n" +
                    "left join transaction_index i on t.tx_hash=i.tx_hash\n" +
                    "left join header h on h.block_hash=i.block_hash\n" +
                    "where  h.height=? and t.type=? order by h.height";
            return tmpl.queryForList(sql, new Object[]{height, type});
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean hasExitVote(byte[] tranbyte) {
        try {
            String sql = "select count(*) from transaction t where t.payload=? and t.type=13";
            int count = tmpl.queryForObject(sql, new Object[]{tranbyte}, Integer.class);
            return count == 1 ? false : true;
        } catch (Exception e) {
            return false;
        }
    }

    public List<Map<String, Object>> getTranList(int height, int type) {
        if (type == 100) {//全部
            String sql = "select encode(h.block_hash,'hex') as block_hash,h.height,t.version,encode(t.tx_hash,'hex') as tx_hash,t.type,t.nonce,encode(t.from,'hex') as from,t.gas_price,t.amount,encode(t.payload,'hex') as payload,\n" +
                    "encode(t.signature,'hex') as signature,encode(t.to,'hex') as to from transaction t \n" +
                    "left join transaction_index i on t.tx_hash=i.tx_hash  \n" +
                    "left join header h on i.block_hash=h.block_hash\n" +
                    "where h.height=?";
            return tmpl.queryForList(sql, new Object[]{height});
        } else {
            String sql = "select encode(h.block_hash,'hex') as block_hash,h.height,t.version,encode(t.tx_hash,'hex') as tx_hash,t.type,t.nonce,encode(t.from,'hex') as from,t.gas_price,t.amount,encode(t.payload,'hex') as payload,\n" +
                    "encode(t.signature,'hex') as signature,encode(t.to,'hex') as to from transaction t \n" +
                    "left join transaction_index i on t.tx_hash=i.tx_hash  \n" +
                    "left join header h on i.block_hash=h.block_hash\n" +
                    "where h.height=? and t.type=?";
            return tmpl.queryForList(sql, new Object[]{height, type});
        }

    }

    public List<Map<String, Object>> getTranBlockList(byte[] blockhash, int type) {
        if (type == 100) {//全部
            String sql = "select encode(h.block_hash,'hex') as block_hash,h.height,t.version,encode(t.tx_hash,'hex') as tx_hash,t.type,t.nonce,encode(t.from,'hex') as from,\n" +
                    "t.gas_price,t.amount,encode(t.payload,'hex') as payload,\n" +
                    "encode(t.signature,'hex') as signature,encode(t.to,'hex') as to from transaction t \n" +
                    "left join transaction_index i on t.tx_hash=i.tx_hash \n" +
                    "left join header h on i.block_hash=h.block_hash\n" +
                    "where h.block_hash=?";
            return tmpl.queryForList(sql, new Object[]{blockhash});
        } else {
            String sql = "select encode(h.block_hash,'hex') as block_hash,h.height,t.version,encode(t.tx_hash,'hex') as tx_hash,t.type,t.nonce,encode(t.from,'hex') as from,\n" +
                    "t.gas_price,t.amount,encode(t.payload,'hex') as payload,\n" +
                    "encode(t.signature,'hex') as signature,encode(t.to,'hex') as to from transaction t \n" +
                    "left join transaction_index i on t.tx_hash=i.tx_hash \n" +
                    "left join header h on i.block_hash=h.block_hash\n" +
                    "where h.block_hash=? and type=?";
            return tmpl.queryForList(sql, new Object[]{blockhash, type});
        }
    }
}
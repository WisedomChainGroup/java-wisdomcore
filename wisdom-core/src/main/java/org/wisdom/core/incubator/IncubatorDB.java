package org.wisdom.core.incubator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
public class IncubatorDB {

    @Autowired
    private JdbcTemplate tmpl;

    public int count(){
        try{
            String sql="select count(*) from incubator_state";
            return tmpl.queryForObject(sql,Integer.class);
        }catch (Exception e){
            e.printStackTrace();
            return 0;
        }
    }

    public Incubator selectIncubator(byte[] tx){
        try{
            String sql="select * from incubator_state s where s.txid_issue=? and s.height=(\n" +
                    "select max(i.height) from incubator_state i where i.txid_issue=?) ";
            return tmpl.queryForObject(sql,new Object[] { tx,tx },new IncubatorRowMapper());
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }


    public int insertIncubator(Incubator incubator){
        try{
            String sql="insert into incubator_state VALUES(?,?,?,?,?,?,?,?,?,?)";
            return tmpl.update(sql,new Object[]{incubator.getId(),incubator.getShare_pubkeyhash(),incubator.getPubkeyhash(),incubator.getTxid_issue(),incubator.getHeight(),incubator.getCost(),incubator.getInterest_amount(),incubator.getShare_pubkeyhash(),incubator.getLast_blockheight_interest(),incubator.getLast_blockheight_share()});
        }catch (Exception e){
            e.printStackTrace();
            return 0;
        }
    }

    public int[] insertIncubatorList(List<Object[]> Object){
        try{
            String sql="insert into incubator_state(id,share_pubkeyhash,pubkeyhash,txid_issue,height,cost,interest_amount,share_amount,last_blockheight_interest,last_blockheight_share) VALUES(?,?,?,?,?,?,?,?,?,?) on conflict(id) do nothing";
            return tmpl.batchUpdate(sql,Object);
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }
    public class IncubatorRowMapper implements RowMapper<Incubator> {
        @Override
        public Incubator mapRow(ResultSet resultSet, int i) throws SQLException {
            Incubator incubator = new Incubator();
            //需要映射的字段
            incubator.setShare_pubkeyhash(resultSet.getBytes("share_pubkeyhash"));
            incubator.setPubkeyhash(resultSet.getBytes("pubkeyhash"));
            incubator.setTxid_issue(resultSet.getBytes("txid_issue"));
            incubator.setHeight(resultSet.getInt("height"));
            incubator.setCost(resultSet.getLong("cost"));
            incubator.setInterest_amount(resultSet.getLong("interest_amount"));
            incubator.setShare_amount(resultSet.getLong("share_amount"));
            incubator.setLast_blockheight_interest(resultSet.getInt("last_blockheight_interest"));
            incubator.setLast_blockheight_share(resultSet.getInt("last_blockheight_share"));
            return incubator;
        }
    }


}

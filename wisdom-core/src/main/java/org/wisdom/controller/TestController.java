package org.wisdom.controller;

import org.apache.commons.codec.binary.Hex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
public class TestController {

    @Autowired
    JdbcTemplate tmpl;

    @RequestMapping(value="/cheackTxlist",method = RequestMethod.GET)
    public Object cheackTxList(@RequestParam(value = "message") String message){
        List<String> strings=new ArrayList<>();
        try{
           String[] list= message.trim().replaceAll("\r|\n", "").split(",");
            for(int x=0;x<list.length;x++){
                byte[] hash=Hex.decodeHex(list[0].toCharArray());
                int s=tmpl.queryForObject("select count(*) from transaction t where t.tx_hash=?", new Object[]{hash}, Integer.class);
                if(s==0){
                    strings.add(list[x]);
                }
            }
            return strings;
        }catch (Exception e){
            e.printStackTrace();
            return "Exception";
        }
    }
}

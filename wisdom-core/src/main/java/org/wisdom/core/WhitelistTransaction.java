package org.wisdom.core;

import com.alibaba.fastjson.JSONArray;
import org.apache.commons.io.IOUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class WhitelistTransaction {

    private Set<String> stringSet;

    public WhitelistTransaction() throws IOException {
        Resource resource=new ClassPathResource("genesis/whitelist.json");
        String str=new String(IOUtils.toByteArray(resource.getInputStream()));
        List<String> list=(List<String>)JSONArray.parseArray(str,String.class);
        stringSet=new HashSet(list);
    }


    public boolean IsUnchecked(String txhash){
        if(stringSet.contains(txhash)){
            return true;
        }
        return false;
    }
}

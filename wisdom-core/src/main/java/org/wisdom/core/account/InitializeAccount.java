package org.wisdom.core.account;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.io.*;

@Component
public class InitializeAccount {

    private JdbcTemplate tmpl;

    private boolean miner;

    public static class Initilize{
        private String version;
        private boolean initialize;

        public Initilize(String version, boolean initialize) {
            this.version = version;
            this.initialize = initialize;
        }

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }

        public boolean isInitialize() {
            return initialize;
        }

        public void setInitialize(boolean initialize) {
            this.initialize = initialize;
        }
    }

    @Autowired
    public InitializeAccount(JdbcTemplate tmpl, @Value("${wisdom.consensus.enable-mining}") String miner ) throws IOException {
        this.tmpl=tmpl;
        this.miner= Boolean.parseBoolean(miner);
        if(!this.miner){
            long height=tmpl.queryForObject("select count(*) from header",Integer.class);
            String path=System.getProperty("user.dir")+ File.separator+"version.json";
            File file = new File(path);
            if(!file.exists()){
                path=System.getProperty("user.dir")+ File.separator+"wisdom-core"+File.separator+"version.json";
                file = new File(path);
            }
            Resource resource = new FileSystemResource(file);
            String str = IOUtils.toString(new InputStreamReader(resource.getInputStream(),"UTF-8"));
            Initilize initilize=JSONObject.parseObject(str, Initilize.class);
            if(height==0){//初始化
                if(initilize.getVersion().equals("v0.0.2")){
                    if(initilize.isInitialize()){
                        initilize.setInitialize(false);
                        JSONObject jsonObj = (JSONObject) JSON.toJSON(initilize);
                        String s = jsonObj.toString();
                        byte[] b = s.getBytes();
                        OutputStream os = new FileOutputStream(file);
                        os.write(b);
                        os.close();
                    }
                }
            }else{
                if(initilize.getVersion().equals("v0.0.2")){
                    if(initilize.isInitialize()){
                        tmpl.batchUpdate("delete  from header where 1 = 1",
                                "delete from transaction where 1 = 1",
                                "delete from transaction_index where 1 = 1",
                                "delete from account where 1 = 1",
                                "delete from incubator_state where 1 = 1");
                        initilize.setInitialize(false);
                        JSONObject jsonObj = (JSONObject) JSON.toJSON(initilize);
                        String s = jsonObj.toString();
                        byte[] b = s.getBytes();
                        OutputStream os = new FileOutputStream(file);
                        os.write(b);
                        os.close();
                    }
                }
            }
        }
    }
}

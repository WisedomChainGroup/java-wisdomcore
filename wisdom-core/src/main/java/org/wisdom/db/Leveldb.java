package org.wisdom.db;

import org.iq80.leveldb.*;
import org.iq80.leveldb.impl.Iq80DBFactory;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

public class Leveldb {

    private DB db;
    private Charset CHARSET;
    private String path;
    private File file;
    private Options options;

    public Leveldb(){
        this.db=null;
        this.CHARSET=Charset.forName("utf-8");
        this.path=System.getProperty("user.dir")+File.separator+"leveldb";
        this.file=new File(path);
        options=new Options();
    }

    public void addPoolDb(String key,String noncepoolval){
        try {
            DBFactory factory = new Iq80DBFactory();
            options.createIfMissing(true);
            this.db = factory.open(file, options);
            byte[] keyByte = key.getBytes(CHARSET);
            // 会写入磁盘中
            this.db.put(keyByte, noncepoolval.getBytes(CHARSET));
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if (db != null) {
                try {
                    db.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }

    public String readPoolDb(String key){
        String noncepool = "";
        try {
            DBFactory factory = new Iq80DBFactory();
            options.createIfMissing(true);
            this.db = factory.open(file, options);
            byte[] valueByte = db.get(key.getBytes(CHARSET));
            if(valueByte!=null && valueByte.length>0){
                return new String(valueByte);
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            if (db != null) {
                try {
                    db.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
        return noncepool;
    }


}

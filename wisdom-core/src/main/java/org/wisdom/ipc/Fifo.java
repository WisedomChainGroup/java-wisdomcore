package org.wisdom.ipc;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.wisdom.ApiResult.APIResult;
import org.wisdom.Controller.RPCClient;
import org.wisdom.core.Block;
import org.wisdom.core.account.Transaction;
import org.wisdom.p2p.PeerServer;
import org.wisdom.service.CommandService;
import org.wisdom.sync.TransactionHandler;

import java.io.*;
import java.util.Collections;

@Component
public class Fifo implements ApplicationRunner, ApplicationListener<Fifo.FifoMessageEvent> {

    private static final Logger logger = LoggerFactory.getLogger(Fifo.class);

    @Autowired
    private ApplicationContext ctx;

    private FileReader reader;

    private FileWriter writer;


    @Autowired
    CommandService commandService;

    @Value("${p2p.mode}")
    private String p2pMode;

    @Autowired
    private TransactionHandler transactionHandler;

    @Autowired
    RPCClient RPCClient;

    @Value("${wisdom.version}")
    private String version;

    @Value("${node-character}")
    private String character;

    @Autowired
    private PeerServer peerServer;

    @Autowired
    private Block genesis;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        if (!isLinuxSystem()) {
            return;
        }
        initFifo();
        while (true) {
            char[] a = new char[1024];
            if ((reader.read(a)) != -1) {
                ctx.publishEvent(new FifoMessageEvent(this, String.valueOf(a)));
            } else {
                Thread.sleep(100);
                continue;
            }
            if (!String.valueOf(a).equals("") && String.valueOf(a).contains("exit")) {
                break;
            }
        }
        closeFifo();
    }

    private void initFifo() throws IOException, InterruptedException {
        File readFile = new File("/ipc/pipe.in");
        if (!readFile.exists()) {
            readFile = createFifoPipe("/ipc/pipe.in");
        }
        File writeFile = new File("/ipc/pipe.out");
        if (!writeFile.exists()) {
            writeFile = createFifoPipe("/ipc/pipe.out");
        }
        reader = new FileReader(readFile);
        writer = new FileWriter(writeFile);
    }

    private void closeFifo() throws IOException {
        reader.close();
        writer.close();
    }

    /**
     * isLinuxSystem 是否是linux系统
     *
     * @return bool
     */
    private boolean isLinuxSystem() {
        String os = System.getProperty("os.name");
        return !os.toLowerCase().startsWith("win");
    }

    /**
     * createFifoPipe 创建 fifo 文件
     *
     * @param fifoName 文件名
     * @return file
     * @throws IOException
     * @throws InterruptedException
     */
    public File createFifoPipe(String fifoName) throws IOException, InterruptedException {
        Process process;
        String[] command = new String[]{"mkfifo", fifoName};
        process = Runtime.getRuntime().exec(command);
        process.waitFor();
        return new File(fifoName);
    }


    @Override
    public void onApplicationEvent(FifoMessageEvent event) {
        try {
            JSONObject jo = (JSONObject) JSONObject.parse(event.message);
            String message = jo.getString("message");
            String type = jo.getString("type");
            String result = dealMessage(message, type);
            writer.write(result);
            writer.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String dealMessage(String message, String type) {
        switch (type) {
            case "sendTranInfo":
                return sendTranInfo(message);
            case "getNodeInfo":
                return getNodeInfo();
        }
        return "";
    }

    private String getNodeInfo() {
        String networkType;
        if (genesis.getHashHexString().equals("add27a8cf6a42e30334e9f100cd42643ac0e2eed6896e9289bbcf8a8adaf814b")) {
            networkType = "网络: 主网";
        } else {
            networkType = "网络: 测试网";
        }
        int port = peerServer.getPort();
        String publicKey = peerServer.getNodePubKey();
        String IP = peerServer.getIP();
        JSONObject jsonObject=new JSONObject();
        jsonObject.put("ip",IP);
        jsonObject.put("publicKey",publicKey);
        jsonObject.put("character",character);
        jsonObject.put("version",version);
        jsonObject.put("networkType",networkType);
        jsonObject.put("port",port);
        return jsonObject.toJSONString();
    }

    private String sendTranInfo(String tranInfo) {
        try {
            byte[] traninfos = Hex.decodeHex(tranInfo.toCharArray());
            APIResult result = commandService.verifyTransfer(traninfos);
            if (result.getCode() == 2000) {
                Transaction t = (Transaction) result.getData();
                if (p2pMode.equals("rest")) {
                    RPCClient.broadcastTransactions(Collections.singletonList(t));
                } else {
                    transactionHandler.broadcastTransactions(Collections.singletonList(t));
                }
            }
            return JSONObject.toJSONString(result);
        } catch (DecoderException e) {
            APIResult apiResult = new APIResult();
            apiResult.setCode(5000);
            apiResult.setMessage("Error");
            return JSONObject.toJSONString(apiResult);
        }
    }

    public static class FifoMessageEvent extends ApplicationEvent {
        private String message;

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        FifoMessageEvent(Object source, String message) {
            super(source);
            this.message = message;
        }
    }
}

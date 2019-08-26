package org.wisdom.ipc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.io.*;

@Component
public class Fifo implements ApplicationRunner, ApplicationListener<Fifo.FifoMessageEvent> {

    private static final Logger logger = LoggerFactory.getLogger(Fifo.class);

    @Autowired
    private ApplicationContext ctx;

    private BufferedWriter bw;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        if (!isLinuxSystem()){
            return;
        }

        File readFile = new File("/ipc/pipe.in");
        if (!readFile.exists()) {
            readFile = createFifoPipe("/ipc/pipe.in");
        }

        File writeFile = new File("/ipc/pipe.out");
        if (!writeFile.exists()) {
            writeFile = createFifoPipe("/ipc/pipe.out");
        }

        FileReader reader = new FileReader(readFile);
        BufferedReader br = new BufferedReader(reader);

        FileWriter write = new FileWriter(writeFile);
        bw = new BufferedWriter(write);

        while (true) {
            // read line by line
            String line = "";
            if ((line = br.readLine()) != null) {
                ctx.publishEvent(new FifoMessageEvent(this, line));
            } else {
                Thread.sleep(1);
                continue;
            }
            if (!line.equals("") && line.contains("exit")) {
                break;
            }
        }
        reader.close();
        write.close();
        br.close();
        bw.close();
    }

    /**
     * isLinuxSystem 是否是linux系统
     * @return bool
     */
    private boolean isLinuxSystem(){
        String os = System.getProperty("os.name");
        return !os.toLowerCase().startsWith("win");
    }

    /**
     * createFifoPipe 创建 fifo 文件
     * @param fifoName 文件名
     * @return file
     * @throws IOException
     * @throws InterruptedException
     */
    public File createFifoPipe(String fifoName) throws IOException, InterruptedException {
        Process process;
        String[] command = new String[] {"mkfifo", fifoName};
        process = Runtime.getRuntime().exec(command);
        process.waitFor();
        return new File(fifoName);
    }


    @Override
    public void onApplicationEvent(FifoMessageEvent fifo) {
        try {
            bw.write(fifo.message);
            bw.flush();
        } catch (IOException e) {
            e.printStackTrace();
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

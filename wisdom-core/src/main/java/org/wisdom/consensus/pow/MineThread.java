package org.wisdom.consensus.pow;

import org.apache.commons.codec.binary.Hex;
import org.wisdom.encoding.BigEndian;
import org.wisdom.core.Block;
import org.wisdom.core.event.NewBlockMinedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.Date;

@Component
@Scope("prototype")
public class MineThread {
    private volatile boolean terminated;
    private static final Logger logger = LoggerFactory.getLogger(Miner.class);

    private static SecureRandom SECURE_RANDOM;

    static {
        SECURE_RANDOM = new SecureRandom();
    }

    @Autowired
    private ApplicationContext ctx;

    @Async
    public void mine(Block block, long parentBlockTimeStamp, long endTime) {
        block.setWeight(1);
        logger.info("start mining at height " + block.nHeight + " target = " + Hex.encodeHexString(block.nBits));
        block = pow(block, parentBlockTimeStamp, endTime);
        terminated = true;
        if (block != null) {
            ctx.publishEvent(new NewBlockMinedEvent(this, block));
        }
    }

    public Block pow(Block block, long parentBlockTimeStamp, long endTime) {
        byte[] nBits = block.nBits;
        while (!terminated) {
            byte[] tmp = new byte[32];
            SECURE_RANDOM.nextBytes(tmp);
            block.nNonce = tmp;
            block.nTime = System.currentTimeMillis() / 1000;
            if (block.nTime <= parentBlockTimeStamp){
                block.nTime = parentBlockTimeStamp + 1;
            }
            if(block.nTime > endTime){
                logger.error("mining timeout, dead line = " + new Date(endTime * 1000).toString() + "consider upgrade your hardware");
                return null;
            }
            byte[] hash = Block.calculatePOWHash(block);
            if (BigEndian.compareUint256(hash, nBits) < 0) {
                logger.info("mining success");
                return block;
            }
        }
        logger.info("mining terminated");
        return null;
    }

    public void stop() {
        terminated = true;
        logger.info("mining terminated");
    }

    public boolean isTerminated() {
        return terminated;
    }
}

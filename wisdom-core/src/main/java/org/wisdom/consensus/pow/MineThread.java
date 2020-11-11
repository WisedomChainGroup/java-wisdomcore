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

package org.wisdom.consensus.pow;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Hex;
import org.wisdom.core.account.Transaction;
import org.wisdom.encoding.BigEndian;
import org.wisdom.core.Block;
import org.wisdom.core.event.NewBlockMinedEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.wisdom.p2p.WisdomOuterClass;
import org.wisdom.vm.abi.WASMTXPool;

import java.security.SecureRandom;
import java.util.Date;
import java.util.stream.Collectors;

@Component
@Scope("prototype")
@Slf4j(topic = "miner")
public class MineThread {
    private volatile boolean terminated;

    private static SecureRandom SECURE_RANDOM;

    static {
        SECURE_RANDOM = new SecureRandom();
    }

    @Autowired
    private ApplicationContext ctx;

    @Autowired
    private WASMTXPool wasmtxPool;

    // 用于计算完成工作量证明的平均时间，单位是毫秒
    private static final Long[] POW_CONSUMES = new Long[8];

    private static int INDEX = 0;

    // 成功完成记录一次完成工作量证明消耗的时间
    private static void record(long consume){
        synchronized (POW_CONSUMES){
            TIMEOUT = 0;
            POW_CONSUMES[INDEX] = consume;
            INDEX++;
            INDEX = INDEX % POW_CONSUMES.length;
        }
    }

    private static void recordTimeOut(){
        synchronized (POW_CONSUMES){
            TIMEOUT++;
        }
    }

    // 记录最近发生了几次 mining timeout，每次 mining timeout 会导致下次 powAvg 的最终结果增加 500 毫秒
    // 直到 mining 成功，解除对 powAvg 的临时调整
    private static int TIMEOUT = 0;

    // 计算工作量证明的平均时间，单位是毫秒
    public static long powAvg(){
        synchronized (POW_CONSUMES){
            long sum = 0;
            int count = 0;
            for (int i = 0; i < POW_CONSUMES.length; i++) {
                Long c = POW_CONSUMES[i];
                if(c == null)
                    continue;
                sum += c;
                count ++;
            }
            if(count == 0)
                return Long.MAX_VALUE;
            return sum / count + TIMEOUT * 500;
        }
    }

    @Async
    public void mine(BlockAndTask b, long startTime, long endTime) {
        Block block = b.getBlock();
        block.setWeight(1);
        log.info("start mining at height " + block.nHeight + " target = " + Hex.encodeHexString(block.nBits));
        block = pow(b, startTime, endTime);
        terminated = true;
        if (block != null) {
            ctx.publishEvent(new NewBlockMinedEvent(this, block));
        }
    }

    public Block pow(BlockAndTask blockAndTask, long parentBlockTimeStamp, long endTime) {
        Block block = blockAndTask.getBlock();
        long start = System.currentTimeMillis();
        byte[] nBits = block.nBits;
        while (!terminated) {
            byte[] tmp = new byte[32];
            SECURE_RANDOM.nextBytes(tmp);
            block.nNonce = tmp;
            block.nTime = System.currentTimeMillis() / 1000;
            if (block.nTime <= parentBlockTimeStamp) {
                try {
                    Thread.sleep(1000);
                } catch (Exception e) {

                }
                continue;
            }
            if (block.nTime >= endTime) {
                log.error("mining timeout, dead line = " + new Date(endTime * 1000).toString() + "consider upgrade your hardware");
                recordTimeOut();
                wasmtxPool.collect(block.body
                        .stream()
                        .filter(x -> x.type == Transaction.Type.WASM_DEPLOY.ordinal() || x.type == Transaction.Type.WASM_CALL.ordinal())
                        .collect(Collectors.toList()));
                return null;
            }
            byte[] hash = Block.calculatePOWHash(block);
            if (BigEndian.compareUint256(hash, nBits) < 0) {
                long end = System.currentTimeMillis();
                record(end - start);
                blockAndTask.getTask().run();
                return block;
            }
        }
        return null;
    }

    public void terminate() {
        terminated = true;
    }

    public boolean isTerminated() {
        return terminated;
    }
}

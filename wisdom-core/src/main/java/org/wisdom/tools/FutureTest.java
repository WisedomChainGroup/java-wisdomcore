package org.wisdom.tools;

import org.apache.commons.codec.binary.Hex;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.*;

public class FutureTest {

    public static final Executor executor = command -> new Thread(command).start();

    public static CompletableFuture<Boolean> newFailedFuture() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                throw new RuntimeException(e.getMessage());
            }
            throw new RuntimeException("error");
        }, executor);
    }

    public static CompletableFuture<Boolean> newSuccessfulFuture() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                return true;
            }
            return true;
        }, executor);
    }

    public static void main(String[] args) throws Exception {
//        newSuccessfulFuture().thenRunAsync(() -> System.out.println("======================"));
        newFailedFuture().handleAsync((x, e) -> {
            if (e != null){
                System.out.println("============================");
            }
            return false;
        });
    }
}

package org.wisdom.tools;

import java.util.concurrent.*;

public class FutureTest {

    public static final Executor executor = command -> new Thread(command).start();

    public static CompletableFuture newFuture(){
        return CompletableFuture.supplyAsync(() -> {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                throw new IllegalStateException(e);
            }
            System.out.println("Result of the asynchronous computation");
            return true;
        }, executor);
    }

    public static void main(String[] args) throws Exception{
        CompletableFuture[] futures = new CompletableFuture[1000];
        for(int i = 0; i < futures.length; i++){
            futures[i] = newFuture();
        }
        CompletableFuture.allOf(futures);
        TimeUnit.SECONDS.sleep(5);
    }
}

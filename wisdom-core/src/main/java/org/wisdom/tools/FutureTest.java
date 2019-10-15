package org.wisdom.tools;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class FutureTest {

    public static CompletableFuture newFuture(){
        return CompletableFuture.supplyAsync(() -> {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                throw new IllegalStateException(e);
            }
            System.out.println("Result of the asynchronous computation");
            return true;
        });
    }

    public static void main(String[] args){
        CompletableFuture[] futures = new CompletableFuture[]{
                newFuture(), newFuture(), newFuture(), newFuture(), newFuture()
        };
        CompletableFuture.allOf(futures).join();
    }
}

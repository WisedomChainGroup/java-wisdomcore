package org.wisdom.tools;

import org.springframework.core.task.support.ExecutorServiceAdapter;

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

    public static void main(String[] args){
        CompletableFuture[] futures = new CompletableFuture[100];
        for(int i = 0; i < futures.length; i++){
            futures[i] = newFuture();
        }
        CompletableFuture.allOf(futures).join();
    }
}

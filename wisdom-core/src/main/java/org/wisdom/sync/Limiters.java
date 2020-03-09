package org.wisdom.sync;

import com.google.common.util.concurrent.RateLimiter;

import java.util.Map;

public class Limiters {
    private RateLimiter status;
    private RateLimiter getBlocks;

    public Limiters(Map<String, Double> config) {
        if (config == null) return;
        if (config.get("status") != null)
            this.status = RateLimiter.create(config.get("status"));
        if (config.get("get-blocks") != null) {
            this.getBlocks = RateLimiter.create(config.get("get-blocks"));
        }
    }

    public RateLimiter status() {
        return status;
    }

    public RateLimiter getBlocks() {
        return getBlocks;
    }
}

package com.laurefindel.finance.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.async")
public class AsyncProperties {

    private long replenishDelayMs = 15000;

    public long getReplenishDelayMs() {
        return replenishDelayMs;
    }

    public void setReplenishDelayMs(long replenishDelayMs) {
        this.replenishDelayMs = replenishDelayMs;
    }
}

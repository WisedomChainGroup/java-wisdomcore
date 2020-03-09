package org.wisdom;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

@ConfigurationProperties(prefix = "wisdom.sync")
@Component
@Setter
@Getter
public class SyncConfig {


    private Map<String, Double> rateLimits;

    private long lockTimeOut;

    private long blockWriteRate;
}

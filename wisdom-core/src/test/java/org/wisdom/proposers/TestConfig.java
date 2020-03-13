package org.wisdom.proposers;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "wisdom-test")
@Component
@NoArgsConstructor
@Getter
@Setter
public class TestConfig {
    @NonNull
    private String blocksDirectory;
    @NonNull
    private String genesisDumpOut;
    private long genesisDumpHeight;
}

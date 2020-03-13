package org.wisdom.core;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.wisdom.core.event.NewBlockEvent;
import org.wisdom.pool.PeningTransPool;

@Component
public class StatePoolUpdate implements ApplicationListener<NewBlockEvent> {

    @Autowired
    PeningTransPool peningTransPool;

    @Override
    public void onApplicationEvent(NewBlockEvent event) {
        Block b = event.getBlock();
        peningTransPool.updatePool(b.body, 1, b.nHeight);
    }
}

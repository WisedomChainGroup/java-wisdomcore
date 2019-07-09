package org.wisdom.Controller;

import org.wisdom.core.event.NewBlockMinedEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class ProposalHandler implements ApplicationListener<NewBlockMinedEvent> {

    @Autowired
    private ConsensusClient consensusClient;

    @Override
    public void onApplicationEvent(NewBlockMinedEvent event) {
        consensusClient.proposalBlock(event.getBlock());
    }
}

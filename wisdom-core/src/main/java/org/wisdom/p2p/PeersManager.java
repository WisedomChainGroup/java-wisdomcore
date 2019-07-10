package org.wisdom.p2p;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PeersManager {

    @Autowired
    private RawClient rawClient;
}

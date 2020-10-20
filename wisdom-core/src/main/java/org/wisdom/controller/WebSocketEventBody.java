package org.wisdom.controller;

import lombok.AllArgsConstructor;
import org.tdf.rlp.RLPList;

@AllArgsConstructor
public class WebSocketEventBody {
    private byte[] pkHash;
    private String name;
    private RLPList outputs;
}

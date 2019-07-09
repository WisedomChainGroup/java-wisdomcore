package org.wisdom.protobuf;

import java.net.InetSocketAddress;

public class DiscoveryModel {

    private byte[] by;
    private InetSocketAddress add;

    public DiscoveryModel(){}

    public DiscoveryModel(byte[] by,InetSocketAddress add){
        this.by=by;
        this.add=add;
    }

    public byte[] getBy() {
        return by;
    }

    public void setBy(byte[] by) {
        this.by = by;
    }

    public InetSocketAddress getAdd() {
        return add;
    }

    public void setAdd(InetSocketAddress add) {
        this.add = add;
    }
}

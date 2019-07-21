package org.wisdom.p2p;

public interface Plugin {
    void onMessage(Context context, PeerServer server);

    void onStart(PeerServer server);
}

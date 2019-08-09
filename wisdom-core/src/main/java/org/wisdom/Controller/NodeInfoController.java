package org.wisdom.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.wisdom.ApiResult.APIResult;
import org.wisdom.p2p.Peer;
import org.wisdom.p2p.PeersManager;

import java.util.HashMap;
import java.util.Map;

@RestController
public class NodeInfoController {
    @Autowired
    private PeersManager peersManager;

    @Value("${wisdom.version}")
    private String version;

    @Value("${node-character}")
    private String character;

    @Value("${p2p.mode}")
    private String p2pMode;

    @Value("${p2p.enable-discovery}")
    private boolean enableDiscovery;

    @GetMapping(value = {"/version", "/"}, produces = "application/json")
    public Object getVersion() {
        Map<String, Object> info = new HashMap<>();
        info.put("version", this.version);
        info.put("character", character);
        return APIResult.newFailResult(2000, "SUCCESS", info);
    }

    @GetMapping(value = "/peers/status", produces = "application/json")
    public Object getP2P() {
        Map<String, Object> info = new HashMap<>();
        info.put("peers", peersManager.getPeers().stream().map(Peer::toString).toArray());
        info.put("self", peersManager.getSelfAddress());
        info.put("p2pMode", p2pMode);
        info.put("enableDiscovery", enableDiscovery);
        return APIResult.newFailResult(2000, "SUCCESS", info);
    }
}

package org.wisdom.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.wisdom.ApiResult.APIResult;
import org.wisdom.ipc.IpcConfig;
import org.wisdom.p2p.Peer;
import org.wisdom.p2p.PeersManager;

import java.util.HashMap;
import java.util.Map;

@RestController
public class NodeInfoController {
    @Autowired
    private PeersManager peersManager;

    @Value("${node-character}")
    private String character;

    @Value("${p2p.enable-discovery}")
    private boolean enableDiscovery;

    @Value("${p2p.max-blocks-per-transfer}")
    private int maxBlocksPerTransfer;

    @Autowired
    IpcConfig config;

    @GetMapping(value = {"/version", "/"}, produces = "application/json")
    public Object getVersion() {
        Map<String, Object> info = new HashMap<>();
        info.put("version", config.getVersion());
        info.put("character", character);
        return APIResult.newFailResult(2000, "SUCCESS", info);
    }

    @GetMapping(value = "/peers/status", produces = "application/json")
    public Object getP2P() {
        Map<String, Object> info = new HashMap<>();
        info.put("peers", peersManager.getPeers().stream().map(Peer::toString).toArray());
        info.put("self", peersManager.getSelfAddress());
        info.put("p2pMode", config.getP2pMode());
        info.put("enableDiscovery", enableDiscovery);
        info.put("maxBlocksPerTransfer", maxBlocksPerTransfer);
        return APIResult.newFailResult(2000, "SUCCESS", info);
    }
}

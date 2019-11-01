package org.wisdom.controller;

import org.apache.commons.codec.binary.Hex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.wisdom.ApiResult.APIResult;
import org.wisdom.account.PublicKeyHash;
import org.wisdom.consensus.pow.EconomicModel;
import org.wisdom.consensus.pow.ProposersState;
import org.wisdom.core.Block;
import org.wisdom.core.account.Transaction;
import org.wisdom.db.AccountState;
import org.wisdom.db.StateDB;
import org.wisdom.encoding.JSONEncodeDecoder;
import org.wisdom.keystore.crypto.PublicKey;
import org.wisdom.p2p.Peer;
import org.wisdom.p2p.PeerServer;
import org.wisdom.p2p.PeersManager;
import org.wisdom.util.Address;

import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
public class NodeInfoController {
    @Autowired
    private PeerServer peerServer;

    @Value("${wisdom.version}")
    private String version;

    @Value("${node-character}")
    private String character;

    @Value("${p2p.mode}")
    private String p2pMode;

    @Value("${p2p.enable-discovery}")
    private boolean enableDiscovery;

    @Value("${p2p.max-blocks-per-transfer}")
    private int maxBlocksPerTransfer;

    @Value("${wisdom.consensus.allow-fork}")
    private boolean allowFork;

    @Autowired
    private StateDB stateDB;

    @Autowired
    private JSONEncodeDecoder encodeDecoder;

    @Value("${wisdom.consensus.blocks-per-era}")
    int blocksPerEra;

    @Value("${wisdom.allow-miner-joins-era}")
    int allowMinersJoinEra;

    @GetMapping(value = {"/version", "/"}, produces = "application/json")
    public Object getVersion() {
        Map<String, Object> info = new HashMap<>();
        info.put("version", this.version);
        info.put("character", character);
        return APIResult.newFailResult(2000, "SUCCESS", info);
    }

    public static Map<String, Object> getPeerInfo(Peer peer) {
        Map<String, Object> res = new HashMap<>();
        res.put(peer.toString(), peer.score);
        return res;
    }

    @GetMapping(value = "/peers/status", produces = "application/json")
    public Object getP2P() {
        Map<String, Object> info = new HashMap<>();
        info.put("peers", peerServer.getPeers().stream().map(NodeInfoController::getPeerInfo));
        info.put("bootstraps", peerServer.getBootstraps().stream().map(Peer::toString));
        info.put("blockList", peerServer.getPeersCache().getBlocked().stream().map(NodeInfoController::getPeerInfo));
        info.put("trusted", peerServer.getPeersCache().getTrusted().stream().map(Peer::toString));
        info.put("self", peerServer.getSelf().toString());
        info.put("p2pMode", p2pMode);
        info.put("enableDiscovery", enableDiscovery);
        info.put("maxBlocksPerTransfer", maxBlocksPerTransfer);
        info.put("allowFork", allowFork);
        return APIResult.newFailResult(2000, "SUCCESS", info);
    }

    @GetMapping(value = "/blocks/unconfirmed", produces = "application/json")
    public Object getNotConfirmed() {
        return encodeDecoder.encodeBlocks(stateDB.getAll());
    }

    @GetMapping(value = "/proposers", produces = "application/json")
    public Object getProposers() {
        Block best = stateDB.getBestBlock();
        Map<String, Object> res = new HashMap<>();
        res.put("height", best.nHeight);
        if (allowMinersJoinEra >= 0) {
            res.put("enableMinerJoins", true);
            res.put("minerJoinsHeight", allowMinersJoinEra * blocksPerEra + 1);
        } else {
            res.put("enableMinerJoins", false);
        }
        ProposersState proposersState = stateDB.getProposersFactory().getInstance(best);
        res.put("proposers", proposersState.getProposers().stream().map(p -> p.publicKeyHash).toArray());
        res.put("blockList", proposersState.getBlockList());
        res.put("votes", proposersState.getCandidates().stream().map(c -> {
            Map<String, Object> m = new HashMap<>();
            m.put("publicKeyHash", c.publicKeyHash);
            m.put("votes", c.getVotes());
            m.put("mortgage", c.mortgage);
            return m;
        }).toArray());
        return res;
    }

    @GetMapping(value = "/account/{account}", produces = APPLICATION_JSON_VALUE)
    public Object getAccount(@PathVariable("account") String account) {
        Block best = stateDB.getBestBlock();
        Optional<PublicKeyHash> publicKeyHash = PublicKeyHash.fromHex(account);
        if (!publicKeyHash.isPresent()) {
            return "invalid account";
        }
        AccountState state = stateDB.getAccount(best.getHash(), publicKeyHash.get().getPublicKeyHash());
        if (state == null) {
            return "database error";
        }
        state.getAccount().setBlockHeight(best.nHeight);
        return encodeDecoder.encode(new Account(
                state.getAccount().getPubkeyHash(),
                state.getAccount().getNonce(),
                state.getAccount().getBalance(),
                state.getAccount().getIncubatecost(),
                state.getAccount().getMortgage(),
                state.getAccount().getVote()
        ));
    }

    public static class Vote{
        public String address;
        public long amount;
        public long accumulated;

        public Vote() {
        }

        public Vote(String address, long amount, long accumulated) {
            this.address = address;
            this.amount = amount;
            this.accumulated = accumulated;
        }
    }

    @GetMapping(value = "/votes/{account}", produces = APPLICATION_JSON_VALUE)
    public Object getVotes(@PathVariable("account") String account) {
        Block best = stateDB.getBestBlock();
        Optional<PublicKeyHash> o = PublicKeyHash.fromHex(account);
        if (!o.isPresent()) {
            return "invalid account";
        }
        PublicKeyHash publicKeyHash = o.get();
        ProposersState state = stateDB.getProposersFactory().getInstance(best);
        return state.getAll().get(publicKeyHash.getHex()).getReceivedVotes()
                .values().stream()
                .map(x -> new Vote(x.from.getAddress(), x.amount, x.accumulated))
                .collect(Collectors.groupingBy(
                        x -> x.address,
                        Collectors.reducing(new Vote(), (x, y) -> {
                            if (x.address == null) return y;
                            if (y.address == null) return x;
                            return new Vote(x.address, x.amount + y.amount, x.accumulated + y.accumulated);
                        })
                ));

    }

    private static class Account {
        public byte[] publicKeyHash;

        public String address;

        public long nonce;

        public String balance;

        public String incubateCost;


        public String mortgage;

        public String votes;

        public Account(byte[] publicKeyHash, long nonce, long balance, long incubateCost, long mortgage, long votes) {
            this.publicKeyHash = publicKeyHash;
            this.nonce = nonce;
            this.balance = balance * 1.0 / EconomicModel.WDC + " WDC";
            this.incubateCost = incubateCost * 1.0 / EconomicModel.WDC + " WDC";
            this.mortgage = mortgage * 1.0 / EconomicModel.WDC + " WDC";
            this.votes = votes * 1.0 / EconomicModel.WDC + " WDC";
            this.address = Address.publicKeyHashToAddress(publicKeyHash);
        }
    }
}

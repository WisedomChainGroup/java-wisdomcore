package org.wisdom.db;

import lombok.Getter;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.tdf.common.trie.Trie;
import org.tdf.common.util.ByteArraySet;
import org.tdf.common.util.HexBytes;
import org.tdf.common.util.LRUMap;
import org.wisdom.consensus.pow.Proposer;
import org.wisdom.consensus.pow.ProposersState;
import org.wisdom.core.Block;
import org.wisdom.encoding.JSONEncodeDecoder;
import org.wisdom.genesis.Genesis;
import org.wisdom.keystore.wallet.KeystoreAction;

import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.wisdom.db.CandidateUpdater.MAXIMUM_PROPOSERS;
import static org.wisdom.db.CandidateUpdater.MINIMUM_PROPOSER_MORTGAGE;

@Component
public class CandidateStateTrie extends EraLinkedStateTrie<Candidate> {
    private CandidateUpdater candidateUpdater;
    private static final JSONEncodeDecoder codec = new JSONEncodeDecoder();

    private static final int POW_WAIT_FACTOR = 3;
    private static final Set<byte[]> WHITE_LIST = new ByteArraySet(Stream.of(
            "552f6d4390367de2b05f4c9fc345eeaaf0750db9",
            "5b0a4c7e31c3123db40a4c14200b54b8e358294b",
            "08f74cb61f41f692011a5e66e3c038969eb0ec75",
            "12acb24a3bbc5b9eaa32b6f8ae5e6c66c8c152aa",
            "15f581858068ed39f7e8cf8e9fdec5dfdae9cf15",


            "2c5de963729478a48d89df268e8c29fd94fd5182",
            "3e8f7c9406e8bb62363eaed0d1e3def77faa9df9",
            "83670eb15325c86b479abb87191be93d3470f91a"
    ).map(x -> {
        try {
            return Hex.decodeHex(x);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }).collect(Collectors.toList()));

    private long allowMinersJoinEra;

    private List<byte[]> initialProposers;

    private long blockIntervalSwitchEra;

    private int blockIntervalSwitchTo;

    private int initialBlockInterval;

    @Getter
    private LRUMap<HexBytes, List<Candidate>> cache;

    public CandidateStateTrie(
            Block genesis,
            Genesis genesisJSON,
            DatabaseStoreFactory factory,
            CandidateUpdater candidateUpdater,
            @Value("${wisdom.consensus.blocks-per-era}") int blocksPerEra,
            @Value("${wisdom.allow-miner-joins-era}") long allowMinersJoinEra,
            @Value("${miner.validators}") String validatorsFile,
            @Value("${wisdom.block-interval-switch-era}") long blockIntervalSwitchEra,
            @Value("${wisdom.block-interval-switch-to}") int blockIntervalSwitchTo,
            @Value("${wisdom.consensus.block-interval}") int initialBlockInterval
    ) throws Exception {
        super(Candidate.class, candidateUpdater, genesis, factory, false, false, blocksPerEra);
        this.cache = new LRUMap<>();
        this.cache = this.cache.withMaximumSize(16);
        this.candidateUpdater = candidateUpdater;
        this.candidateUpdater.setCandidateStateTrie(this);
        this.blocksPerEra = blocksPerEra;
        this.allowMinersJoinEra = allowMinersJoinEra;
        this.blockIntervalSwitchEra = blockIntervalSwitchEra;
        this.blockIntervalSwitchTo = blockIntervalSwitchTo;
        this.initialBlockInterval = initialBlockInterval;

        Resource resource = new FileSystemResource(validatorsFile);
        if (!resource.exists()) {
            resource = new ClassPathResource(validatorsFile);
        }

        initialProposers = Arrays.stream(
                codec.decode(IOUtils.toByteArray(resource.getInputStream()), String[].class)
        ).map(v -> {
            try {
                URI uri = new URI(v);
                return KeystoreAction.addressToPubkeyHash(uri.getRawUserInfo());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }).collect(Collectors.toList());
    }

    private int blocksPerEra;

    private long getPowWait(Block parent) {
        if (blockIntervalSwitchEra >= 0 && eraLinker.getEraAtBlockNumber(parent.nHeight + 1) >= blockIntervalSwitchEra) {
            return blockIntervalSwitchTo * POW_WAIT_FACTOR;
        }
        return initialBlockInterval * POW_WAIT_FACTOR;
    }

    @Override
    protected void setRepository(WisdomRepository repository) {
        super.setRepository(repository);
        candidateUpdater.setRepository(repository);
    }

    @Override
    protected String getPrefix() {
        return "candidate";
    }

    @Override
    void updateHook(List<Block> blocks, Trie<byte[], Candidate> trie) {
        generateProposers(blocks, trie);
    }

    List<byte[]> getProposersByEraLst(byte[] hash, long height){
        if(height % eraLinker.getBlocksPerEra() != 0) throw new RuntimeException("unreachable");

        boolean enableMultiMiners = allowMinersJoinEra >= 0 &&
                eraLinker.getEraAtBlockNumber(height + 1) >= allowMinersJoinEra;

        if (!enableMultiMiners && height >= 9235) {
            return initialProposers.subList(0, 1);
        }

        if (!enableMultiMiners) {
            return initialProposers;
        }

        List<byte[]> res = cache.get(HexBytes.fromBytes(hash))
                    .stream().map(Candidate::getPublicKeyHash)
                    .map(HexBytes::getBytes)
                    .collect(Collectors.toList());

        if (height + 1 < ProposersState.COMMUNITY_MINER_JOINS_HEIGHT) {
            res = res.stream().filter(WHITE_LIST::contains).collect(Collectors.toList());
        }
        if (res.size() > 0) {
            return res;
        }
        return initialProposers;
    }

    public List<byte[]> getProposers(Block parentBlock) {
        if (parentBlock.nHeight % eraLinker.getBlocksPerEra() == 0) {
            return getProposersByEraLst(parentBlock.getHash(), parentBlock.nHeight);
        }
        Block preEraLast = eraLinker.getPrevEraLast(parentBlock);
        return getProposersByEraLst(preEraLast.getHash(), preEraLast.nHeight);
    }

    public Optional<Proposer> getProposer(Block parentBlock, long timeStamp) {
        List<HexBytes> proposers = getProposers(parentBlock).stream()
                .map(HexBytes::fromBytes)
                .collect(Collectors.toList());

        if (timeStamp <= parentBlock.nTime) {
            return Optional.empty();
        }

        if (parentBlock.nHeight == 0) {
            return Optional.of(new Proposer(proposers.get(0).getBytes(), 0, Long.MAX_VALUE));
        }

        long step = (timeStamp - parentBlock.nTime)
                / getPowWait(parentBlock) + 1;
        byte[] lastValidator = parentBlock.body.get(0).to;
        int lastValidatorIndex = proposers
                .indexOf(HexBytes.fromBytes(lastValidator));
        int currentValidatorIndex = (int) (lastValidatorIndex + step) % proposers.size();
        long endTime = parentBlock.nTime + step * getPowWait(parentBlock);
        long startTime = endTime - getPowWait(parentBlock);
        HexBytes validator = proposers.get(currentValidatorIndex);
        return Optional.of(new Proposer(
                validator.getBytes(),
                startTime,
                endTime
        ));
    }

    public void generateProposers(List<Block> blocks, Trie<byte[], Candidate> trie){
        // 重新生成 proposers
        Stream<Candidate> candidateStream = trie
                .stream()
                .map(Map.Entry::getValue)
                // 过滤掉黑名单中节点
                .filter(p -> !p.isBlocked())
                // 过滤掉抵押数量不足和投票为零的账户
                .filter(p -> p.getMortgage() >= MINIMUM_PROPOSER_MORTGAGE);
        boolean dropZeroVotes = blocks.get(0).getnHeight() > candidateUpdater.getWIP_12_17_HEIGHT();
        long nextEra = eraLinker.getEraAtBlockNumber(blocks.get(0).nHeight) + 1;
        if (dropZeroVotes) {
            candidateStream = candidateStream
                    .filter(x -> x.getAccumulated(nextEra) > 0);
        }
        // 按照 投票，抵押，字典从大到小排序
        List<Candidate> proposers = candidateStream.sorted((x, y) -> -compareCandidate(x, y, nextEra))
                .limit(MAXIMUM_PROPOSERS)
                .collect(Collectors.toList());

        cache.put(HexBytes.fromBytes(blocks.get(blocks.size() - 1).getHash()), proposers);
    }

    private int compareCandidate(Candidate x, Candidate y, long era) {
        if (x.getAccumulated(era) != y.getAccumulated(era)) {
            return Long.compare(x.getAccumulated(era), y.getAccumulated(era));
        }
        if (x.getMortgage() != y.getMortgage()) {
            return Long.compare(x.getMortgage(), y.getMortgage());
        }
        return x.getPublicKeyHash()
                .toHex()
                .compareTo(y.getPublicKeyHash().toHex());
    }
}

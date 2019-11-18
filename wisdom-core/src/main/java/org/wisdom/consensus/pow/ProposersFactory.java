package org.wisdom.consensus.pow;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.wisdom.core.Block;
import org.wisdom.core.state.EraLinkedStateFactory;
import org.wisdom.db.StateDB;
import org.wisdom.encoding.JSONEncodeDecoder;
import org.wisdom.keystore.wallet.KeystoreAction;

import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Component
public class ProposersFactory extends EraLinkedStateFactory<ProposersState> {
    private static final JSONEncodeDecoder codec = new JSONEncodeDecoder();
    private static final int POW_WAIT_FACTOR = 3;
    private static final int MINER_JOINS_HEIGHT = 522215;
    private static final Set<String> WHITE_LIST = Stream.of(
            "552f6d4390367de2b05f4c9fc345eeaaf0750db9",
            "5b0a4c7e31c3123db40a4c14200b54b8e358294b",
            "08f74cb61f41f692011a5e66e3c038969eb0ec75",
            "12acb24a3bbc5b9eaa32b6f8ae5e6c66c8c152aa",
            "15f581858068ed39f7e8cf8e9fdec5dfdae9cf15"
    ).collect(Collectors.toSet());


    @Value("${wisdom.consensus.block-interval}")
    private int initialBlockInterval;

    @Value("${wisdom.block-interval-switch-era}")
    private long blockIntervalSwitchEra;

    @Value("${wisdom.block-interval-switch-to}")
    private int blockIntervalSwitchTo;

    private List<String> initialProposers;

    @Value("${wisdom.allow-miner-joins-era}")
    private long allowMinersJoinEra;

    public ProposersFactory(
            ProposersState genesisState,
            @Value("${wisdom.consensus.blocks-per-era}") int blocksPerEra,
            @Value("${miner.validators}") String validatorsFile
    ) throws Exception {
        super(StateDB.CACHE_SIZE, genesisState, blocksPerEra);

        Resource resource = new FileSystemResource(validatorsFile);
        if (!resource.exists()) {
            resource = new ClassPathResource(validatorsFile);
        }

        initialProposers = Arrays.stream(
                codec.decode(IOUtils.toByteArray(resource.getInputStream()), String[].class)
        ).map(v -> {
            try {
                URI uri = new URI(v);
                return Hex.encodeHexString(KeystoreAction.addressToPubkeyHash(uri.getRawUserInfo()));
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }).collect(Collectors.toList());


    }

    private long getPowWait(Block parent) {
        if (blockIntervalSwitchEra >= 0 && getEraAtBlockNumber(parent.nHeight + 1, getBlocksPerEra()) >= blockIntervalSwitchEra) {
            return blockIntervalSwitchTo * POW_WAIT_FACTOR;
        }
        return initialBlockInterval * POW_WAIT_FACTOR;
    }

    public List<String> getProposers(Block parentBlock) {
        boolean enableMultiMiners = allowMinersJoinEra >= 0 &&
                getEraAtBlockNumber(parentBlock.nHeight + 1, this.getBlocksPerEra()) >= allowMinersJoinEra;

        if (!enableMultiMiners && parentBlock.nHeight >= 9235) {
            return initialProposers.subList(0, 1);
        }

        if (!enableMultiMiners) {
            return initialProposers;
        }

        List<String> res;
        if (parentBlock.nHeight % getBlocksPerEra() == 0) {
            ProposersState state = getFromCache(parentBlock);
            res = state.getProposers().stream().map(p -> p.publicKeyHash).collect(Collectors.toList());
        } else {
            ProposersState state = getInstance(parentBlock);
            res = state.getProposers().stream().map(p -> p.publicKeyHash).collect(Collectors.toList());
        }
        if (parentBlock.getnHeight() + 1 < MINER_JOINS_HEIGHT) {
            res = res.stream().filter(WHITE_LIST::contains).collect(Collectors.toList());
        }
        if (res.size() > 0) {
            return res;
        }
        return initialProposers;

    }

    public Optional<Proposer> getProposer(Block parentBlock, long timeStamp) {
        List<String> proposers = getProposers(parentBlock);

        if (timeStamp <= parentBlock.nTime) {
            return Optional.empty();
        }

        if (parentBlock.nHeight == 0) {
            return Optional.of(new Proposer(proposers.get(0), 0, Long.MAX_VALUE));
        }

        long step = (timeStamp - parentBlock.nTime)
                / getPowWait(parentBlock) + 1;
        String lastValidator = Hex
                .encodeHexString(
                        parentBlock.body.get(0).to
                );
        int lastValidatorIndex = proposers
                .indexOf(lastValidator);
        int currentValidatorIndex = (int) (lastValidatorIndex + step) % proposers.size();
        long endTime = parentBlock.nTime + step * getPowWait(parentBlock);
        long startTime = endTime - getPowWait(parentBlock);
        String validator = proposers.get(currentValidatorIndex);
        return Optional.of(new Proposer(
                validator,
                startTime,
                endTime
        ));
    }
}

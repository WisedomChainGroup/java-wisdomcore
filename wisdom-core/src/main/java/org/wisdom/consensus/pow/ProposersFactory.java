package org.wisdom.consensus.pow;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.tdf.common.util.ByteArraySet;
import org.tdf.common.util.HexBytes;
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
@Deprecated
// use CandidateStateTrie
public class ProposersFactory extends EraLinkedStateFactory<ProposersState> {
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
    ).map(HexBytes::decode).collect(Collectors.toList()));

//    private static final Set<String> WHITE_LIST = Stream.of(
//            "c017fd8d81fb6e5bbe56dc549c33abcf4f397332",
//            "ee649fbd62ee91dce16017152c94acdaa11abe86",
//            "a5291f6f324278866c58a0a73584ab287b99634e",
//            "7f988e4adf871abe6fb75c67e2d2d5fb114c497c",
//
//
//            "2bfdb3c647a958cdfc9b9b713d858a205bffc4d9", "2df6206b4397b2d36d8f9630ec8ff7e967a27ac4", "8b9bd2c9685ba026c28a2f7b8d60eceb48d78ad4", "8ecffa68905f574a3252770c32d39c7032106e58", "c4061e318e4ce23ae37b56568a0d2edbe8d394fe", "60f67e9af39ba22c41e38b35f856e4dc1d5dd890", "f763e6e1b877db7ca0b569c10bb96e7a34b999b8", "bd12eca4b58a85d5ffef66f89d3e3f27072fc4c9", "2c04de0200ab247954d81eac9a26024d27add58c", "1b83fceae112e4147e84886594bf2439a97ebb44", "7d4d105a3fc6db71d35ed654b1b7aab73d8fa50d", "675647683903226f4ee95c802a60902805a4e98e", "fbdacd374729b74c594cf955dc207fbb1d203a10", "93dd7c2848815428ff6a424a8a2881f72d96176e", "f12a4c8bbffd85eff63315af85639af75c7c70e8", "301dc508da19f98c1cc9df2cd32b8331ef252963", "2ba466a219d379f25e677bc880090935d996bf5a", "7a1255185f7daff03a869d3d38de4be6cff5a386", "b814681b8e4cc0e99b69b3126559abf500cd9a21", "9d1052366cdf6d0a1978bb5053333170e942e6d1"
//    ).collect(Collectors.toSet());


    @Value("${wisdom.consensus.block-interval}")
    private int initialBlockInterval;

    @Value("${wisdom.block-interval-switch-era}")
    private long blockIntervalSwitchEra;

    @Value("${wisdom.block-interval-switch-to}")
    private int blockIntervalSwitchTo;

    private List<byte[]> initialProposers;

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
                return KeystoreAction.addressToPubkeyHash(uri.getRawUserInfo());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }).collect(Collectors.toList());


    }

    private long getPowWait(Block parent) {
        if (blockIntervalSwitchEra >= 0 && getEraAtBlockNumber(parent.nHeight + 1, getBlocksPerEra()) >= blockIntervalSwitchEra) {
            return blockIntervalSwitchTo * POW_WAIT_FACTOR;
        }
        return initialBlockInterval * POW_WAIT_FACTOR;
    }

    public List<byte[]> getProposers(Block parentBlock) {
        boolean enableMultiMiners = allowMinersJoinEra >= 0 &&
                getEraAtBlockNumber(parentBlock.nHeight + 1, this.getBlocksPerEra()) >= allowMinersJoinEra;

        if (!enableMultiMiners && parentBlock.nHeight >= 9235) {
            return initialProposers.subList(0, 1);
        }

        if (!enableMultiMiners) {
            return initialProposers;
        }

        List<byte[]> res;
        if (parentBlock.nHeight % getBlocksPerEra() == 0) {
            ProposersState state = getFromCache(parentBlock);
            res = state.getProposers().stream().map(p -> HexBytes.decode(p.publicKeyHash)).collect(Collectors.toList());
        } else {
            ProposersState state = getInstance(parentBlock);
            res = state.getProposers().stream().map(p -> HexBytes.decode(p.publicKeyHash)).collect(Collectors.toList());
        }
        if (parentBlock.getnHeight() + 1 < ProposersState.COMMUNITY_MINER_JOINS_HEIGHT) {
            res = res.stream().filter(WHITE_LIST::contains).collect(Collectors.toList());
        }
        if (res.size() > 0) {
            return res;
        }
        return initialProposers;

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
        byte[] lastValidator =
                        parentBlock.body.get(0).to
                ;
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
}

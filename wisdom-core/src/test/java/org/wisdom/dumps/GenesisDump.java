package org.wisdom.dumps;

import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.tdf.common.util.ByteArraySet;
import org.tdf.rlp.RLPElement;
import org.tdf.rlp.RLPItem;
import org.tdf.rlp.RLPList;
import org.wisdom.consensus.pow.ValidatorState;
import org.wisdom.context.BlockStreamBuilder;
import org.wisdom.core.Block;
import org.wisdom.core.WisdomBlockChain;
import org.wisdom.core.account.AccountDB;
import org.wisdom.db.AccountState;
import org.wisdom.db.CandidateStateTrie;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

// genesis =
// block + accounts + Map<byte[], long> validator states + Map<byte[], Candidate> candidates
@AllArgsConstructor
public class GenesisDump {
    private String genesisDirectory;

    private JdbcTemplate jdbcTemplate;

    private ValidatorState validatorState;

    private CandidateStateTrie candidateStateTrie;

    private long genesisDumpHeight;

    private BlockStreamBuilder blockStreamBuilder;

    private AccountDB accountDB;

    private WisdomBlockChain wisdomBlockChain;

    public void dump() throws Exception{
        List<AccountState> all = getAllPublicKeyHashes()
                .stream()
                .map(k -> accountDB.getAccounstate(k, genesisDumpHeight))
                .peek(Objects::requireNonNull)
                .collect(Collectors.toList());

        Path path =
                Paths.get(genesisDirectory,
                        String.format("genesis.%d.rlp", genesisDumpHeight)
                );

        Block block = Objects.requireNonNull(wisdomBlockChain.getCanonicalBlock(genesisDumpHeight));
        RLPElement newGenesisAccounts = RLPElement.readRLPTree(all);
        RLPElement newGenesisData = RLPList.createEmpty(2);
        newGenesisData.add(RLPElement.readRLPTree(block));
        newGenesisData.add(newGenesisAccounts);

        Files.write(path, newGenesisData.getEncoded(),
                StandardOpenOption.SYNC, StandardOpenOption.CREATE, StandardOpenOption.WRITE);
    }

    public Set<byte[]> getAllPublicKeyHashes(){
        Set<byte[]> expected = new ByteArraySet();
        List<byte[]> list;
        try {
            String sql = "select pubkeyhash from account a where a.blockheight<=?";
            list = jdbcTemplate.queryForList(sql, new Object[]{genesisDumpHeight}, byte[].class);
            expected.addAll(list);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        try {
            String sql = "select pubkeyhash from incubator_state  where height<=?";
            list = jdbcTemplate.queryForList(sql, new Object[]{genesisDumpHeight}, byte[].class);
            expected.addAll(list);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        try {
            String sql = "select share_pubkeyhash from incubator_state  where height<=? and not share_pubkeyhash is null";
            list = jdbcTemplate.queryForList(sql, new Object[]{genesisDumpHeight}, byte[].class);
            expected.addAll(list);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return expected;
    }
}

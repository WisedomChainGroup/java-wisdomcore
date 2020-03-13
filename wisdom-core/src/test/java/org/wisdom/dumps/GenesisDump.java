package org.wisdom.dumps;

import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.tdf.common.util.ByteArrayMap;
import org.tdf.common.util.ByteArraySet;
import org.tdf.rlp.RLPElement;
import org.tdf.rlp.RLPList;
import org.wisdom.consensus.pow.ProposersState;
import org.wisdom.consensus.pow.ValidatorState;
import org.wisdom.context.BlockStreamBuilder;
import org.wisdom.contract.AssetCodeInfo;
import org.wisdom.core.Block;
import org.wisdom.core.WisdomBlockChain;
import org.wisdom.core.account.AccountDB;
import org.wisdom.db.AccountState;
import org.wisdom.db.Candidate;
import org.wisdom.db.CandidateStateTrie;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

// genesis =
// block + accounts + Map<byte[], long> validator states + Map<byte[], Candidate> candidates
@AllArgsConstructor
public class GenesisDump {
    private String genesisDirectory;

    private JdbcTemplate jdbcTemplate;

    private CandidateStateTrie candidateStateTrie;

    private long genesisDumpHeight;

    private BlockStreamBuilder blockStreamBuilder;

    private AccountDB accountDB;

    private WisdomBlockChain wisdomBlockChain;

    private int blocksPerEra;


    public void dump() throws Exception {
        System.out.println("=============================================1");
        List<AccountState> all = getAllPublicKeyHashes()
                .stream()
                .map(k -> accountDB.getAccounstate(k, genesisDumpHeight))
                .peek(Objects::requireNonNull)
                .collect(Collectors.toList());

        Path path =
                Paths.get(genesisDirectory,
                        String.format("genesis.%d.rlp", genesisDumpHeight)
                );
        Block block = Objects.requireNonNull(wisdomBlockChain.getBlockByHeight(genesisDumpHeight));
        System.out.println("=============================================2");
        RLPElement newGenesisAccounts = RLPElement.readRLPTree(all);
        Map<byte[], Long> miners = getValidators();
        RLPElement validators = RLPElement.readRLPTree(miners);
        System.out.println(miners.toString());
        RLPElement newGenesisData = RLPList.createEmpty(5);
        newGenesisData.add(RLPElement.readRLPTree(block));
        newGenesisData.add(newGenesisAccounts);
        newGenesisData.add(validators);
        System.out.println("=============================================3");
        RLPElement candidates = RLPElement.readRLPTree(getCandidates());
        newGenesisData.add(candidates);
        Map<byte[], AssetCodeInfo> assetCodeInfoMap = new ByteArrayMap<>();
        newGenesisData.add(RLPElement.readRLPTree(assetCodeInfoMap));
        Files.write(path, newGenesisData.getEncoded(),
                StandardOpenOption.SYNC, StandardOpenOption.CREATE, StandardOpenOption.WRITE);
    }

    static class Validator {
        byte[] to;
        long count;
    }

    private ProposersState genesisProposersState;

    static class ValidatorMapper implements RowMapper<Validator> {

        @Override
        public Validator mapRow(ResultSet rs, int rowNum) throws SQLException {
            Validator validator = new Validator();
            validator.to = rs.getBytes("to");
            validator.count = rs.getLong("count");
            return validator;
        }
    }

    private Map<byte[], Candidate> getCandidates() {
        List<Block> era = new ArrayList<>(blocksPerEra);
        blockStreamBuilder.getBlocks()
                .filter(block -> block.getnHeight() <= genesisDumpHeight)
                .forEach(b -> {
                    if (b.nHeight == 0) return;
                    era.add(b);
                    if (era.size() < blocksPerEra) return;
                    System.out.println(b.nHeight);
                    candidateStateTrie.commit(era);
                    genesisProposersState.updateBlocks(era);

                    final long nextEra = (b.nHeight - 1) / blocksPerEra + 1;
                    candidateStateTrie.getTrie()
                            .revert(candidateStateTrie.getRootStore().get(b.getHash()).get())
                            .values()
                            .forEach(c -> {
                                if (c.getAccumulated(nextEra) !=
                                        genesisProposersState.getAll()
                                                .get(c.getPublicKeyHash().toHex()).getAccumulated()) {
                                    throw new RuntimeException("assertion failed");
                                }
                            });
                    era.clear();
                });
        Block block = Objects.requireNonNull(wisdomBlockChain.getBlockByHeight(genesisDumpHeight));
        return candidateStateTrie.getTrieByBlockHash(block.getHash()).asMap();
    }

    private Map<byte[], Long> getValidators() {
        Map<byte[], Long> map = new ByteArrayMap<>();
        String sql = "SELECT t.\"to\", count(t.\"to\") from \"transaction\" as t inner join \"transaction_index\" as ti on t.tx_hash = ti.tx_hash inner join \"header\" as h on h.block_hash = ti.block_hash and h.height <=? and h.height > 0  WHERE t.\"type\" = 0  GROUP BY t.\"to\"";
        List<Validator> list;
        list = jdbcTemplate.query(sql, new Object[]{genesisDumpHeight}, new ValidatorMapper());
        list.forEach(x -> map.put(x.to, x.count));
        return map;
    }


    public Set<byte[]> getAllPublicKeyHashes() {
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

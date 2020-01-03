package org.wisdom.dumps;

import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.wisdom.consensus.pow.ValidatorState;
import org.wisdom.context.BlockStreamBuilder;
import org.wisdom.db.CandidateStateTrie;

// genesis =
// block + accounts + Map<byte[], long> validator states + Map<byte[], Candidate> candidates
@AllArgsConstructor
public class GenesisDump {
    private String genesisDirectory;

    private JdbcTemplate jdbcTemplate;

    private ValidatorState validatorState;

    private CandidateStateTrie candidateStateTrie;

    private int genesisDumpHeight;

    private BlockStreamBuilder blockStreamBuilder;
}

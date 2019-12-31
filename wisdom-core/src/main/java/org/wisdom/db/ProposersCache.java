package org.wisdom.db;


import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.tdf.common.util.HexBytes;

import java.util.List;

@Component
public class ProposersCache {
    private Cache<HexBytes, List<Candidate>> proposers;

    @Autowired
    private CandidateStateTrie candidateStateTrie;

    public List<Candidate> getProposers(byte[] eraLastBlock){

    }

    public ProposersCache() {
        this.proposers = CacheBuilder.newBuilder()
                .maximumSize(8).build();
    }


    @Override
    public String toString() {
        return "ProposerState{" +
                "proposers=" + proposers +
                '}';
    }
}

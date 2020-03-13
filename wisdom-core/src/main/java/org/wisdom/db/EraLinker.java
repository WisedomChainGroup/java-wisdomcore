package org.wisdom.db;

import lombok.Getter;
import lombok.Setter;
import org.wisdom.core.Block;

public class EraLinker {
    @Setter
    private WisdomRepository repository;

    @Getter
    private int blocksPerEra;

    public EraLinker(int blocksPerEra){
        this.blocksPerEra = blocksPerEra;
    }

    public long getEraAtBlockNumber(long number) {
        return (number - 1) / blocksPerEra;
    }

    public Block getPrevEraLast(Block target) {
        if (target.nHeight == 0) {
            throw new RuntimeException("cannot find prev era last of genesis");
        }
        long lastHeaderNumber = getEraAtBlockNumber(target.nHeight) * blocksPerEra;
        if (lastHeaderNumber == target.nHeight - 1) {
            return repository.getHeaderByHash(target.hashPrevBlock);
        }
        return repository.getAncestorHeader(target.hashPrevBlock, lastHeaderNumber);
    }
}

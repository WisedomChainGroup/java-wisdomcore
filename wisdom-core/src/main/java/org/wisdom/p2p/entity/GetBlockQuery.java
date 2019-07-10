package org.wisdom.p2p.entity;

public class GetBlockQuery {
    public long start;
    public long stop;

    public GetBlockQuery clip(int maxSize, boolean clipFromStop){
        // clip interval
        start = start <= 0 ? 1 : start;
        stop = stop <= 0 ? (start + maxSize - 1) : stop;

        if(stop < start){
            stop = start;
        }

        // clip interval when overflow
        boolean isOverFlow = stop - start + 1 > maxSize;
        if (isOverFlow && clipFromStop) {
            start = stop - maxSize + 1;
        }
        if (isOverFlow && !clipFromStop) {
            stop = start + maxSize - 1;
        }
        return this;
    }

    public GetBlockQuery(long start, long stop) {
        this.start = start;
        this.stop = stop;
    }
}

/*
 * Copyright (c) [2018]
 * This file is part of the java-wisdomcore
 *
 * The java-wisdomcore is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The java-wisdomcore is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with the java-wisdomcore. If not, see <http://www.gnu.org/licenses/>.
 */
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

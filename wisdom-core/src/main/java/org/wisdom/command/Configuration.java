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

package org.wisdom.command;

import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Setter
public class Configuration {

    @Value("${transaction.day.count}")
    private int day_count;

    @Value("${min.procedurefee}")
    private long min_procedurefee;

    public void setMin_procedurefee(long min_procedurefee) {
        this.min_procedurefee = min_procedurefee;
    }

    @Value("${pool.clear.days}")
    private long poolcleardays;

    @Value("${transaction.nonce}")
    private long maxnonce;

    @Value("${pool.queued.maxcount}")
    private long maxqueued;

    @Value("${pool.pending.maxcount}")
    private long maxpending;

    @Value("${pool.queuedtopending.maxcount}")
    private long maxqpcount;

    @Value("${wisdom.block-interval-switch-era}")
    private int era;

    public long getPoolcleardays() {
        return poolcleardays;
    }

    public long getMin_procedurefee() {
        return min_procedurefee;
    }

    public int getDay_count(long height) {
        if(era>=0){
            long updateheight=era*120;
            if(height>updateheight){
                return day_count*3;
            }
        }
        return day_count;
    }

    public long getMaxnonce() {
        return maxnonce;
    }

    public long getMaxqueued() {
        return maxqueued;
    }

    public long getMaxpending() {
        return maxpending;
    }

    public long getMaxqpcount() {
        return maxqpcount;
    }

    public void setMaxqueued(long maxqueued) {
        this.maxqueued = maxqueued;
    }

    public void setMaxpending(long maxpending) {
        this.maxpending = maxpending;
    }
}

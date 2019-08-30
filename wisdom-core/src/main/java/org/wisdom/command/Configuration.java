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

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class Configuration {

    @Value("${transaction.gas.wdc}")
    private long gas_wds;

    @Value("${transaction.gas.deposit}")
    private long gas_deposit;

    @Value("${transaction.gas.hatch}")
    private long gas_hatch;

    @Value("${transaction.day.count}")
    private int day_count;

    @Value("${min.procedurefee}")
    private long min_procedurefee;

    public void setMin_procedurefee(long min_procedurefee) {
        this.min_procedurefee = min_procedurefee;
    }

    @Value("${pool.clear.days}")
    private long poolcleardays;

    public long getPoolcleardays() {
        return poolcleardays;
    }

    public long getMin_procedurefee() {
        return min_procedurefee;
    }

    public long getGas_wds() {
        return gas_wds;
    }

    public long getGas_deposit() {
        return gas_deposit;
    }

    public long getGas_hatch() {
        return gas_hatch;
    }

    public int getDay_count() {
        return day_count;
    }
}
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

package org.wisdom.consensus.pow;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.wisdom.core.state.EraLinkedStateFactory;
import org.wisdom.genesis.Genesis;

import javax.annotation.PostConstruct;

// comment
@Component
@Setter
@Slf4j(topic = "经济模型")
public class EconomicModel {
    public static final long WDC = 100000000;

    public static final long INITIAL_SUPPLY = 20 * WDC;

    public static final long HALF_PERIOD = 1051200 * 2;

    @Value("${wisdom.consensus.block-interval}")
    private int blockInterval;

    @Value("${wisdom.block-interval-switch-era}")
    private long blockIntervalSwitchEra;

    @Value("${wisdom.block-interval-switch-to}")
    private int blockIntervalSwitchTo;

    @Value("${wisdom.consensus.blocks-per-era}")
    private int blocksPerEra;

    @Autowired
    private Genesis genesis;

    @Getter
    private long total;

    public EconomicModel(@Value("${wisdom.consensus.block-interval}") int blockInterval,  @Value("${wisdom.block-interval-switch-era}") long blockIntervalSwitchEra, @Value("${wisdom.block-interval-switch-to}") int blockIntervalSwitchTo,     @Value("${wisdom.consensus.blocks-per-era}")
            int blocksPerEra) {
        this.blockInterval = blockInterval;
        this.blockIntervalSwitchEra = blockIntervalSwitchEra;
        this.blockIntervalSwitchTo = blockIntervalSwitchTo;
        this.blocksPerEra = blocksPerEra;
    }

    @PostConstruct
    public void init() {
        for (Genesis.InitAmount amount : genesis.alloc.initAmount) {
            total += amount.balance.longValue() * WDC;
        }
        total += getTotalSupply();

        log.info("total supply is {} WDC", total * 1.0 / WDC);
    }

    public long getConsensusRewardAtHeight(long height) {
        long reward = INITIAL_SUPPLY;
        long era = height / HALF_PERIOD;
        for (long i = 0; i < era; i++) {
            reward = reward * 52218182 / 100000000;
        }
        if (blockIntervalSwitchEra >= 0 && EraLinkedStateFactory.getEraAtBlockNumber(height, blocksPerEra) >= blockIntervalSwitchEra) {
            return reward * blockIntervalSwitchTo / blockInterval;
        }
        return reward;
    }

    public long getConsensusRewardAtHeight1(long height) {
        long era = (height > 5736000) ? ((height - 5736000) / 6307200 + 1) : (height / 6307200);
        long reward = INITIAL_SUPPLY;
        for (long i = 0; i < era; i++) {
            reward = reward * 52218182 / 100000000;
        }
        if (blockIntervalSwitchEra >= 0 && EraLinkedStateFactory.getEraAtBlockNumber(height, blocksPerEra) >= blockIntervalSwitchEra) {
            return reward * blockIntervalSwitchTo / blockInterval;
        }
        return reward;
    }

    public static void printRewardPerEra() {
        for (long reward = INITIAL_SUPPLY; reward > 0; reward = reward * 52218182 / 100000000) {
            System.out.println(reward * 1.0 / WDC);
        }
    }

    // 9140868887284800
    public long getTotalSupply() {
        long totalSupply = 0;
        for (long i = 0; ; i++) {
            long reward = getConsensusRewardAtHeight1(i);
            if (reward == 0) {
                System.out.println(totalSupply);
                return totalSupply;
            }
            totalSupply += reward;
        }
    }


    public static void main(String[] args) {
        EconomicModel model = new EconomicModel(30, 2380, 10, 120);
        for (long i = 0; i < 2000000; i++) {
            if (model.getConsensusRewardAtHeight(i) != model.getConsensusRewardAtHeight1(i)) {
                System.out.println("==========================");
            }
        }
        System.out.println(model.getTotalSupply());
    }
}
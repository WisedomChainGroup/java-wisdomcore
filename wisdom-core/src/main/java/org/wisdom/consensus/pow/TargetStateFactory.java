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

import org.wisdom.core.state.EraLinkedStateFactory;
import org.wisdom.core.WisdomBlockChain;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * @author sal 1564319846@qq.com
 * adjust difficulty per era
 */
@Component
public class TargetStateFactory extends EraLinkedStateFactory<TargetState> {

    private static final int CACHE_SIZE = 20;

    @Autowired
    public TargetStateFactory(WisdomBlockChain blockChain, TargetState genesisState, @Value("${wisdom.consensus.blocks-per-era}") int blocksPerRea) {
        super(blockChain, CACHE_SIZE, genesisState, blocksPerRea);
    }

    @PostConstruct
    public void init(){
        super.initCache();
    }
}

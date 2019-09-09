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

package org.wisdom.core.validate;

import org.wisdom.core.Block;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
public class CompositeBlockRule implements BlockRule {

    private List<BlockRule> rulers;

    @Autowired
    private BasicRule basicRule;

    @Autowired
    private AddressRule addressRule;

    @Autowired
    private CoinbaseRule coinbaseRule;

    @Autowired
    private ConsensusRule consensusRule;

    @Autowired
    private AccountRule accountRule;

    @Autowired
    private SignatureRule signatureRule;

    @Autowired
    private MerkleRule merkleRule;

    public void addRule(BlockRule... rules) {
        Collections.addAll(rulers, rules);
    }

    @Override
    public Result validateBlock(Block block) {
        for (BlockRule r : rulers) {
            Result res = r.validateBlock(block);
            if (!res.isSuccess()) {
                return res;
            }
        }
        return Result.SUCCESS;
    }

    public CompositeBlockRule() {
        rulers = new ArrayList<>();
    }

    @PostConstruct
    public void init() {
        addRule(basicRule, addressRule, coinbaseRule, consensusRule, signatureRule, accountRule, merkleRule);
    }
}
package org.wisdom.core.validate;
import org.wisdom.core.Block;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
public class CompositeBlockRule implements BlockRule{

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

    public void addRule(BlockRule... rules){
        Collections.addAll(rulers, rules);
    }

    @Override
    public Result validateBlock(Block block) {
        for(BlockRule r: rulers){
            Result res = r.validateBlock(block);
            if (!res.isSuccess()){
                return res;
            }
        }
        return Result.SUCCESS;
    }

    public CompositeBlockRule(){
        rulers = new ArrayList<>();
    }

    @PostConstruct
    public void init(){
        addRule(basicRule, addressRule, coinbaseRule, consensusRule, signatureRule, accountRule);
    }
}

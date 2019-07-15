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

package org.wisdom.Controller;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.wisdom.ApiResult.APIResult;
import org.wisdom.encoding.JSONEncodeDecoder;
import org.wisdom.service.CommandService;
import org.wisdom.core.Block;
import org.wisdom.core.WisdomBlockChain;
import org.wisdom.core.account.AccountDB;
import org.wisdom.core.account.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

@RestController
public class CommandController {
    private static Logger logger = LoggerFactory.getLogger(CommandController.class);

    @Value("${wisdom.consensus.enable-mining}")
    boolean enableMining;

    @Autowired
    CommandService commandService;

    @Autowired
    WisdomBlockChain bc;

    @Autowired
    JSONEncodeDecoder encodeDecoder;

    @Autowired
    AccountDB accountDB;

    @Autowired
    Block genesis;

    @PostMapping(value = {"/sendTransaction", "/sendIncubator", "/sendInterest",
            "/sendShare", "/sendDeposit", "/sendCost"})
    public Object sendTransaction(@RequestParam(value = "traninfo", required = true) String traninfo) {
        if (!enableMining) {
            return ConsensusResult.ERROR("this node cannot process transaction");
        }
        try {
            byte[] traninfos = Hex.decodeHex(traninfo.toCharArray());
            return commandService.verifyTransfer(traninfos);
        } catch (DecoderException e) {
            APIResult apiResult = new APIResult();
            apiResult.setCode(5000);
            apiResult.setMessage("Error");
            return apiResult;
        }
    }

    @RequestMapping(value="/getTransactionHeight",method = RequestMethod.POST)
    public Object getTransactionHeight(@RequestParam("height") int height,String type){
        try {
            if(type==null || type==""){//默认转账事务
                return commandService.getTransactionList(height,1);
            }else{//全部事务
                int types=Integer.valueOf(type);
                return commandService.getTransactionList(height,types);
            }
        } catch (Exception e) {
            APIResult apiResult = new APIResult();
            apiResult.setCode(5000);
            apiResult.setMessage("Error");
            return apiResult;
        }
    }

    @RequestMapping(value="/getTransactionBlcok",method = RequestMethod.POST)
    public Object getTransactionBlcok(@RequestParam("blockhash") String blockhash,String type){
        try {
            byte[] block_hash=Hex.decodeHex(blockhash.toCharArray());
            if(type==null || type==""){//默认转账事务
                return commandService.getTransactionBlcok(block_hash,1);
            }else{
                int types=Integer.valueOf(type);
                return commandService.getTransactionBlcok(block_hash,types);
            }
        } catch (DecoderException e) {
            APIResult apiResult = new APIResult();
            apiResult.setCode(5000);
            apiResult.setMessage("Error");
            return apiResult;
        }
    }


    @RequestMapping(method = RequestMethod.GET, value = "/block/{id}", produces = "application/json")
    public Object getBlock(@PathVariable("id") String id) {
        Block b;
        try {
            int height = Integer.parseInt(id);
            if (height < 0) {
                b = bc.currentBlock();
            } else {
                b = bc.getCanonicalBlock(height);
            }
            if (b == null) {
                return ConsensusResult.ERROR("cannot find block at height = " + height);
            }
            if(b.nHeight == 0){
                b = b.toHeader();
            }
            return encodeDecoder.encode(b);
        } catch (Exception e) {
            return handleGetBlockByHash(id);
        }
    }

    private Object handleGetBlockByHash(String hash) {
        try {
            byte[] h = Hex.decodeHex(hash);
            Block b = bc.getBlock(h);
            if (b != null) {
                return encodeDecoder.encodeBlock(b);
            }
            return ConsensusResult.ERROR("cannot find transaction where hash = " + hash);
        } catch (Exception e) {
            return ConsensusResult.ERROR("invalid hex " + hash);
        }
    }

    @RequestMapping(method = RequestMethod.GET, value = "/transaction/{txHash}", produces = "application/json")
    public Object getTransactionByHash(@PathVariable("txHash") String hash) {
        try {
            byte[] h = Hex.decodeHex(hash.toCharArray());
            Transaction tx = bc.getTransaction(h);
            if (tx != null) {
                return encodeDecoder.encodeTransaction(tx);
            }
        } catch (Exception e) {
            return ConsensusResult.ERROR("invalid transaction hash hex string " + hash);
        }
        return ConsensusResult.ERROR("the transaction " + hash + " not exists");
    }

    @RequestMapping(method = RequestMethod.GET, value = "/transaction", produces = "application/json")
    public Object getTransactionByTo(@RequestParam("to") String hash) {
        try {
            byte[] h = Hex.decodeHex(hash.toCharArray());
            Transaction tx = bc.getTransactionByTo(h);
            if (tx != null) {
                return encodeDecoder.encodeTransaction(tx);
            }
        } catch (Exception e) {
            return ConsensusResult.ERROR("invalid pubkey hex string " + hash);
        }
        return ConsensusResult.ERROR("the transaction where to = " + hash + " not exists");
    }

}

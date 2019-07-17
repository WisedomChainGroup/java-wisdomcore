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

import org.apache.commons.codec.binary.Hex;
import org.wisdom.ApiResult.APIResult;
import org.wisdom.keystore.wallet.KeystoreAction;
import org.wisdom.service.HatchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class HatchController {

    @Autowired
    HatchService hatchService;

    @RequestMapping(value="/sendBalance",method = RequestMethod.POST)
    public Object sendBalance(@RequestParam("pubkeyhash") String pubkeyhash){
        return hatchService.getBalance(pubkeyhash);
    }

    @RequestMapping(value="/getAddressBalance",method = RequestMethod.POST)
    public Object getAddressBalance(@RequestParam("address") String address){
        try{
            byte[] pubhash=KeystoreAction.addressToPubkeyHash(address);
            String pubkeyhash= Hex.encodeHexString(pubhash);
            return hatchService.getBalance(pubkeyhash);
        }catch (Exception e){
            return APIResult.newFailResult(5000,"ERROR");
        }

    }


    @RequestMapping(value="/sendNonce",method =RequestMethod.POST )
    public Object sendNonce(@RequestParam("pubkeyhash") String pubkeyhash){
        return hatchService.getNonce(pubkeyhash);
    }

    @RequestMapping(value="/WisdomCore/getNowInterest",method = RequestMethod.POST)
    public Object getNowInterest(@RequestParam("coinHash") String coinHash){
        return hatchService.getNowInterest(coinHash);
    }

    @GetMapping(value = "/WisdomCore/sendTransferList")
    public Object sendTransferList(@RequestParam("height") int height) {
        return hatchService.getTransfer(height);
    }

    @GetMapping(value ="/WisdomCore/sendHatchList")
    public Object sendHatchList(@RequestParam("height") int height){
        return hatchService.getHatch(height);
    }

    @GetMapping(value = "/WisdomCore/sendInterestList")
    public Object sendInterestList(@RequestParam("height") int height){
        return hatchService.getInterest(height);
    }

    @GetMapping(value = "/WisdomCore/sendShareList")
    public Object sendShareList(@RequestParam("height") int height){
        return hatchService.getShare(height);
    }

    @GetMapping(value = "/WisdomCore/sendCostList")
    public Object sendCostList(@RequestParam("height") int height){
        return hatchService.getCost(height);
    }
}
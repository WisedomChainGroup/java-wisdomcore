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

package org.wisdom.controller;

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

    @RequestMapping(value = "/sendBalance", method = RequestMethod.POST)
    public Object sendBalance(@RequestParam("pubkeyhash") String pubkeyhash) {
        return hatchService.getBalance(pubkeyhash);
    }

    @RequestMapping(value = "/getAddressBalance", method = RequestMethod.POST)
    public Object getAddressBalance(@RequestParam("address") String address) {
        try {
            byte[] pubhash = KeystoreAction.addressToPubkeyHash(address);
            String pubkeyhash = Hex.encodeHexString(pubhash);
            return hatchService.getBalance(pubkeyhash);
        } catch (Exception e) {
            return APIResult.newFailResult(5000, "ERROR");
        }

    }

    @RequestMapping(value = "/getTxrecordFromAddress", method = RequestMethod.GET)
    @Deprecated
    public Object getTxrecordFromAddress(@RequestParam("address") String address) {
        return APIResult.newFailResult(5000, "The interface has been deprecated");
    }

    @RequestMapping(value = "/sendNonce", method = RequestMethod.POST)
    public Object sendNonce(@RequestParam("pubkeyhash") String pubkeyhash) {
        return hatchService.getNonce(pubkeyhash);
    }

    @RequestMapping(value = "/WisdomCore/getNowInterest", method = RequestMethod.POST)
    public Object getNowInterest(@RequestParam("coinHash") String coinHash) {
        return hatchService.getNowInterest(coinHash);
    }

    @RequestMapping(value = "/WisdomCore/getNowShare", method = RequestMethod.POST)
    public Object getNowShare(@RequestParam("coinHash") String coinHash) {
        return hatchService.getNowShare(coinHash);
    }

    @GetMapping(value = "/WisdomCore/sendTransferList")
    public Object sendTransferList(@RequestParam("height") long height) {
        return hatchService.getTransfer(height);
    }

    @GetMapping(value = "/WisdomCore/sendHatchList")
    public Object sendHatchList(@RequestParam("height") long height) {
        return hatchService.getHatch(height);
    }

    @GetMapping(value = "/WisdomCore/sendInterestList")
    public Object sendInterestList(@RequestParam("height") long height) {
        return hatchService.getInterest(height);
    }

    @GetMapping(value = "/WisdomCore/sendShareList")
    public Object sendShareList(@RequestParam("height") long height) {
        return hatchService.getShare(height);
    }

    @GetMapping(value = "/WisdomCore/sendCostList")
    public Object sendCostList(@RequestParam("height") long height) {
        return hatchService.getCost(height);
    }

    @GetMapping(value = "/WisdomCore/sendVoteList")
    public Object sendVoteList(@RequestParam("height") long height) {
        return hatchService.getVote(height);
    }

    @GetMapping(value = "/WisdomCore/sendCancelVoteList")
    public Object sendCancelVoteList(@RequestParam("height") long height) {
        return hatchService.getCancelVote(height);
    }

    @GetMapping(value = "/WisdomCore/sendMortgageList")
    public Object sendMortgageList(@RequestParam("height") long height) {
        return hatchService.getMortgage(height);
    }

    @GetMapping(value = "/WisdomCore/sendCancelMortgageList")
    public Object sendCancelMortgageList(@RequestParam("height") long height) {
        return hatchService.getCancelMortgage(height);
    }

    @GetMapping(value = "/WisdomCore/sendCoinBase")
    public Object sendCoinBase(@RequestParam("height") long height) {
        return hatchService.getCoinBaseList(height);
    }

    @GetMapping(value = "/WisdomCore/sendAsset")
    public Object sendAsset(@RequestParam("height") long height) {
        return hatchService.getAssetList(height);
    }

    @GetMapping(value = "/WisdomCore/sendAssetTransfer")
    public Object sendAssetTransfer(@RequestParam("height") long height) {
        return hatchService.getAssetTransferList(height);
    }

    @GetMapping(value = "/WisdomCore/sendAssetOwner")
    public Object sendAssetOwner(@RequestParam("height") long height){
        return hatchService.getAssetOwnerList(height);
    }

    @GetMapping(value = "/WisdomCore/sendAssetIncreased")
    public Object sendAssetIncreased(@RequestParam("height") long height){
        return hatchService.getAssetIncreasedList(height);
    }
}
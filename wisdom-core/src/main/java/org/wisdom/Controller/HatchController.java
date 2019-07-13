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

import org.wisdom.service.Impl.HatchServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class HatchController {

    @Autowired
    HatchServiceImpl hatchService;

    @RequestMapping(value="/sendBalance",method = RequestMethod.POST)
    public Object sendBalance(@RequestParam("pubkeyhash") String pubkeyhash){
        return hatchService.getBalance(pubkeyhash);
    }

    @RequestMapping(value="/sendNonce",method =RequestMethod.POST )
    public Object sendNonce(@RequestParam("pubkeyhash") String pubkeyhash){
        return hatchService.getNonce(pubkeyhash);
    }

    @RequestMapping(value="/WisdomCore/getNowInterest",method = RequestMethod.POST)
    public Object getNowInterest(@RequestParam("tranhash") String tranhash){
        return hatchService.getNowInterest(tranhash);
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
}
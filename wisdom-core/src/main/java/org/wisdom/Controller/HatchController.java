package org.wisdom.Controller;

import org.wisdom.ApiResult.APIResult;
import org.wisdom.service.Impl.HatchServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class HatchController {

    @Autowired
    HatchServiceImpl hatchService;

    @RequestMapping(value="/sendBalance",method = RequestMethod.POST)
    public long sendBalance(@RequestParam("pubkeyhash") String pubkeyhash){
        return hatchService.getBalance(pubkeyhash);
    }

    @RequestMapping(value="/sendNonce",method =RequestMethod.POST )
    public long sendNonce(@RequestParam("pubkeyhash") String pubkeyhash){
        return hatchService.getNonce(pubkeyhash);
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

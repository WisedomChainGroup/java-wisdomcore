package org.wisdom.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.wisdom.ApiResult.APIResult;
import org.wisdom.command.Dllparameter;
import org.wisdom.encoding.JSONEncodeDecoder;
import org.wisdom.service.DllService;

@ConditionalOnProperty(name = "wisdom.dll.rpc", havingValue = "true")
@RestController
public class DllController {

    @Autowired
    JSONEncodeDecoder jsonEncodeDecoder;

    @Autowired
    DllService dllService;

    @RequestMapping(value="/dll",method = RequestMethod.POST,produces = "application/json;charset=UTF-8")
    public Object ddl(@RequestBody byte[] jsonParam){
        Dllparameter dllparameter= jsonEncodeDecoder.decode(jsonParam, Dllparameter.class);
        if(dllparameter==null || (dllparameter.getJarname()==null || dllparameter.getJarname().equals("")) ||
                (dllparameter.getClasspackage()==null || dllparameter.getJarname().equals("")) ||
                (dllparameter.getMethodname()==null || dllparameter.equals(""))){
            return APIResult.newFailResult(5000,"Parameter conversion error");
        }
        return dllService.CallMethod(dllparameter);
    }

}

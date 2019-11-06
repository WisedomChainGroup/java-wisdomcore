package org.wisdom.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ExplorerController {

    @GetMapping(value = "/WisdomCore/ExplorerInfo")
    public Object getExplorerInfo(){
        return null;
    }

}

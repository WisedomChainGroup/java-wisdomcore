package org.wisdom.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.wisdom.ApiResult.APIResult;
import org.wisdom.dao.HeaderDao;
import org.wisdom.entity.HeaderEntity;


@RestController
public class JpaController {

    @Autowired
    private HeaderDao headerDao;

    @GetMapping(value = {"/entity", "/"}, produces = "application/json")
    public Object getVersion() {
        HeaderEntity entity = headerDao.findByHeight(1L);
        return APIResult.newFailResult(2000, "SUCCESS", entity);
    }


}

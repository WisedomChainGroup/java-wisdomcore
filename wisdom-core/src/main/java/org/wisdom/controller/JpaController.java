package org.wisdom.controller;

import org.bouncycastle.util.encoders.Hex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.wisdom.ApiResult.APIResult;
import org.wisdom.core.Block;
import org.wisdom.dao.HeaderDao;
import org.wisdom.dao.TransactionDao;
import org.wisdom.dao.TransactionIndexDao;
import org.wisdom.entity.HeaderEntity;
import org.wisdom.entity.Mapping;
import org.wisdom.entity.TransactionEntity;
import org.wisdom.service.BlockRepositoryService;

import java.util.List;
import java.util.stream.Collectors;


@RestController
public class JpaController {

    @Autowired
    private HeaderDao headerDao;
    @Autowired
    private TransactionDao transactionDao;
    @Autowired
    private TransactionIndexDao transactionIndexDao;

    @GetMapping(value = {"/entity", "/"}, produces = "application/json")
    public Object getVersion() {
        TransactionEntity transactionEntity= transactionDao.findByToAndType( Hex.decode("6410ded6c0a7a9961c379fe3901c7c012abb0bfa"),10, PageRequest.of(0, 1)).stream().findFirst().get();
        return APIResult.newFailResult(2000, "SUCCESS", Mapping.getTransactionFromHeaderEntity(transactionEntity));
    }


}

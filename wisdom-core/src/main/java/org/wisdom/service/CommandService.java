package org.wisdom.service;

import org.wisdom.ApiResult.APIResult;

public interface CommandService {

    APIResult verifyTransfer(byte[] transfer);
}

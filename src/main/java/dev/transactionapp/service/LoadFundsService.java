package dev.transactionapp.service;

import dev.transactionapp.entity.LoadRequest;
import dev.transactionapp.entity.LoadResponse;

public interface LoadFundsService {

    LoadResponse loadFundsAndGetResponse(String messageId, LoadRequest loadRequest) throws RuntimeException;
}

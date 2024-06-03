package dev.codescreen.service;

import dev.codescreen.entity.LoadRequest;
import dev.codescreen.entity.LoadResponse;

public interface LoadFundsService {

    LoadResponse loadFundsAndGetResponse(String messageId, LoadRequest loadRequest) throws RuntimeException;
}

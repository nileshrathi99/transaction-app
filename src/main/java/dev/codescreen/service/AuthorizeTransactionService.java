package dev.codescreen.service;

import dev.codescreen.entity.AuthorizationRequest;
import dev.codescreen.entity.AuthorizationResponse;

public interface AuthorizeTransactionService {
    
     AuthorizationResponse authorizeTransactionAndGetResponse(String messageId, AuthorizationRequest authorizationRequest) throws RuntimeException;
}

package dev.transactionapp.service;

import dev.transactionapp.entity.AuthorizationRequest;
import dev.transactionapp.entity.AuthorizationResponse;

public interface AuthorizeTransactionService {
    
     AuthorizationResponse authorizeTransactionAndGetResponse(String messageId, AuthorizationRequest authorizationRequest) throws RuntimeException;
}

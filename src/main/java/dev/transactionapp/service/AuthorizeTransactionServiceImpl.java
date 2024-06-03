package dev.transactionapp.service;

import dev.transactionapp.entity.*;
import dev.transactionapp.enums.ResponseCode;
import dev.transactionapp.exception.*;
import dev.transactionapp.repository.AuthorizationResponseRepository;
import dev.transactionapp.repository.UserRepository;
import dev.transactionapp.validator.RequestsValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
public class AuthorizeTransactionServiceImpl implements AuthorizeTransactionService{

    @Autowired
    private AuthorizationResponseRepository authorizationResponseRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RequestsValidator requestsValidator;

    /**
     * Processes a request to authorize a transaction and returns a corresponding response.
     * Expects a DEBIT transaction type.
     * Here's a breakdown of the steps involved:
     *  1. Validates the `AuthorizationRequest` object using the `requestsValidator` to ensure data integrity.
     *  2. Retrieves user information from the repository to access their current balance.
     *  3. Extracts the transaction amount from the `authorizationRequest` object.
     *  4. **Checks user's balance for sufficiency:**
     *      - Verifies if the user has enough funds to cover the requested transaction amount.
     *  5. Prepares an `AuthorizationResponse` object based on the authorization decision (approved/denied).
     *  6. **Updates user's balance upon authorization (within a transaction):**
     *      - If the transaction is authorized, the user's balance is updated within a database transaction.
     *      - This ensures either the entire update happens (debiting the amount) or none at all, preventing inconsistencies.
     *  7. **Saves authorization response (failure scenario):**
     *      - If authorization fails, the `AuthorizationResponse` object is saved to the repository for record-keeping.
     *
     * @param messageId The unique identifier of the message received.
     * @param authorizationRequest The object containing user and transaction details for authorization.
     * @return The `AuthorizationResponse` object containing the authorization decision and user balance (if applicable).
     * @throws RuntimeException If any unexpected error occurs during processing.
     *
     * **Lock Mechanism:**
     * Pessimistic-Locking
     *  - Pessimistic locking is used to ensure exclusive access to user data during fund loading.
     *  - When calling `userRepository.findById(userId)` with `@Lock(LockModeType.PESSIMISTIC_WRITE)`, the database acquires a write lock on the user row identified by `userId`.
     *  - This lock prevents other threads from reading or updating the same user data until the current transaction releases the lock.
     *  - This guarantees that only one request can process fund authorization for a specific user at a time, preventing race conditions and maintaining data consistency.
     *
     * **Transactional Annotation:**
     *  - This annotation guarantees that updating the user's balance (if authorized) and sending the response are treated as a single unit.
     *  - If any step fails, the entire transaction is rolled back, preventing partial updates or inconsistencies.
     *  - This maintains data integrity by ensuring all changes happen together or none at all.
     */
    @Override
    @Transactional
    public AuthorizationResponse authorizeTransactionAndGetResponse(String messageId, AuthorizationRequest authorizationRequest) throws RuntimeException {
        log.debug("AuthorizeTransactionServiceImpl.authorizeTransactionAndGetResponse() initiated for messageId {}", messageId);
        requestsValidator.checkAuthorizationRequestValidity(messageId, authorizationRequest);
        AuthorizationResponse authorizationResponse = getResponse(authorizationRequest);
        log.debug("AuthorizeTransactionServiceImpl.authorizeTransactionAndGetResponse() completed for messageId {}", messageId);
        return authorizationResponse;
    }

    private AuthorizationResponse getResponse(AuthorizationRequest authorizationRequest) throws UserNotFoundException{
        User user = fetchUser(authorizationRequest.getUserId());
        double transactionAmount = parseTransactionAmount(authorizationRequest);
        double currentBalance = user.getBalance();
        boolean isAuthorized = isAuthorized(transactionAmount, currentBalance);
        AuthorizationResponse authorizationResponse = prepareResponse(authorizationRequest, currentBalance, transactionAmount, isAuthorized);
        if (isAuthorized) {
            log.info("User with ID: {} authorized for transaction of {} {}", user.getId(), transactionAmount, authorizationRequest.getTransactionAmount().getCurrency());
            updateUserBalance(user, transactionAmount);
        }
        else {
            log.info("User with ID: {} declined for transaction due to insufficient balance", user.getId());
            saveAuthorizationResponse(authorizationResponse);
        }
        return authorizationResponse;
    }

    private User fetchUser(String userId) throws UserNotFoundException{
        return userRepository.findById(UUID.fromString(userId))
                .orElseThrow(() -> new UserNotFoundException(String.format("User with ID %s not found", userId)));
    }

    private double parseTransactionAmount(AuthorizationRequest authorizationRequest) {
        return Double.parseDouble(authorizationRequest.getTransactionAmount().getAmount());
    }

    private boolean isAuthorized(double transactionAmount, double currentBalance) {
        return currentBalance >= transactionAmount;
    }

    private AuthorizationResponse prepareResponse(AuthorizationRequest authorizationRequest, double currentBalance, double transactionAmount, boolean isAuthorized) {
        Amount balance = new Amount(String.valueOf(isAuthorized ? currentBalance - transactionAmount : transactionAmount),
                authorizationRequest.getTransactionAmount().getCurrency(),
                authorizationRequest.getTransactionAmount().getDebitOrCredit());

        return new AuthorizationResponse(authorizationRequest.getMessageId(),
                authorizationRequest.getUserId(),
                isAuthorized ? ResponseCode.APPROVED.toString() : ResponseCode.DECLINED.toString(),
                balance);
    }

    private void updateUserBalance(User user, double transactionAmount) {
        double updatedBalance = user.getBalance() - transactionAmount;
        user.setBalance(updatedBalance);
        userRepository.save(user);
    }

    private void saveAuthorizationResponse(AuthorizationResponse authorizationResponse) {
        authorizationResponseRepository.save(authorizationResponse);
    }

}

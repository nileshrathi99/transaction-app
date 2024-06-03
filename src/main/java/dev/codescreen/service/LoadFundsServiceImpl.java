package dev.codescreen.service;

import dev.codescreen.entity.*;
import dev.codescreen.enums.DebitCredit;
import dev.codescreen.exception.*;
import dev.codescreen.repository.UserRepository;
import dev.codescreen.validator.RequestsValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
public class LoadFundsServiceImpl implements LoadFundsService{

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RequestsValidator requestsValidator;

    /**
     * Processes a request to load funds into a user's account and returns a response containing the updated balance.
     * Expects a CREDIT transaction type.
     * Here's a breakdown of the steps involved:
     *  1. Validates the `LoadRequest` object using the `requestsValidator` to ensure data integrity.
     *  2. Retrieves user information from the repository to access their current balance.
     *  3. Extracts the transaction amount from the `loadRequest` object.
     *  4. Updates the user's account balance with the loaded amount within a database transaction.
     *      - This ensures either the entire update happens or none at all, preventing partial changes.
     *  5. Prepares and returns a `LoadResponse` object containing the updated user balance information.
     *
     * @param messageId The unique identifier of the message received.
     * @param loadRequest The object containing user and transaction details for loading funds.
     * @return The `LoadResponse` object containing the user's updated balance after loading funds.
     * @throws RuntimeException If any unexpected error occurs during processing.
     *
     * **Lock Mechanism:**
     * Pessimistic-Locking
     *  - Pessimistic locking is used to ensure exclusive access to user data during fund loading.
     *  - When calling `userRepository.findById(userId)` with `@Lock(LockModeType.PESSIMISTIC_WRITE)`, the database acquires a write lock on the user row identified by `userId`.
     *  - This lock prevents other threads from reading or updating the same user data until the current transaction releases the lock.
     *  - This guarantees that only one request can process fund loading for a specific user at a time, preventing race conditions and maintaining data consistency.
     *
     * **Transactional Annotation:**
     *  - This annotation guarantees that updating the user's balance in the database and sending the response are treated as a single unit.
     *  - If any step fails, the entire transaction is rolled back.
     */
    @Override
    @Transactional
    public LoadResponse loadFundsAndGetResponse(String messageId, LoadRequest loadRequest) throws RuntimeException {
        log.debug("LoadFundsServiceImpl.loadFundsAndGetResponse() initiated for messageId {}", messageId);
        requestsValidator.checkLoadRequestValidity(messageId, loadRequest);
        LoadResponse loadResponse = getResponse(loadRequest);
        log.debug("LoadFundsServiceImpl.loadFundsAndGetResponse() completed for messageId {}", messageId);
        return loadResponse;
    }

    private LoadResponse getResponse(LoadRequest loadRequest) {
        User user = fetchUser(loadRequest.getUserId());
        double transactionAmount = parseTransactionAmount(loadRequest);
        double updatedBalance = updateUserBalance(user, transactionAmount);
        log.info("User with ID: {} successfully loaded funds. New balance: {} {}", user.getId(), updatedBalance, loadRequest.getTransactionAmount().getCurrency());
        return prepareLoadResponse(loadRequest, updatedBalance);
    }

    private User fetchUser(String userId) throws UserNotFoundException{
        return userRepository.findById(UUID.fromString(userId))
                .orElseThrow(() -> new UserNotFoundException(String.format("User with ID %s not found", userId)));
    }

    private double parseTransactionAmount(LoadRequest loadRequest) {
        return Double.parseDouble(loadRequest.getTransactionAmount().getAmount());
    }

    private double updateUserBalance(User user, double transactionAmount) {
        double updatedBalance = user.getBalance() + transactionAmount;
        user.setBalance(updatedBalance);
        userRepository.save(user);
        return updatedBalance;
    }

    private LoadResponse prepareLoadResponse(LoadRequest loadRequest, double updatedBalance) {
        return new LoadResponse(loadRequest.getUserId(),
                loadRequest.getMessageId(),
                new Amount(String.valueOf(updatedBalance),
                        loadRequest.getTransactionAmount().getCurrency(),
                        DebitCredit.CREDIT.toString()));
    }

}

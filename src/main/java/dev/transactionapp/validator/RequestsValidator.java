package dev.transactionapp.validator;

import dev.transactionapp.entity.AuthorizationRequest;
import dev.transactionapp.entity.LoadRequest;
import dev.transactionapp.entity.User;
import dev.transactionapp.enums.DebitCredit;
import dev.transactionapp.exception.*;
import dev.transactionapp.repository.AuthorizationResponseRepository;
import dev.transactionapp.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
public class RequestsValidator {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthorizationResponseRepository authorizationResponseRepository;

    /**
     * Validates the Authorization Request object.
     * This method performs the following checks:
     *  - Checks if the messageId in the path variable matches the messageId in the request body.
     *  - Checks if the messageId is unique (not already used).
     *  - Checks if the transaction type is 'DEBIT'.
     *  - Validates the user ID format (ensures it's a valid UUID).
     *  - Verifies if the user with the provided ID exists.
     *  - Ensures the user's currency matches the transaction currency.
     *
     * @param messageId The message ID from the path variable.
     * @param request The Authorization Request object to validate.
     * @throws RuntimeException if any validation check fails.
     */
    public void checkAuthorizationRequestValidity(String messageId, AuthorizationRequest request)
            throws RuntimeException{
        log.debug("RequestsValidator.checkAuthorizationRequestValidity() initiated for messageId {}", messageId);
        checkMessageIdMatch(messageId, request.getMessageId());
        checkUniqueMessageId(messageId);
        checkTransactionTypeMatch(request.getTransactionAmount().getDebitOrCredit(), DebitCredit.DEBIT.toString());
        checkValidUUID(request.getUserId());
        checkUserExists(request.getUserId());
        checkCurrencyMatch(request.getUserId(), request.getTransactionAmount().getCurrency());
        log.debug("RequestsValidator.checkAuthorizationRequestValidity() for messageId {} completed", messageId);
    }

    /**
     * Validates the Load Request object.
     * This method performs similar checks as `checkAuthorizationRequestValidity` but ensures the transaction type is 'CREDIT'.
     *
     * @param messageId The message ID from the path variable.
     * @param request The Load Request object to validate.
     * @throws RuntimeException if any validation check fails.
     */
    public void checkLoadRequestValidity(String messageId, LoadRequest request)
            throws RuntimeException{
        log.debug("RequestsValidator.checkLoadRequestValidity() initiated with messageId {}", messageId);
        checkMessageIdMatch(messageId, request.getMessageId());
        checkUniqueMessageId(messageId);
        checkTransactionTypeMatch(request.getTransactionAmount().getDebitOrCredit(), DebitCredit.CREDIT.toString());
        checkValidUUID(request.getUserId());
        checkUserExists(request.getUserId());
        checkCurrencyMatch(request.getUserId(), request.getTransactionAmount().getCurrency());
        log.debug("RequestsValidator.checkLoadRequestValidity() with messageId {} completed", messageId);
    }

    private void checkMessageIdMatch(String messageId, String requestBodyMessageId) throws MessageIdNotMatchException {
        if(!messageId.equals(requestBodyMessageId))
            throw new MessageIdNotMatchException(String.format("path variable message: %s doesn't match request body message: %s", messageId, requestBodyMessageId));
    }

    private void checkUniqueMessageId(String messageId) throws MessageIdAlreadyExistsException {
        if(authorizationResponseRepository.existsById(messageId))
            throw new MessageIdAlreadyExistsException(String.format("message id: %s already exists", messageId));
    }

    private void checkTransactionTypeMatch(String transactionType, String supportedTransactionType) throws InvalidTransactionTypeException {
        if(!transactionType.equals(supportedTransactionType))
            throw new InvalidTransactionTypeException(String.format("Supported transaction type: %s", supportedTransactionType));
    }

    private void checkValidUUID(String userId) throws InvalidUUIDException{
        try {
            UUID.fromString(userId);
        } catch (IllegalArgumentException ex){
            throw new InvalidUUIDException(String.format("userid provided: %s is not A valid UUID format", userId));
        }
    }

    private void checkUserExists(String userId) throws UserNotFoundException, CurrencyNotMatchException {
        if(!userRepository.existsById(UUID.fromString(userId)))
            throw new UserNotFoundException(String.format("User with ID: %s not found", userId));
    }

    private void checkCurrencyMatch(String userId, String transactionCurrency) {
        User user = userRepository.findById(UUID.fromString(userId)).orElseThrow(() -> new UserNotFoundException(String.format("User with ID %s not found", userId)));
        if(!user.getCurrency().equals(transactionCurrency))
            throw new CurrencyNotMatchException(String.format("User currency: %s doesn't match with request body currency: %s", user.getCurrency(), transactionCurrency));
    }

}

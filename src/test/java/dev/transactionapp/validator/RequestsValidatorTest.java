package dev.transactionapp.validator;

import dev.transactionapp.entity.*;
import dev.transactionapp.exception.*;
import dev.transactionapp.repository.AuthorizationResponseRepository;
import dev.transactionapp.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RequestsValidatorTest {

    @InjectMocks
    private RequestsValidator requestsValidator;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AuthorizationResponseRepository authorizationResponseRepository;

    private String dummyUserId;
    private String dummyMessageId;
    private User dummyUser;
    private Amount dummyTransactionAmount;
    private AuthorizationRequest dummyAuthorizationRequest;
    private LoadRequest dummyLoadRequest;


    @BeforeEach
    void setUp(){
        dummyMessageId = "someMessageId";
        dummyUserId = "f09752d7-c4e7-4491-98c8-36a9f5fc9f37"; // has to be UUID
        dummyUser = new User("USD", Double.parseDouble("200"));
        dummyTransactionAmount = new Amount("20", "USD", "DEBIT");
        dummyAuthorizationRequest = new AuthorizationRequest(dummyUserId, dummyMessageId, dummyTransactionAmount);
    }

    // Authorization Request

    @Test
    void authorizationRequestCheckCheckMessageIdMatchTest() {
        dummyTransactionAmount = new Amount("20", "USD", "DEBIT");
        dummyAuthorizationRequest = new AuthorizationRequest(dummyUserId, "some other messageId", dummyTransactionAmount);
        assertThrows(MessageIdNotMatchException.class, () -> requestsValidator.checkAuthorizationRequestValidity(dummyMessageId, dummyAuthorizationRequest));
    }

    @Test
    void authorizationRequestCheckCheckUniqueMessageIdTest() {
        dummyTransactionAmount = new Amount("20", "USD", "DEBIT");
        dummyAuthorizationRequest = new AuthorizationRequest(dummyUserId, dummyMessageId, dummyTransactionAmount);
        when(authorizationResponseRepository.existsById(ArgumentMatchers.anyString())).thenReturn(true);
        assertThrows(MessageIdAlreadyExistsException.class, () -> requestsValidator.checkAuthorizationRequestValidity(dummyMessageId, dummyAuthorizationRequest));
    }

    @Test
    void authorizationRequestCheckTransactionTypeMatchTest() {
        dummyTransactionAmount = new Amount("20", "USD", "CREDIT");
        dummyAuthorizationRequest = new AuthorizationRequest(dummyUserId, dummyMessageId, dummyTransactionAmount);
        when(authorizationResponseRepository.existsById(ArgumentMatchers.anyString())).thenReturn(false);
        assertThrows(InvalidTransactionTypeException.class, () -> requestsValidator.checkAuthorizationRequestValidity(dummyMessageId, dummyAuthorizationRequest));
    }

    @Test
    void authorizationRequestCheckValidUUIDTest() {
        dummyTransactionAmount = new Amount("20", "INR", "DEBIT");
        dummyAuthorizationRequest = new AuthorizationRequest("some invalid UUID", dummyMessageId, dummyTransactionAmount);
        when(authorizationResponseRepository.existsById(ArgumentMatchers.anyString())).thenReturn(false);
        assertThrows(InvalidUUIDException.class, () -> requestsValidator.checkAuthorizationRequestValidity(dummyMessageId, dummyAuthorizationRequest));
    }


    @Test
    void authorizationRequestCheckUserExistsTest() {
        dummyTransactionAmount = new Amount("20", "USD", "DEBIT");
        dummyAuthorizationRequest = new AuthorizationRequest(dummyUserId, dummyMessageId, dummyTransactionAmount);
        when(authorizationResponseRepository.existsById(ArgumentMatchers.anyString())).thenReturn(false);
        when(userRepository.existsById(UUID.fromString(dummyUserId))).thenReturn(false);
        assertThrows(UserNotFoundException.class, () -> requestsValidator.checkAuthorizationRequestValidity(dummyMessageId, dummyAuthorizationRequest));
    }

    @Test
    void authorizationRequestCheckCurrencyMatchTest() {
        dummyTransactionAmount = new Amount("20", "INR", "DEBIT");
        dummyAuthorizationRequest = new AuthorizationRequest(dummyUserId, dummyMessageId, dummyTransactionAmount);
        when(authorizationResponseRepository.existsById(ArgumentMatchers.anyString())).thenReturn(false);
        when(userRepository.existsById(UUID.fromString(dummyUserId))).thenReturn(true);
        when(userRepository.findById(UUID.fromString(dummyUserId))).thenReturn(java.util.Optional.ofNullable(dummyUser));
        assertThrows(CurrencyNotMatchException.class, () -> requestsValidator.checkAuthorizationRequestValidity(dummyMessageId, dummyAuthorizationRequest));
    }



    // Load Request

    @Test
    void loadRequestCheckCheckMessageIdMatchTest() {
        dummyTransactionAmount = new Amount("20", "USD", "CREDIT");
        dummyLoadRequest = new LoadRequest(dummyUserId, "some other messageId", dummyTransactionAmount);
        assertThrows(MessageIdNotMatchException.class, () -> requestsValidator.checkLoadRequestValidity(dummyMessageId, dummyLoadRequest));
    }

    @Test
    void loadRequestCheckCheckUniqueMessageIdTest() {
        dummyTransactionAmount = new Amount("20", "USD", "CREDIT");
        dummyLoadRequest = new LoadRequest(dummyUserId, dummyMessageId, dummyTransactionAmount);
        when(authorizationResponseRepository.existsById(ArgumentMatchers.anyString())).thenReturn(true);
        assertThrows(MessageIdAlreadyExistsException.class, () -> requestsValidator.checkLoadRequestValidity(dummyMessageId, dummyLoadRequest));
    }

    @Test
    void loadRequestCheckTransactionTypeMatchTest() {
        dummyTransactionAmount = new Amount("20", "USD", "DEBIT");
        dummyLoadRequest = new LoadRequest(dummyUserId, dummyMessageId, dummyTransactionAmount);
        when(authorizationResponseRepository.existsById(ArgumentMatchers.anyString())).thenReturn(false);
        assertThrows(InvalidTransactionTypeException.class, () -> requestsValidator.checkLoadRequestValidity(dummyMessageId, dummyLoadRequest));
    }

    @Test
    void loadRequestCheckValidUUIDMatchTest() {
        dummyTransactionAmount = new Amount("20", "INR", "CREDIT");
        dummyLoadRequest = new LoadRequest("some invalid UUID", dummyMessageId, dummyTransactionAmount);
        when(authorizationResponseRepository.existsById(ArgumentMatchers.anyString())).thenReturn(false);
        assertThrows(InvalidUUIDException.class, () -> requestsValidator.checkLoadRequestValidity(dummyMessageId, dummyLoadRequest));
    }

    @Test
    void loadRequestCheckUserExistsTest() {
        dummyTransactionAmount = new Amount("20", "USD", "CREDIT");
        dummyLoadRequest = new LoadRequest(dummyUserId, dummyMessageId, dummyTransactionAmount);
        when(authorizationResponseRepository.existsById(ArgumentMatchers.anyString())).thenReturn(false);
        when(userRepository.existsById(UUID.fromString(dummyUserId))).thenReturn(false);
        assertThrows(UserNotFoundException.class, () -> requestsValidator.checkLoadRequestValidity(dummyMessageId, dummyLoadRequest));
    }

    @Test
    void loadRequestCheckCurrencyMatchTest() {
        dummyTransactionAmount = new Amount("20", "INR", "CREDIT");
        dummyLoadRequest = new LoadRequest(dummyUserId, dummyMessageId, dummyTransactionAmount);
        when(authorizationResponseRepository.existsById(ArgumentMatchers.anyString())).thenReturn(false);
        when(userRepository.existsById(UUID.fromString(dummyUserId))).thenReturn(true);
        when(userRepository.findById(UUID.fromString(dummyUserId))).thenReturn(java.util.Optional.ofNullable(dummyUser));
        assertThrows(CurrencyNotMatchException.class, () ->requestsValidator.checkLoadRequestValidity(dummyMessageId, dummyLoadRequest));
    }

}
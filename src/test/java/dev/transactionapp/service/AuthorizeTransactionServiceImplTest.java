package dev.transactionapp.service;

import dev.transactionapp.entity.*;
import dev.transactionapp.enums.DebitCredit;
import dev.transactionapp.enums.ResponseCode;
import dev.transactionapp.repository.AuthorizationResponseRepository;
import dev.transactionapp.repository.UserRepository;
import dev.transactionapp.validator.RequestsValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class AuthorizeTransactionServiceImplTest {

    @InjectMocks
    private AuthorizeTransactionServiceImpl authorizeTransactionService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AuthorizationResponseRepository authorizationResponseRepository;

    @Mock
    private RequestsValidator requestsValidator;


    private Amount dummyTransactionAmount;
    private AuthorizationRequest dummyAuthorizationRequest;
    private User dummyUser;
    private AuthorizationResponse expectedAuthorizationResponse;
    private Amount expectedTransactionAmount;
    private String dummyUserId;
    private String dummyMessageId;
    @BeforeEach
    void setUp(){
        dummyMessageId = "someMessageId";
        dummyUserId = "f09752d7-c4e7-4491-98c8-36a9f5fc9f37"; // has to be UUID
        dummyUser = new User("USD", Double.parseDouble("200"));
    }

    @Test
    void authorizeTransactionAndGetResponseApprovedTest(){
        dummyTransactionAmount = new Amount("20", "USD", "DEBIT");
        dummyAuthorizationRequest = new AuthorizationRequest(dummyUserId, dummyMessageId, dummyTransactionAmount);

        expectedTransactionAmount = new Amount("180.0", "USD", DebitCredit.DEBIT.toString());
        expectedAuthorizationResponse = new AuthorizationResponse(dummyMessageId, dummyUserId, ResponseCode.APPROVED.toString(), expectedTransactionAmount);

        doNothing().when(requestsValidator).checkAuthorizationRequestValidity(dummyMessageId, dummyAuthorizationRequest);

        when(userRepository.findById(UUID.fromString(dummyUserId))).thenReturn(java.util.Optional.ofNullable(dummyUser));
        when(userRepository.save(ArgumentMatchers.any())).thenReturn(dummyUser);

        AuthorizationResponse authorizationResponse = authorizeTransactionService.authorizeTransactionAndGetResponse(dummyMessageId, dummyAuthorizationRequest);
        System.out.println(authorizationResponse);
        assertEquals(expectedAuthorizationResponse, authorizationResponse);
    }

    @Test
    void authorizeTransactionAndGetResponseDeclinedTest(){
        dummyTransactionAmount = new Amount("210", "USD", "DEBIT");
        dummyAuthorizationRequest = new AuthorizationRequest(dummyUserId, dummyMessageId, dummyTransactionAmount);

        expectedTransactionAmount = new Amount("210.0", "USD", "DEBIT");
        expectedAuthorizationResponse = new AuthorizationResponse(dummyMessageId, dummyUserId, ResponseCode.DECLINED.toString(), expectedTransactionAmount);

        doNothing().when(requestsValidator).checkAuthorizationRequestValidity(dummyMessageId, dummyAuthorizationRequest);

        when(userRepository.findById(UUID.fromString(dummyUserId))).thenReturn(java.util.Optional.ofNullable(dummyUser));

        when(authorizationResponseRepository.save(ArgumentMatchers.any())).thenReturn(expectedAuthorizationResponse);

        AuthorizationResponse authorizationResponse = authorizeTransactionService.authorizeTransactionAndGetResponse(dummyMessageId, dummyAuthorizationRequest);
        assertEquals(expectedAuthorizationResponse, authorizationResponse);
    }


    @Test
    void authorizeTransactionAndGetResponseExceptionTest(){
        dummyTransactionAmount = new Amount("210", "USD", "DEBIT");
        dummyAuthorizationRequest = new AuthorizationRequest(dummyUserId, dummyMessageId, dummyTransactionAmount);
        doThrow(new RuntimeException("Some runtime exception")).when(requestsValidator).checkAuthorizationRequestValidity(dummyMessageId, dummyAuthorizationRequest);
        assertThrows(RuntimeException.class, () -> authorizeTransactionService.authorizeTransactionAndGetResponse(dummyMessageId, dummyAuthorizationRequest));
    }

    @Test
    void testThreads(){
        //later
    }

}
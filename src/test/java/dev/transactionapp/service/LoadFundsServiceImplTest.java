package dev.transactionapp.service;

import dev.transactionapp.entity.*;
import dev.transactionapp.enums.DebitCredit;
import dev.transactionapp.repository.UserRepository;
import dev.transactionapp.validator.RequestsValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoadFundsServiceImplTest {

    @InjectMocks
    private LoadFundsServiceImpl loadFundsService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RequestsValidator requestsValidator;


    private Amount dummyTransactionAmount;
    private LoadRequest dummyLoadRequest;
    private User dummyUser;
    private LoadResponse expectedLoadResponse;
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
    void loadFundsAndGetResponseTest(){
        dummyTransactionAmount = new Amount("20", "USD", "CREDIT");
        dummyLoadRequest = new LoadRequest(dummyUserId, dummyMessageId, dummyTransactionAmount);

        expectedTransactionAmount = new Amount("220.0", "USD", DebitCredit.CREDIT.toString());
        expectedLoadResponse = new LoadResponse(dummyUserId, dummyMessageId, expectedTransactionAmount);

        doNothing().when(requestsValidator).checkLoadRequestValidity(dummyMessageId, dummyLoadRequest);

        when(userRepository.findById(UUID.fromString(dummyUserId))).thenReturn(java.util.Optional.ofNullable(dummyUser));
        when(userRepository.save(ArgumentMatchers.any())).thenReturn(dummyUser);

        LoadResponse loadResponse = loadFundsService.loadFundsAndGetResponse(dummyMessageId, dummyLoadRequest);
        assertEquals(loadResponse, expectedLoadResponse);

    }


    @Test
    void loadFundsAndGetResponseExceptionTest(){
        dummyTransactionAmount = new Amount("20", "USD", "CREDIT");
        dummyLoadRequest = new LoadRequest(dummyUserId, dummyMessageId, dummyTransactionAmount);
        doThrow(new RuntimeException("Some runtime exception")).when(requestsValidator).checkLoadRequestValidity(dummyMessageId, dummyLoadRequest);
        assertThrows(RuntimeException.class, () -> loadFundsService.loadFundsAndGetResponse(dummyMessageId, dummyLoadRequest));
    }
}
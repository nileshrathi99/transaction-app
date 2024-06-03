package dev.transactionapp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.transactionapp.entity.*;
import dev.transactionapp.enums.ResponseCode;
import dev.transactionapp.repository.UserRepository;
import dev.transactionapp.service.AuthorizeTransactionService;
import dev.transactionapp.service.LoadFundsService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(controllers = TransactionalController.class)
public class TransactionalControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private AuthorizeTransactionService authorizeTransactionServiceImpl;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private LoadFundsService loadFundsServiceImpl;

    private String dummyMessageId;
    private String dummyUserId;
    private Amount dummyTransactionAmount;
    private AuthorizationRequest dummyAuthorizationRequest;
    private AuthorizationResponse expectedAuthorizationResponse;
    private LoadRequest dummyLoadRequest;
    private LoadResponse expectedLoadResponse;

    @BeforeEach
    void setUp() {
        dummyMessageId = "someMessageId";
        dummyUserId = "f09752d7-c4e7-4491-98c8-36a9f5fc9f37"; // has to be UUID
        dummyTransactionAmount = new Amount("20", "USD", "DEBIT");
        dummyAuthorizationRequest = new AuthorizationRequest(dummyUserId, dummyMessageId, dummyTransactionAmount);
        expectedAuthorizationResponse = new AuthorizationResponse(dummyMessageId, dummyUserId, ResponseCode.APPROVED.toString(), dummyTransactionAmount);

        dummyLoadRequest = new LoadRequest(dummyUserId, dummyMessageId, dummyTransactionAmount);
        expectedLoadResponse = new LoadResponse(dummyUserId, dummyMessageId, dummyTransactionAmount);

    }

    @Test
    void pingTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/ping"))
                .andExpect(status().isOk());
    }


    @Test
    void authorizeTransactionTest() throws Exception {
        when(authorizeTransactionServiceImpl.authorizeTransactionAndGetResponse(ArgumentMatchers.anyString(), ArgumentMatchers.any())).thenReturn(expectedAuthorizationResponse);
        mockMvc.perform(MockMvcRequestBuilders.put("/authorization/{messageId}", dummyMessageId)
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("utf-8")
                .content(objectMapper.writeValueAsString(dummyAuthorizationRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.messageId", Matchers.equalTo(dummyMessageId)))
                .andExpect(jsonPath("$.userId", Matchers.equalTo(dummyUserId)));
        ;
    }

    @Test
    void loadFundsTest() throws Exception {
        when(loadFundsServiceImpl.loadFundsAndGetResponse(ArgumentMatchers.anyString(), ArgumentMatchers.any())).thenReturn(expectedLoadResponse);
        mockMvc.perform(MockMvcRequestBuilders.put("/load/{messageId}", dummyMessageId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("utf-8")
                        .content(objectMapper.writeValueAsString(dummyLoadRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.messageId", Matchers.equalTo(dummyMessageId)))
                .andExpect(jsonPath("$.userId", Matchers.equalTo(dummyUserId)));
    }
}
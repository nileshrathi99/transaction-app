package dev.transactionapp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.transactionapp.entity.Amount;
import dev.transactionapp.entity.AuthorizationResponse;
import dev.transactionapp.entity.User;
import dev.transactionapp.enums.DebitCredit;
import dev.transactionapp.enums.ResponseCode;
import dev.transactionapp.repository.AuthorizationResponseRepository;
import dev.transactionapp.repository.UserRepository;
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

import java.util.ArrayList;
import java.util.Arrays;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(controllers = UtilityController.class)
public class UtilityControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthorizationResponseRepository authorizationResponseRepository;

    @MockBean
    private UserRepository userRepository;


    private User dummyUser;
    private Amount transactionAmount;
    private AuthorizationResponse dummyAuthorizationResponse;
    @BeforeEach
    void setUp(){
        dummyUser = new User("USD", Double.parseDouble("20"));
        transactionAmount = new Amount("50", "USD", DebitCredit.DEBIT.toString());
        dummyAuthorizationResponse = new AuthorizationResponse("messageId", "userId", ResponseCode.DECLINED.toString(), transactionAmount);
    }

    @Test
    void addUserTest() throws Exception {
        when(userRepository.save(ArgumentMatchers.any())).thenReturn(dummyUser);
        mockMvc.perform(MockMvcRequestBuilders.post("/user")
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("utf-8")
                .content(objectMapper.writeValueAsString(dummyUser)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.currency", Matchers.equalTo("USD")));
    }


    @Test
    void findAllUsersTest() throws Exception {
        when(userRepository.findAll()).thenReturn(new ArrayList<>(Arrays.asList(dummyUser)));
        mockMvc.perform(MockMvcRequestBuilders.get("/user"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].currency", Matchers.equalTo("USD")));
    }

    @Test
    void findAllAuthorizationResponses() throws Exception {
        when(authorizationResponseRepository.findAll()).thenReturn(new ArrayList<>(Arrays.asList(dummyAuthorizationResponse)));
        mockMvc.perform(MockMvcRequestBuilders.get("/responses"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].messageId", Matchers.equalTo("messageId")));
    }



}
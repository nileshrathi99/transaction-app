package dev.transactionapp;

import dev.transactionapp.entity.*;
import dev.transactionapp.repository.AuthorizationResponseRepository;
import dev.transactionapp.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ApplicationTest {

    @LocalServerPort
    private int port;

    private RestTemplate restTemplate;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthorizationResponseRepository authorizationResponseRepository;

    private String baseUrl = "http://localhost";
    private String dummyMessageId;
    private String dummyUserId;
    private Amount dummyTransactionAmount;
    private AuthorizationRequest dummyAuthorizationRequest;


    @BeforeEach
    void setUp(){
        baseUrl = baseUrl.concat(":").concat(String.valueOf(port));
        restTemplate = new RestTemplate();
        dummyMessageId = "someMessageId";
    }

    @Test
    public void addUserTest(){
        User user = new User("USD", Double.parseDouble("20"));
        String url = baseUrl.concat("/user");
        User addedUser = restTemplate.postForObject(url, user, User.class);
        assertEquals(user.getBalance(), addedUser.getBalance());
    }


    @Test
    @Sql(statements = "Insert into users(id, currency, balance, created_at) values ('846eb86e-b3fa-4577-b9a8-aa56227459b8', 'USD', '20', CURRENT_TIMESTAMP)", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(statements = "Delete from users where id = '846eb86e-b3fa-4577-b9a8-aa56227459b8'", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void findAllUsersTest(){
        String url = baseUrl.concat("/user");
        List<User> users = restTemplate.getForObject(url, List.class);
        assertTrue(userRepository.existsById(UUID.fromString("846eb86e-b3fa-4577-b9a8-aa56227459b8")));
    }

    @Test
    @Sql(statements = "Insert into authorization_responses(message_id, user_id, response_code, amount, currency, debit_or_credit) values ('messageId', '846eb86e-b3fa-4577-b9a8-aa56227459b8', 'DECLINED', '100', 'USD', 'DEBIT')", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(statements = "Delete from authorization_responses where message_id = 'messageId'", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void findAllAuthorizationResponsesTest(){
        String url = baseUrl.concat("/responses");
        List<AuthorizationResponse> users = restTemplate.getForObject(url, List.class);
        assertEquals(users.size(), authorizationResponseRepository.findAll().size());
    }


    @Test
    @Sql(statements = "Insert into users(id, currency, balance, created_at) values ('846eb86e-b3fa-4577-b9a8-aa56227459b8', 'USD', '20', CURRENT_TIMESTAMP)", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(statements = "Delete from users where id = '846eb86e-b3fa-4577-b9a8-aa56227459b8'", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void authorizeTransactionDeclinedTest(){
        dummyUserId = "846eb86e-b3fa-4577-b9a8-aa56227459b8";
        dummyTransactionAmount = new Amount("200", "USD", "DEBIT");
        dummyAuthorizationRequest = new AuthorizationRequest(dummyUserId, dummyMessageId, dummyTransactionAmount);
        String url = baseUrl.concat("/authorization/{messageId}");
        restTemplate.put(url, dummyAuthorizationRequest, dummyMessageId);
        assertTrue(authorizationResponseRepository.existsById(dummyMessageId));
    }

    @Test
    @Sql(statements = "Insert into users(id, currency, balance, created_at) values ('846eb86e-b3fa-4577-b9a8-aa56227459b8', 'USD', '500', CURRENT_TIMESTAMP)", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(statements = "Delete from users where id = '846eb86e-b3fa-4577-b9a8-aa56227459b8'", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void authorizeTransactionApprovedTest(){
        dummyUserId = "846eb86e-b3fa-4577-b9a8-aa56227459b8";
        dummyTransactionAmount = new Amount("200", "USD", "DEBIT");
        dummyAuthorizationRequest = new AuthorizationRequest(dummyUserId, dummyMessageId, dummyTransactionAmount);
        String url = baseUrl.concat("/authorization/{messageId}");
        restTemplate.put(url, dummyAuthorizationRequest, dummyMessageId);
        assertFalse(authorizationResponseRepository.existsById(dummyMessageId));
    }

    @Test
    @Sql(statements = "Insert into users(id, currency, balance, created_at) values ('846eb86e-b3fa-4577-b9a8-aa56227459b8', 'USD', '20', CURRENT_TIMESTAMP)", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(statements = "Delete from users where id = '846eb86e-b3fa-4577-b9a8-aa56227459b8'", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void loadFundsTest(){
        dummyUserId = "846eb86e-b3fa-4577-b9a8-aa56227459b8";
        dummyTransactionAmount = new Amount("200", "USD", "CREDIT");
        dummyAuthorizationRequest = new AuthorizationRequest(dummyUserId, dummyMessageId, dummyTransactionAmount);
        String url = baseUrl.concat("/load/{messageId}");
        restTemplate.put(url, dummyAuthorizationRequest, dummyMessageId);
        assertEquals(Double.parseDouble("220"), userRepository.findById(UUID.fromString("846eb86e-b3fa-4577-b9a8-aa56227459b8")).get().getBalance());
    }



}
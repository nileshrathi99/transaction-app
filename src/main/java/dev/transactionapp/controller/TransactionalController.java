package dev.transactionapp.controller;

import dev.transactionapp.entity.AuthorizationRequest;
import dev.transactionapp.entity.LoadRequest;
import dev.transactionapp.entity.Ping;
import dev.transactionapp.service.AuthorizeTransactionService;
import dev.transactionapp.service.LoadFundsService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
public class TransactionalController {

    @Autowired
    private AuthorizeTransactionService authorizeTransactionServiceImpl;

    @Autowired
    private LoadFundsService loadFundsServiceImpl;

    /**
     * Handles a GET request to "/ping".
     * Responds with a simple "Ping" object and HTTP status code OK (200).
     * Used for basic health checks.
     *
     * @return ResponseEntity containing a Ping object and HttpStatus.OK
     */
    @GetMapping("/ping")
    public ResponseEntity<?> healthCheck(){
        return new ResponseEntity<>(new Ping(), HttpStatus.OK);
    }


    /**
     * Handles a PUT request to "/authorization/{messageId}".
     * Expects a valid AuthorizationRequest object in the request body.
     * Delegates processing to the `authorizeTransactionServiceImpl` and returns the response.
     * Responds with HTTP status code CREATED (201) upon successful authorization.
     *
     * @param messageId The message ID from the path variable.
     * @param authorizationRequest The Authorization Request object containing user and transaction details.
     * @return ResponseEntity containing the Authorization Response object and HttpStatus.CREATED
     * @throws Exception if any error occurs during processing
     */
    @PutMapping("/authorization/{messageId}")
    public ResponseEntity<?> authorizeTransaction(@PathVariable String messageId, @Valid @RequestBody AuthorizationRequest authorizationRequest){
        return new ResponseEntity<>(authorizeTransactionServiceImpl.authorizeTransactionAndGetResponse(messageId, authorizationRequest), HttpStatus.CREATED);
    }

    /**
     * Handles a PUT request to "/load/{messageId}".
     * Expects a valid LoadRequest object in the request body.
     * Delegates processing to the `loadFundsServiceImpl` and returns the response.
     * Responds with HTTP status code CREATED (201) upon successful load funds operation.
     *
     * @param messageId The message ID from the path variable.
     * @param loadRequest The Load Request object containing user and transaction details.
     * @return ResponseEntity containing the Load Response object and HttpStatus.CREATED
     * @throws Exception if any error occurs during processing
     */
    @PutMapping("/load/{messageId}")
    public ResponseEntity<?> loadFunds(@PathVariable String messageId, @Valid @RequestBody LoadRequest loadRequest){
        return new ResponseEntity<>(loadFundsServiceImpl.loadFundsAndGetResponse(messageId, loadRequest), HttpStatus.CREATED);
    }

}

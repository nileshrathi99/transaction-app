package dev.transactionapp.exception;

import dev.transactionapp.entity.Error;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RestExceptionHandlerTest {

    @InjectMocks
    RestExceptionHandler handler;

    @Mock
    private MethodArgumentNotValidException methodArgumentNotValidException;


    @Test
    void messageIdNotMatchExceptionHandlerTest() {
        String expectedMessage = "Message ID not found";
        MessageIdNotMatchException ex = new MessageIdNotMatchException(expectedMessage);
        ResponseEntity<Error> response = handler.messageIdNotMatchExceptionHandler(ex);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(expectedMessage, response.getBody().getMessage());
    }

    @Test
    void messageIdAlreadyExistsExceptionHandlerTest() {
        String expectedMessage = "Message ID already exists";
        MessageIdAlreadyExistsException ex = new MessageIdAlreadyExistsException(expectedMessage);
        ResponseEntity<Error> response = handler.messageIdAlreadyExistsExceptionHandler(ex);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(expectedMessage, response.getBody().getMessage());
    }

    @Test
    void userNotFoundExceptionHandlerTest() {
        String expectedMessage = "User doesn't exist";
        UserNotFoundException ex = new UserNotFoundException(expectedMessage);
        ResponseEntity<Error> response = handler.userNotFoundExceptionHandler(ex);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(expectedMessage, response.getBody().getMessage());
    }

    @Test
    void currencyNotMatchExceptionHandlerTest() {
        String expectedMessage = "Currency doesn't match";
        CurrencyNotMatchException ex = new CurrencyNotMatchException(expectedMessage);
        ResponseEntity<Error> response = handler.currencyNotMatchExceptionHandler(ex);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(expectedMessage, response.getBody().getMessage());
    }

    @Test
    void invalidTransactionTypeExceptionHandlerTest() {
        String expectedMessage = "Invalid Transaction Type";
        InvalidTransactionTypeException ex = new InvalidTransactionTypeException(expectedMessage);
        ResponseEntity<Error> response = handler.invalidTransactionTypeExceptionHandler(ex);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(expectedMessage, response.getBody().getMessage());
    }

    @Test
    void invalidUUIDExceptionHandlerTest() {
        String expectedMessage = "Invalid UUID format";
        InvalidUUIDException ex = new InvalidUUIDException(expectedMessage);
        ResponseEntity<Error> response = handler.invalidUUIDExceptionHandler(ex);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(expectedMessage, response.getBody().getMessage());
    }

    @Test
    void methodArgumentNotValidExceptionTest() {
        String expectedMessage = "{amount=should not be empty}";
        when(methodArgumentNotValidException.getFieldErrors()).thenReturn(Arrays.asList(new FieldError("Amount", "amount", "should not be empty")));
        ResponseEntity<Error> response = handler.methodArgumentNotValidException(methodArgumentNotValidException);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(expectedMessage, response.getBody().getMessage());

    }
}
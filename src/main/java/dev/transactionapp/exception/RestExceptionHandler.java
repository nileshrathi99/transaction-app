package dev.transactionapp.exception;

import dev.transactionapp.entity.Error;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

/**
 * This class is a global exception handler for the REST API.
 * It's annotated with @Component and @ControllerAdvice to be automatically discovered by Spring.
 *
 * It defines methods annotated with @ExceptionHandler to handle specific exceptions thrown by the application.
 * Each handler method takes the exception object as input and returns a ResponseEntity containing an Error object
 * with appropriate error message and HTTP status code.
 */
@Component
@ControllerAdvice
public class RestExceptionHandler {

    /**
     * Handles MessageIdNotMatchException.
     * This exception is likely thrown when message IDs don't match between request and processing.
     * The handler creates an Error object with the exception message and a BAD_REQUEST (400) status code.
     *
     * @param ex The MessageIdNotMatchException object.
     * @return ResponseEntity containing an Error object and HttpStatus.BAD_REQUEST
     */
    @ExceptionHandler(MessageIdNotMatchException.class)
    public ResponseEntity<Error> messageIdNotMatchExceptionHandler(MessageIdNotMatchException ex){
        Error error = new Error(ex.getLocalizedMessage(), HttpStatus.BAD_REQUEST.toString());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles MessageIdAlreadyExistsException.
     * This exception is likely thrown when message ID already exists in repository and isn't unique.
     * The handler creates an Error object with the exception message and a BAD_REQUEST (400) status code.
     *
     * @param ex The MessageIdAlreadyExistsException object.
     * @return ResponseEntity containing an Error object and HttpStatus.BAD_REQUEST
     */
    @ExceptionHandler(MessageIdAlreadyExistsException.class)
    public ResponseEntity<Error> messageIdAlreadyExistsExceptionHandler(MessageIdAlreadyExistsException ex){
        Error error = new Error(ex.getLocalizedMessage(), HttpStatus.BAD_REQUEST.toString());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles UserNotFoundException.
     * This exception is likely thrown when user ID doesn't exist in repository.
     * The handler creates an Error object with the exception message and a BAD_REQUEST (400) status code.
     *
     * @param ex The UserNotFoundException object.
     * @return ResponseEntity containing an Error object and HttpStatus.BAD_REQUEST
     */
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Error> userNotFoundExceptionHandler(UserNotFoundException ex){
        Error error = new Error(ex.getLocalizedMessage(), HttpStatus.BAD_REQUEST.toString());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles CurrencyNotMatchException.
     * This exception is likely thrown when user's currency and transaction currency doesn't match
     * The handler creates an Error object with the exception message and a BAD_REQUEST (400) status code.
     *
     * @param ex The CurrencyNotMatchException object.
     * @return ResponseEntity containing an Error object and HttpStatus.BAD_REQUEST
     */
    @ExceptionHandler(CurrencyNotMatchException.class)
    public ResponseEntity<Error> currencyNotMatchExceptionHandler(CurrencyNotMatchException ex){
        Error error = new Error(ex.getLocalizedMessage(), HttpStatus.BAD_REQUEST.toString());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }


    /**
     * Handles InvalidTransactionTypeException.
     * This exception is likely thrown when user's transaction type is unknown
     * The handler creates an Error object with the exception message and a BAD_REQUEST (400) status code.
     *
     * @param ex The InvalidTransactionTypeException object.
     * @return ResponseEntity containing an Error object and HttpStatus.BAD_REQUEST
     */
    @ExceptionHandler(InvalidTransactionTypeException.class)
    public ResponseEntity<Error> invalidTransactionTypeExceptionHandler(InvalidTransactionTypeException ex){
        Error error = new Error(ex.getLocalizedMessage(), HttpStatus.BAD_REQUEST.toString());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles InvalidUUIDException.
     * This exception is likely thrown when user id is not of UUID format
     * The handler creates an Error object with the exception message and a BAD_REQUEST (400) status code.
     *
     * @param ex The InvalidUUIDException object.
     * @return ResponseEntity containing an Error object and HttpStatus.BAD_REQUEST
     */
    @ExceptionHandler(InvalidUUIDException.class)
    public ResponseEntity<Error> invalidUUIDExceptionHandler(InvalidUUIDException ex){
        Error error = new Error(ex.getLocalizedMessage(), HttpStatus.BAD_REQUEST.toString());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles MethodArgumentNotValidException.
     * This exception is typically thrown when request body validation fails (e.g., missing fields, invalid formats).
     * The handler creates a Map to store validation errors (field and corresponding message).
     * It iterates through field errors and populates the Map.
     * Finally, an Error object is created with the error Map converted to string and a BAD_REQUEST (400) status code.
     *
     * @param ex The MethodArgumentNotValidException object.
     * @return ResponseEntity containing an Error object with validation errors and HttpStatus.BAD_REQUEST
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Error> methodArgumentNotValidException(MethodArgumentNotValidException ex){
        Map<String, String> errorMap = new HashMap<>();
        ex.getFieldErrors().forEach(fieldError ->
                errorMap.put(fieldError.getField(), fieldError.getDefaultMessage())
        );
        Error error = new Error(errorMap.toString(), HttpStatus.BAD_REQUEST.toString());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }
}

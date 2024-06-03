package dev.transactionapp.validator;

import dev.transactionapp.entity.*;
import dev.transactionapp.enums.Currency;
import dev.transactionapp.enums.DebitCredit;
import dev.transactionapp.enums.ResponseCode;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class ObjectsValidatorTest {

    private ObjectsValidator<Amount> amountObjectsValidator;
    private ObjectsValidator<AuthorizationRequest> authorizationRequestObjectsValidator;
    private ObjectsValidator<AuthorizationResponse> authorizationResponseObjectsValidator;
    private ObjectsValidator<LoadRequest> loadRequestRequestObjectsValidator;
    private ObjectsValidator<LoadResponse> loadResponseObjectsValidator;

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
        amountObjectsValidator = new ObjectsValidator<>();
        authorizationRequestObjectsValidator = new ObjectsValidator<>();
        authorizationResponseObjectsValidator = new ObjectsValidator<>();
        loadRequestRequestObjectsValidator = new ObjectsValidator<>();
        loadResponseObjectsValidator = new ObjectsValidator<>();
    }

    // Amount

    @Test
    void validateAmountObjectTest() {
        Amount amount = new Amount("20", "USD", DebitCredit.CREDIT.toString());
        Set<String> validationMessages = amountObjectsValidator.validate(amount);
        assertEquals(0, validationMessages.size());
    }

    @Test
    void validateAmountObjectAmountFieldTest1() {
        Amount amount = new Amount("-20", Currency.USD.name(), DebitCredit.CREDIT.toString());
        Set<String> validationMessages = amountObjectsValidator.validate(amount);
        Set<String> expectedValidationMessages = new HashSet<>();
        expectedValidationMessages.add("Amount must be a positive number");
        expectedValidationMessages.add("Amount can have optional 2 decimal");
        assertEquals(expectedValidationMessages, validationMessages);
    }

    @Test
    void validateAmountObjectAmountFieldTest2() {
        Amount amount = new Amount("20.000", Currency.USD.name(), DebitCredit.CREDIT.toString());
        Set<String> validationMessages = amountObjectsValidator.validate(amount);
        Set<String> expectedValidationMessages = new HashSet<>();
        expectedValidationMessages.add("Amount can have optional 2 decimal");
        assertEquals(expectedValidationMessages, validationMessages);
    }

    @Test
    void validateAmountObjectAmountFieldTest3() {
        Amount amount = new Amount("2000000000", Currency.USD.name(), DebitCredit.CREDIT.toString());
        Set<String> validationMessages = amountObjectsValidator.validate(amount);
        Set<String> expectedValidationMessages = new HashSet<>();
        expectedValidationMessages.add("Amount must be a less than 1 Billion");
        assertEquals(expectedValidationMessages, validationMessages);
    }

    // need to be updated in future
    @Test
    void validateAmountObjectCurrencyFieldTest() {
        Amount amount = new Amount("20", "ABC", DebitCredit.CREDIT.toString());
        Set<String> validationMessages = amountObjectsValidator.validate(amount);
        Set<String> expectedValidationMessages = new HashSet<>();
        expectedValidationMessages.add("currently supported: USD, INR");
        assertEquals(expectedValidationMessages, validationMessages);
    }

    @Test
    void validateAmountObjectDebitCreditFieldTest1() {
        Amount amount = new Amount("20", Currency.USD.name(), "something");
        Set<String> validationMessages = amountObjectsValidator.validate(amount);
        Set<String> expectedValidationMessages = new HashSet<>();
        expectedValidationMessages.add("Must be DEBIT or CREDIT");
        assertEquals(expectedValidationMessages, validationMessages);
    }

    @Test
    void validateAmountObjectDebitCreditFieldTest2() {
        Amount amount = new Amount("20", Currency.USD.name(), null);
        Set<String> validationMessages = amountObjectsValidator.validate(amount);
        Set<String> expectedValidationMessages = new HashSet<>();
        expectedValidationMessages.add("Required, Must be DEBIT or CREDIT");
        assertEquals(expectedValidationMessages, validationMessages);
    }

    // AuthorizationRequest

    @Test
    void validateAuthorizationRequestObjectTest() {
        Amount amount = new Amount("20", Currency.USD.name(), DebitCredit.DEBIT.toString());
        AuthorizationRequest authorizationRequest = new AuthorizationRequest("userId","messageId", amount);
        Set<String> validationMessages = authorizationRequestObjectsValidator.validate(authorizationRequest);
        assertEquals(0, validationMessages.size());
    }


    @Test
    void validateAuthorizationRequestObjectMessageIdFieldTest() {
        Amount amount = new Amount("20", Currency.USD.name(), DebitCredit.DEBIT.toString());
        AuthorizationRequest authorizationRequest = new AuthorizationRequest("userId","", amount);
        Set<String> validationMessages = authorizationRequestObjectsValidator.validate(authorizationRequest);
        Set<String> expectedValidationMessages = new HashSet<>();
        expectedValidationMessages.add("Minimum 1 length required");
        assertEquals(expectedValidationMessages, validationMessages);
    }

    @Test
    void validateAuthorizationRequestObjectUserIdFieldTest() {
        Amount amount = new Amount("20", Currency.USD.name(), DebitCredit.DEBIT.toString());
        AuthorizationRequest authorizationRequest = new AuthorizationRequest("","messageId", amount);
        Set<String> validationMessages = authorizationRequestObjectsValidator.validate(authorizationRequest);
        Set<String> expectedValidationMessages = new HashSet<>();
        expectedValidationMessages.add("Minimum 1 length required");
        assertEquals(expectedValidationMessages, validationMessages);
    }


    // AuthorizationResponse

    @Test
    void validateAuthorizationResponseObjectTest() {
        Amount amount = new Amount("20", Currency.USD.name(), DebitCredit.DEBIT.toString());
        AuthorizationResponse authorizationResponse = new AuthorizationResponse("userId","messageId", ResponseCode.APPROVED.toString(), amount);
        Set<String> validationMessages = authorizationResponseObjectsValidator.validate(authorizationResponse);
        assertEquals(0, validationMessages.size());
    }


    @Test
    void validateAuthorizationResponseObjectMessageIdFieldTest() {
        Amount amount = new Amount("20", Currency.USD.name(), DebitCredit.DEBIT.toString());
        AuthorizationResponse authorizationResponse = new AuthorizationResponse("", "userId", ResponseCode.APPROVED.toString(), amount);
        Set<String> validationMessages = authorizationResponseObjectsValidator.validate(authorizationResponse);
        Set<String> expectedValidationMessages = new HashSet<>();
        expectedValidationMessages.add("Minimum 1 length required");
        assertEquals(expectedValidationMessages, validationMessages);
    }

    @Test
    void validateAuthorizationResponseObjectUserIdFieldTest() {
        Amount amount = new Amount("20", Currency.USD.name(), DebitCredit.DEBIT.toString());
        AuthorizationResponse authorizationResponse = new AuthorizationResponse("messageId", "", ResponseCode.APPROVED.toString(), amount);
        Set<String> validationMessages = authorizationResponseObjectsValidator.validate(authorizationResponse);
        Set<String> expectedValidationMessages = new HashSet<>();
        expectedValidationMessages.add("Minimum 1 length required");
        assertEquals(expectedValidationMessages, validationMessages);
    }

    @Test
    void validateAuthorizationResponseObjectResponseCodeFieldTest() {
        Amount amount = new Amount("20", Currency.USD.name(), DebitCredit.DEBIT.toString());
        AuthorizationResponse authorizationResponse = new AuthorizationResponse("messageId", "userId", "something", amount);
        Set<String> validationMessages = authorizationResponseObjectsValidator.validate(authorizationResponse);
        Set<String> expectedValidationMessages = new HashSet<>();
        expectedValidationMessages.add("Must be APPROVED or DECLINED");
        assertEquals(expectedValidationMessages, validationMessages);
    }


    // LoadRequest

    @Test
    void validateLoadRequestObjectTest() {
        Amount amount = new Amount("20", Currency.USD.name(), DebitCredit.CREDIT.toString());
        LoadRequest loadRequest = new LoadRequest("userId","messageId", amount);
        Set<String> validationMessages = loadRequestRequestObjectsValidator.validate(loadRequest);
        assertEquals(0, validationMessages.size());
    }


    @Test
    void validateLoadRequestObjectMessageIdFieldTest() {
        Amount amount = new Amount("20", Currency.USD.name(), DebitCredit.CREDIT.toString());
        LoadRequest loadRequest = new LoadRequest("userId","", amount);
        Set<String> validationMessages = loadRequestRequestObjectsValidator.validate(loadRequest);
        Set<String> expectedValidationMessages = new HashSet<>();
        expectedValidationMessages.add("Minimum 1 length required");
        assertEquals(expectedValidationMessages, validationMessages);
    }

    @Test
    void validateLoadRequestObjectUserIdFieldTest() {
        Amount amount = new Amount("20", Currency.USD.name(), DebitCredit.CREDIT.toString());
        LoadRequest loadRequest = new LoadRequest("","messageId", amount);
        Set<String> validationMessages = loadRequestRequestObjectsValidator.validate(loadRequest);
        Set<String> expectedValidationMessages = new HashSet<>();
        expectedValidationMessages.add("Minimum 1 length required");
        assertEquals(expectedValidationMessages, validationMessages);
    }

    // Load Response

    @Test
    void validateLoadResponseObjectTest() {
        Amount amount = new Amount("20", Currency.USD.name(), DebitCredit.CREDIT.toString());
        LoadResponse loadResponse = new LoadResponse("userId","messageId", amount);
        Set<String> validationMessages = loadResponseObjectsValidator.validate(loadResponse);
        assertEquals(0, validationMessages.size());
    }


    @Test
    void validateLoadResponseObjectMessageIdFieldTest() {
        Amount amount = new Amount("20", Currency.USD.name(), DebitCredit.CREDIT.toString());
        LoadResponse loadResponse = new LoadResponse("userId","", amount);
        Set<String> validationMessages = loadResponseObjectsValidator.validate(loadResponse);
        Set<String> expectedValidationMessages = new HashSet<>();
        expectedValidationMessages.add("Minimum 1 length required");
        assertEquals(expectedValidationMessages, validationMessages);
    }

    @Test
    void validateLoadResponseObjectUserIdFieldTest() {
        Amount amount = new Amount("20", Currency.USD.name(), DebitCredit.CREDIT.toString());
        LoadResponse loadResponse = new LoadResponse("","messageId", amount);
        Set<String> validationMessages = loadResponseObjectsValidator.validate(loadResponse);
        Set<String> expectedValidationMessages = new HashSet<>();
        expectedValidationMessages.add("Minimum 1 length required");
        assertEquals(expectedValidationMessages, validationMessages);
    }
}
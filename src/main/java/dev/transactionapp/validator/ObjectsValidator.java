package dev.transactionapp.validator;


import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Component
public class ObjectsValidator<T> {

    private final ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
    private final Validator validator = validatorFactory.getValidator();

    /**
     * Validates the provided object using Jakarta Bean Validation annotations.
     *
     * @param objectToValidate The object to perform validation on.
     * @return A set of error messages if any validation violations are found, otherwise an empty set.
     */
    public Set<String> validate(T objectToValidate){
        Set<ConstraintViolation<T>> violations = validator.validate(objectToValidate);
        if(!violations.isEmpty()){
            log.warn("Validation failed for object of type {} with {} violations:",
                    objectToValidate.getClass().getSimpleName(), violations.size());
            return violations.stream()
                    .map(ConstraintViolation::getMessage)
                    .collect(Collectors.toSet());
        }
        log.debug("Object of type {} passed validation successfully.", objectToValidate.getClass().getSimpleName());
        return Collections.emptySet();
    }
}
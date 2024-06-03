package dev.codescreen.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * This class implements a custom constraint validator for the `@ValueOfEnum` annotation.
 * It checks if the provided CharSequence value matches one of the accepted enum values.
 */
@Slf4j
public class ValueOfEnumValidator implements ConstraintValidator<ValueOfEnum, CharSequence> {

    private List<String> acceptedValues;

    /**
     * Initializes the validator with the accepted enum values based on the provided annotation.
     *
     * @param annotation The `@ValueOfEnum` annotation containing the enum class reference.
     */
    @Override
    public void initialize(ValueOfEnum annotation) {
        acceptedValues = Stream.of(annotation.enumClass().getEnumConstants())
                .map(Enum::name)
                .collect(Collectors.toList());
    }

    /**
     * Checks if the provided CharSequence value is a valid enum value based on the accepted list.
     *
     * @param value The CharSequence value to validate.
     * @param context The ConstraintValidatorContext used for logging or building constraint violation messages.
     * @return True if the value is a valid enum value, false otherwise.
     */
    @Override
    public boolean isValid(CharSequence value, ConstraintValidatorContext context) {
        if (value == null)
            return true;
        boolean isValid = acceptedValues.contains(value.toString());
        if (!isValid)
            log.warn("Invalid value '{}' provided for @ValueOfEnum annotation. Accepted values: {}", value, acceptedValues);
        return isValid;
    }
}

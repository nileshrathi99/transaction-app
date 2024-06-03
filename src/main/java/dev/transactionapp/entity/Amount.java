package dev.transactionapp.entity;

import dev.transactionapp.enums.Currency;
import dev.transactionapp.enums.DebitCredit;
import dev.transactionapp.validator.ValueOfEnum;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class Amount {

    @NotNull
    @NotBlank(message = "Amount required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Amount must be a positive number")
    @Pattern(regexp = "\\d+(\\.\\d{1,2})?", message = "Amount can have optional 2 decimal")
    @DecimalMax(value = "1000000000.0", inclusive = false, message = "Amount must be a less than 1 Billion")
    @Column(nullable = false)
    private String amount;

    @NotNull(message = "cannot be null, currently supported: USD, INR")
    @ValueOfEnum(enumClass = Currency.class, message = "currently supported: USD, INR")
    @Column(nullable = false)
    private String currency;

    @NotNull(message = "Required, Must be DEBIT or CREDIT")
    @ValueOfEnum(enumClass = DebitCredit.class, message = "Must be DEBIT or CREDIT")
    @Column(nullable = false)
    private String debitOrCredit;

}

package dev.codescreen.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.Valid;



@Data
@AllArgsConstructor
public class AuthorizationRequest {

    @NotNull
    @NotBlank(message = "Minimum 1 length required")
    private String userId;

    @NotNull
    @NotBlank(message = "Minimum 1 length required")
    private String messageId;

    @NotNull(message = "Transaction details required")
    @Valid
    private Amount transactionAmount;
}

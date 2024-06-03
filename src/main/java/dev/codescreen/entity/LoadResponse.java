package dev.codescreen.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;


@Data
@AllArgsConstructor
public class LoadResponse {

    @NotNull
    @NotBlank(message = "Minimum 1 length required")
    private String userId;

    @NotNull
    @NotBlank(message = "Minimum 1 length required")
    private String messageId;

    @NotNull(message = "Transaction details required")
    private Amount balance;
}

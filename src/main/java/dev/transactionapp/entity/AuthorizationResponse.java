package dev.transactionapp.entity;

import dev.transactionapp.enums.ResponseCode;
import dev.transactionapp.validator.ValueOfEnum;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "authorization_responses")
public class AuthorizationResponse {

    @Id
    @NotNull
    @NotBlank(message = "Minimum 1 length required")
    private String messageId;

    @NotNull
    @NotBlank(message = "Minimum 1 length required")
    @Column(nullable = false)
    private String userId;

    @NotNull(message = "Required, Must be APPROVED or DECLINED")
    @ValueOfEnum(enumClass = ResponseCode.class, message = "Must be APPROVED or DECLINED")
    @Column(nullable = false)
    private String responseCode;

    @NotNull(message = "Transaction details required")
    @Column(nullable = false)
    private Amount balance;

}

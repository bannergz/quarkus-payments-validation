package com.example.payments.infrastructure.adapter.in.messaging.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO for the transaction data inside a validation response event.
 */
@Data
@NoArgsConstructor
public class ValidationResponseTransactionDto {

    @JsonProperty("transactionExternalId")
    private String transactionExternalId;

    @JsonProperty("transactionStatus")
    private ValidationResponseStatusDto transactionStatus;

    @JsonProperty("value")
    private BigDecimal value;
}

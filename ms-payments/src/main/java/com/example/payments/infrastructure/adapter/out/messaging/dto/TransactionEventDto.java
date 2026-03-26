package com.example.payments.infrastructure.adapter.out.messaging.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO representing the core transaction data in a messaging event.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionEventDto {

    @JsonProperty("transactionExternalId")
    private String transactionExternalId;

    @JsonProperty("transactionType")
    private TransactionTypeDto transactionType;

    @JsonProperty("transactionStatus")
    private TransactionStatusDto transactionStatus;

    @JsonProperty("value")
    private BigDecimal value;

    @JsonProperty("createdAt")
    private String createdAt;
}

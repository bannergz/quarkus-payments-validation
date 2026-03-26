package com.example.frauds.infrastructure.adapter.in.messaging.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO for transaction data within an incoming validation request event.
 */
@Data
@NoArgsConstructor
public class RequestTransactionDto {

    @JsonProperty("transactionExternalId")
    private String transactionExternalId;

    @JsonProperty("transactionType")
    private RequestTransactionTypeDto transactionType;

    @JsonProperty("transactionStatus")
    private RequestTransactionStatusDto transactionStatus;

    @JsonProperty("value")
    private BigDecimal value;

    @JsonProperty("createdAt")
    private String createdAt;

}

package com.example.frauds.infrastructure.adapter.out.messaging.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

/**
 * DTO for the transaction data in a validation response event (outgoing).
 */
@Data
@AllArgsConstructor
@Builder
public class ResponseTransactionDto {

    @JsonProperty("transactionExternalId")
    private String transactionExternalId;

    @JsonProperty("transactionStatus")
    private ResponseTransactionStatusDto transactionStatus;

    @JsonProperty("value")
    private BigDecimal value;

}

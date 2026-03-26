package com.example.frauds.infrastructure.adapter.in.messaging.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Root envelope DTO for the transaction-validation-request event (incoming).
 */
@Data
@NoArgsConstructor
public class TransactionValidationRequestDto {

    @JsonProperty("eventId")
    private String eventId;

    @JsonProperty("eventTimestamp")
    private String eventTimestamp;

    @JsonProperty("eventType")
    private String eventType;

    @JsonProperty("transaction")
    private RequestTransactionDto transaction;

}

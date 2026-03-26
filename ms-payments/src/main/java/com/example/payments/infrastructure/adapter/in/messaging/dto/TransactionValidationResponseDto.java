package com.example.payments.infrastructure.adapter.in.messaging.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Root envelope DTO for the transaction-validation-response event.
 */
@Data
@NoArgsConstructor
public class TransactionValidationResponseDto {

    @JsonProperty("eventId")
    private String eventId;

    @JsonProperty("eventTimestamp")
    private String eventTimestamp;

    @JsonProperty("eventType")
    private String eventType;

    @JsonProperty("transaction")
    private ValidationResponseTransactionDto transaction;
}

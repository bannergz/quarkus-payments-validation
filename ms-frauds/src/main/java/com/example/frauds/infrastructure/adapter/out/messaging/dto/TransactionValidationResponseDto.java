package com.example.frauds.infrastructure.adapter.out.messaging.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Root envelope DTO for the transaction-validation-response event (outgoing).
 * Matches the JSON schema registered in the Confluent Schema Registry.
 */
@Data
@AllArgsConstructor
public class TransactionValidationResponseDto {

    @JsonProperty("eventId")
    private String eventId;

    @JsonProperty("eventTimestamp")
    private String eventTimestamp;

    @JsonProperty("eventType")
    private String eventType;

    @JsonProperty("transaction")
    private ResponseTransactionDto transaction;

}

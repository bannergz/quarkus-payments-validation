package com.example.payments.infrastructure.adapter.out.messaging.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Root envelope DTO for the transaction-validation-request event.
 * Matches the JSON schema registered in the Confluent Schema Registry.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionValidationRequestDto {

    @JsonProperty("eventId")
    private String eventId;

    @JsonProperty("eventTimestamp")
    private String eventTimestamp;

    @JsonProperty("eventType")
    private String eventType;

    @JsonProperty("transaction")
    private TransactionEventDto transaction;
}

package com.example.frauds.infrastructure.adapter.out.messaging.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * DTO for the transaction status in a validation response event (outgoing).
 */
@Data
@AllArgsConstructor
public class ResponseTransactionStatusDto {

    @JsonProperty("id")
    private Integer id;

    @JsonProperty("name")
    private String name;

}

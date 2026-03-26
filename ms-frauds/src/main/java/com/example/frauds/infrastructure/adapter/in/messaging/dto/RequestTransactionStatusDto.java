package com.example.frauds.infrastructure.adapter.in.messaging.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for transaction status data within an incoming validation request event.
 */
@Data
@NoArgsConstructor
public class RequestTransactionStatusDto {

    @JsonProperty("id")
    private Integer id;

    @JsonProperty("name")
    private String name;

}

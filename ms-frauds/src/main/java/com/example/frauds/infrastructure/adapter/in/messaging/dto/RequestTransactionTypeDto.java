package com.example.frauds.infrastructure.adapter.in.messaging.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for transaction type data within an incoming validation request event.
 */
@Data
@NoArgsConstructor
public class RequestTransactionTypeDto {

    @JsonProperty("id")
    private Integer id;

    @JsonProperty("name")
    private String name;

}

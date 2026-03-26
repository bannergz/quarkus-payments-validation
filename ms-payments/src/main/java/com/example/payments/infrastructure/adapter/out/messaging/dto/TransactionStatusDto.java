package com.example.payments.infrastructure.adapter.out.messaging.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO representing transaction status data inside a messaging event.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionStatusDto {

    @JsonProperty("id")
    private Integer id;

    @JsonProperty("name")
    private String name;
}

package com.example.payments.infrastructure.adapter.in.messaging.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for the transaction status inside a validation response event.
 */
@Data
@NoArgsConstructor
public class ValidationResponseStatusDto {

    @JsonProperty("id")
    private Integer id;

    @JsonProperty("name")
    private String name;
}

package com.example.frauds.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * TransactionType value object for the ms-frauds bounded context.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionType {

    private Integer id;
    private String name;

}

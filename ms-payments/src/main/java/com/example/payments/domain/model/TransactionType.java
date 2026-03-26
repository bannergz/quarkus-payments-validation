package com.example.payments.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * TransactionType domain value object.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionType {

    private Integer id;
    private String name;
}

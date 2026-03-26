package com.example.frauds.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * TransactionStatus value object — used to express fraud validation outcomes.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionStatus {

    public static final TransactionStatus PENDING = new TransactionStatus(1, "PENDING");
    public static final TransactionStatus APPROVED = new TransactionStatus(2, "APPROVED");
    public static final TransactionStatus REJECTED = new TransactionStatus(3, "REJECTED");

    private Integer id;
    private String name;

}

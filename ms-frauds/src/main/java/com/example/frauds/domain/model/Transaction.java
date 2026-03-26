package com.example.frauds.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * Transaction domain model for the ms-frauds bounded context.
 * Represents the minimal transaction data required for fraud validation.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {

    private UUID transactionExternalId;
    private TransactionType transactionType;
    private TransactionStatus transactionStatus;
    private BigDecimal value;
    private Instant createdAt;

}

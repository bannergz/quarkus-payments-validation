package com.example.payments.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * Transaction domain model — pure domain entity, no framework dependencies.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {

    private UUID transactionExternalId;
    private UUID accountExternalIdDebit;
    private UUID accountExternalIdCredit;
    private TransactionType transactionType;
    private TransactionStatus transactionStatus;
    private BigDecimal value;
    private Instant createdAt;
    private Instant updatedAt;
}

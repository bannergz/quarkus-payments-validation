package com.example.payments.infrastructure.adapter.out.persistence.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Column;
import lombok.NoArgsConstructor;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * JPA entity for the transaction table.
 * Uses Panache active-record style with public fields for brevity.
 */
@Entity
@Table(name = "transaction", indexes = {
        @Index(name = "idx_transaction_external_id", columnList = "external_id")
})
@NoArgsConstructor
public class TransactionEntity extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @NotNull
    @Column(name = "external_id", nullable = false, unique = true, columnDefinition = "uuid")
    public UUID externalId;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transaction_type_id", nullable = false)
    public TransactionTypeEntity transactionType;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transaction_status_id", nullable = false)
    public TransactionStatusEntity transactionStatus;

    @NotNull
    @Column(name = "account_debit_id", nullable = false, columnDefinition = "uuid")
    public UUID accountDebitId;

    @NotNull
    @Column(name = "account_credit_id", nullable = false, columnDefinition = "uuid")
    public UUID accountCreditId;

    @NotNull
    @DecimalMin(value = "0.01", message = "Transaction value must be greater than zero")
    @Column(name = "value", nullable = false, precision = 19, scale = 4)
    public BigDecimal value;

    @NotNull
    @Column(name = "created_at", nullable = false)
    public Instant createdAt;

    @NotNull
    @Column(name = "updated_at", nullable = false)
    public Instant updatedAt;
}

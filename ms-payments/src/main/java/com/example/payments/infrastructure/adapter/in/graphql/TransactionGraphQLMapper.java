package com.example.payments.infrastructure.adapter.in.graphql;

import com.example.payments.domain.model.Transaction;
import com.example.payments.domain.model.TransactionStatus;
import com.example.payments.domain.model.TransactionType;
import com.example.payments.infrastructure.adapter.in.graphql.dto.CreateTransactionInput;
import jakarta.enterprise.context.ApplicationScoped;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

/**
 * Maps between GraphQL DTOs and domain models in the ms-payments context.
 */
@ApplicationScoped
public class TransactionGraphQLMapper {

    /**
     * Lookup map for transaction types by ID.
     * In production, load from the database; this is a seeded reference table.
     */
    private static final Map<Integer, String> TRANSACTION_TYPE_NAMES = Map.of(
            1, "DEBIT",
            2, "CREDIT",
            3, "TRANSFER"
    );

    public Transaction createInputToDomain(CreateTransactionInput input) {
        String typeName = TRANSACTION_TYPE_NAMES.getOrDefault(
                input.getTransferTypeId(), "UNKNOWN");

        Transaction domain = new Transaction();
        domain.setTransactionExternalId(UUID.randomUUID());
        domain.setAccountExternalIdDebit(input.getAccountExternalIdDebit());
        domain.setAccountExternalIdCredit(input.getAccountExternalIdCredit());
        domain.setTransactionType(new TransactionType(input.getTransferTypeId(), typeName));
        domain.setTransactionStatus(TransactionStatus.PENDING);
        domain.setValue(input.getValue());
        domain.setCreatedAt(Instant.now());
        domain.setUpdatedAt(Instant.now());
        return domain;
    }
}

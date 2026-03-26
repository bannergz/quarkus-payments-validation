package com.example.payments.domain.port.out;

import com.example.payments.domain.model.Transaction;
import com.example.payments.domain.model.TransactionStatus;
import java.util.Optional;
import java.util.UUID;

/**
 * Output port: repository contract for Transaction persistence.
 */
public interface TransactionRepositoryPort {

    /**
     * Persists a new transaction.
     *
     * @param transaction the transaction to persist
     * @return the persisted transaction with generated identifiers
     */
    Transaction save(Transaction transaction);

    /**
     * Finds a transaction by its external UUID.
     *
     * @param externalId the external UUID
     * @return an Optional containing the transaction if found
     */
    Optional<Transaction> findByExternalId(UUID externalId);

    /**
     * Updates the status of a transaction identified by its external UUID.
     *
     * @param externalId the external UUID
     * @param status the new status
     */
    void updateStatus(UUID externalId, TransactionStatus status);
}

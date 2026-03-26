package com.example.payments.domain.port.in;

import com.example.payments.domain.model.Transaction;

/**
 * Input port: use case for creating a new transaction.
 */
public interface CreateTransactionUseCase {

    /**
     * Creates a transaction and publishes a validation event.
     *
     * @param transaction the transaction to create
     * @return the persisted transaction
     */
    Transaction createTransaction(Transaction transaction);
}

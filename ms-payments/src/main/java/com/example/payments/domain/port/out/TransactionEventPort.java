package com.example.payments.domain.port.out;

import com.example.payments.domain.model.Transaction;

/**
 * Output port: event publishing contract for transaction validation events.
 */
public interface TransactionEventPort {

    /**
     * Publishes a transaction validation request event to the message broker.
     *
     * @param transaction the transaction to validate
     */
    void publishValidationRequest(Transaction transaction);
}

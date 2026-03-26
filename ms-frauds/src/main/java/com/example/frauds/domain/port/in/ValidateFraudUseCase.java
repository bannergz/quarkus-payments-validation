package com.example.frauds.domain.port.in;

import com.example.frauds.domain.model.Transaction;

/**
 * Input port: use case for validating a transaction for potential fraud.
 */
public interface ValidateFraudUseCase {

    /**
     * Evaluates fraud rules for the given transaction and publishes
     * a validation response event with the result.
     *
     * @param transaction the transaction to evaluate
     */
    void validateTransaction(Transaction transaction);
}

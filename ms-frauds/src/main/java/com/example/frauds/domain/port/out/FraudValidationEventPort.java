package com.example.frauds.domain.port.out;

import com.example.frauds.domain.model.Transaction;

/**
 * Output port: contract for publishing the fraud validation result.
 */
public interface FraudValidationEventPort {

    /**
     * Publishes the result of a fraud validation to the message broker.
     *
     * @param transaction the transaction with the resolved status
     */
    void publishValidationResponse(Transaction transaction);
}

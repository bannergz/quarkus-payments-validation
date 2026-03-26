package com.example.payments.domain.port.in;

import com.example.payments.domain.model.Transaction;
import java.util.UUID;

/**
 * Input port: use case for retrieving a transaction by its external ID.
 */
public interface RetrieveTransactionUseCase {

    /**
     * Retrieves a transaction by its external UUID.
     *
     * @param externalId the external ID of the transaction
     * @return the found transaction
     */
    Transaction retrieveTransaction(UUID externalId);
}

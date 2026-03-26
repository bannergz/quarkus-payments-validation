package com.example.payments.domain.port.in;

import com.example.payments.domain.model.TransactionStatus;
import java.util.UUID;

/**
 * Input port: use case for updating the status of a transaction.
 */
public interface UpdateTransactionStatusUseCase {

    /**
     * Updates the status of a transaction after fraud validation.
     *
     * @param externalId the external ID of the transaction
     * @param status the new status to apply
     */
    void updateTransactionStatus(UUID externalId, TransactionStatus status);
}

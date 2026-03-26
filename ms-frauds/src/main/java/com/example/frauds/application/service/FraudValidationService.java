package com.example.frauds.application.service;

import com.example.frauds.domain.model.Transaction;
import com.example.frauds.domain.model.TransactionStatus;
import com.example.frauds.domain.port.in.ValidateFraudUseCase;
import com.example.frauds.domain.port.out.FraudValidationEventPort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;

import java.math.BigDecimal;

/**
 * Application service implementing fraud validation logic.
 *
 * <p>Rule: transactions with value > 1000 are rejected; otherwise approved.
 */
@ApplicationScoped
public class FraudValidationService implements ValidateFraudUseCase {

    private static final Logger LOG = Logger.getLogger(FraudValidationService.class);
    private static final BigDecimal FRAUD_THRESHOLD = BigDecimal.valueOf(1000);

    @Inject
    FraudValidationEventPort fraudValidationEventPort;

    @Override
    public void validateTransaction(Transaction transaction) {
        TransactionStatus result = evaluateFraudRules(transaction);
        transaction.setTransactionStatus(result);

        LOG.infof("Transaction %s validated: status=%s (value=%s)",
                transaction.getTransactionExternalId(),
                result.getName(),
                transaction.getValue());

        try {
            fraudValidationEventPort.publishValidationResponse(transaction);
        } catch (Exception e) {
            LOG.errorf(e, "Failed to publish validation response for transaction %s",
                    transaction.getTransactionExternalId());
        }
    }

    private TransactionStatus evaluateFraudRules(Transaction transaction) {
        if (transaction.getValue().compareTo(FRAUD_THRESHOLD) > 0) {
            return TransactionStatus.REJECTED;
        }
        return TransactionStatus.APPROVED;
    }
}

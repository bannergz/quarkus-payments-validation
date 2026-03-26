package com.example.payments.application.service;

import com.example.payments.domain.model.Transaction;
import com.example.payments.domain.model.TransactionStatus;
import com.example.payments.domain.port.in.CreateTransactionUseCase;
import com.example.payments.domain.port.in.RetrieveTransactionUseCase;
import com.example.payments.domain.port.in.UpdateTransactionStatusUseCase;
import com.example.payments.domain.port.out.TransactionEventPort;
import com.example.payments.domain.port.out.TransactionRepositoryPort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import org.jboss.logging.Logger;

import java.util.UUID;

/**
 * Application service implementing all transaction use cases.
 * Orchestrates domain logic, persistence and event publishing.
 */
@ApplicationScoped
public class TransactionService
        implements CreateTransactionUseCase, RetrieveTransactionUseCase, UpdateTransactionStatusUseCase {

    private static final Logger LOG = Logger.getLogger(TransactionService.class);

    @Inject
    TransactionRepositoryPort transactionRepository;

    @Inject
    TransactionEventPort transactionEventPort;

    @Override
    @Transactional
    public Transaction createTransaction(Transaction transaction) {
        Transaction persisted = transactionRepository.save(transaction);

        try {
            transactionEventPort.publishValidationRequest(persisted);
        } catch (Exception e) {
            LOG.errorf(e, "Failed to publish validation event for transaction %s",
                    persisted.getTransactionExternalId());
            // Do not fail transaction creation if event publishing fails
        }

        return persisted;
    }

    @Override
    public Transaction retrieveTransaction(UUID externalId) {
        return transactionRepository.findByExternalId(externalId)
                .orElseThrow(() -> new NotFoundException(
                        "Transaction with externalId '" + externalId + "' not found"));
    }

    @Override
    @Transactional
    public void updateTransactionStatus(UUID externalId, TransactionStatus status) {
        transactionRepository.updateStatus(externalId, status);
        LOG.infof("Transaction %s status updated to %s", externalId, status.getName());
    }
}

package com.example.payments.infrastructure.adapter.out.persistence;

import com.example.payments.domain.model.Transaction;
import com.example.payments.domain.model.TransactionStatus;
import com.example.payments.domain.port.out.TransactionRepositoryPort;
import com.example.payments.infrastructure.adapter.out.persistence.entity.TransactionStatusEntity;
import com.example.payments.infrastructure.adapter.out.persistence.entity.TransactionTypeEntity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.NotFoundException;
import org.jboss.logging.Logger;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

/**
 * Adapter implementing the TransactionRepositoryPort using Panache.
 */
@ApplicationScoped
public class TransactionRepositoryAdapter implements TransactionRepositoryPort {

    private static final Logger LOG = Logger.getLogger(TransactionRepositoryAdapter.class);

    @Inject
    TransactionPanacheRepository panacheRepository;

    @Inject
    TransactionPersistenceMapper mapper;

    @Override
    public Transaction save(Transaction domain) {
        TransactionTypeEntity typeEntity = TransactionTypeEntity
                .find("name", domain.getTransactionType().getName())
                .<TransactionTypeEntity>firstResultOptional()
                .orElseThrow(() -> new NotFoundException(
                        "TransactionType not found: " + domain.getTransactionType().getName()));

        TransactionStatusEntity statusEntity = TransactionStatusEntity
                .find("name", domain.getTransactionStatus().getName())
                .<TransactionStatusEntity>firstResultOptional()
                .orElseThrow(() -> new NotFoundException(
                        "TransactionStatus not found: " + domain.getTransactionStatus().getName()));

        var entity = mapper.toEntity(domain, typeEntity, statusEntity);
        panacheRepository.persist(entity);

        LOG.infof("Transaction persisted with externalId=%s", entity.externalId);
        return mapper.toDomain(entity);
    }

    @Override
    public Optional<Transaction> findByExternalId(UUID externalId) {
        return panacheRepository.findByExternalId(externalId)
                .map(mapper::toDomain);
    }

    @Override
    public void updateStatus(UUID externalId, TransactionStatus status) {
        var entity = panacheRepository.findByExternalId(externalId)
                .orElseThrow(() -> new NotFoundException(
                        "Transaction not found: " + externalId));

        TransactionStatusEntity statusEntity = TransactionStatusEntity
                .find("name", status.getName())
                .<TransactionStatusEntity>firstResultOptional()
                .orElseThrow(() -> new NotFoundException(
                        "TransactionStatus not found: " + status.getName()));

        entity.transactionStatus = statusEntity;
        entity.updatedAt = Instant.now();
        panacheRepository.persist(entity);
    }
}

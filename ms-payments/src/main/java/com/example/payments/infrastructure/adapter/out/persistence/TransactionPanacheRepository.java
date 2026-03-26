package com.example.payments.infrastructure.adapter.out.persistence;

import com.example.payments.infrastructure.adapter.out.persistence.entity.TransactionEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Optional;
import java.util.UUID;

/**
 * Panache repository for TransactionEntity.
 * Provides CRUD operations and custom queries for the transaction table.
 */
@ApplicationScoped
public class TransactionPanacheRepository implements PanacheRepositoryBase<TransactionEntity, Long> {

    /**
     * Finds a transaction by its external UUID, eagerly loading associations.
     *
     * @param externalId the external UUID
     * @return an Optional with the entity if found
     */
    public Optional<TransactionEntity> findByExternalId(UUID externalId) {
        return find(
                "SELECT t FROM TransactionEntity t "
                        + "JOIN FETCH t.transactionType "
                        + "JOIN FETCH t.transactionStatus "
                        + "WHERE t.externalId = ?1",
                externalId
        ).firstResultOptional();
    }
}

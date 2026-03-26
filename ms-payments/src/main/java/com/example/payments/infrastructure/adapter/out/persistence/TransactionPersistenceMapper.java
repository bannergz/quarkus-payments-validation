package com.example.payments.infrastructure.adapter.out.persistence;

import com.example.payments.domain.model.Transaction;
import com.example.payments.domain.model.TransactionStatus;
import com.example.payments.domain.model.TransactionType;
import com.example.payments.infrastructure.adapter.out.persistence.entity.TransactionEntity;
import com.example.payments.infrastructure.adapter.out.persistence.entity.TransactionStatusEntity;
import com.example.payments.infrastructure.adapter.out.persistence.entity.TransactionTypeEntity;
import jakarta.enterprise.context.ApplicationScoped;

/**
 * Maps between TransactionEntity (infrastructure) and Transaction (domain).
 */
@ApplicationScoped
public class TransactionPersistenceMapper {

    public Transaction toDomain(TransactionEntity entity) {
        Transaction domain = new Transaction();
        domain.setTransactionExternalId(entity.externalId);
        domain.setAccountExternalIdDebit(entity.accountDebitId);
        domain.setAccountExternalIdCredit(entity.accountCreditId);
        domain.setTransactionType(new TransactionType(
                entity.transactionType.id.intValue(),
                entity.transactionType.name));
        domain.setTransactionStatus(new TransactionStatus(
                entity.transactionStatus.id.intValue(),
                entity.transactionStatus.name));
        domain.setValue(entity.value);
        domain.setCreatedAt(entity.createdAt);
        domain.setUpdatedAt(entity.updatedAt);
        return domain;
    }

    public TransactionEntity toEntity(Transaction domain,
            TransactionTypeEntity typeEntity,
            TransactionStatusEntity statusEntity) {
        TransactionEntity entity = new TransactionEntity();
        entity.externalId = domain.getTransactionExternalId();
        entity.accountDebitId = domain.getAccountExternalIdDebit();
        entity.accountCreditId = domain.getAccountExternalIdCredit();
        entity.transactionType = typeEntity;
        entity.transactionStatus = statusEntity;
        entity.value = domain.getValue();
        entity.createdAt = domain.getCreatedAt();
        entity.updatedAt = domain.getUpdatedAt();
        return entity;
    }
}

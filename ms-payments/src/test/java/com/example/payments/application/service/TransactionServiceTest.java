package com.example.payments.application.service;

import com.example.payments.domain.model.Transaction;
import com.example.payments.domain.model.TransactionStatus;
import com.example.payments.domain.model.TransactionType;
import com.example.payments.domain.port.out.TransactionEventPort;
import com.example.payments.domain.port.out.TransactionRepositoryPort;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.ws.rs.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@QuarkusTest
class TransactionServiceTest {

    @Inject
    TransactionService transactionService;

    @InjectMock
    TransactionRepositoryPort transactionRepository;

    @InjectMock
    TransactionEventPort transactionEventPort;

    private Transaction sampleTransaction;

    @BeforeEach
    void setUp() {
        sampleTransaction = new Transaction(
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                new TransactionType(1, "DEBIT"),
                TransactionStatus.PENDING,
                BigDecimal.valueOf(500.00),
                Instant.now(),
                Instant.now()
        );
    }

    @Test
    void createTransaction_shouldPersistAndPublishEvent() {
        when(transactionRepository.save(any(Transaction.class))).thenReturn(sampleTransaction);
        doNothing().when(transactionEventPort).publishValidationRequest(any(Transaction.class));

        Transaction result = transactionService.createTransaction(sampleTransaction);

        assertThat(result).isNotNull();
        assertThat(result.getTransactionExternalId())
                .isEqualTo(sampleTransaction.getTransactionExternalId());
        verify(transactionRepository).save(sampleTransaction);
        verify(transactionEventPort).publishValidationRequest(sampleTransaction);
    }

    @Test
    void createTransaction_shouldNotFailWhenEventPublishingFails() {
        when(transactionRepository.save(any(Transaction.class))).thenReturn(sampleTransaction);
        doThrow(new RuntimeException("Kafka unavailable"))
                .when(transactionEventPort).publishValidationRequest(any(Transaction.class));

        // Should not throw — event failure is non-blocking
        Transaction result = transactionService.createTransaction(sampleTransaction);

        assertThat(result).isNotNull();
        verify(transactionRepository).save(sampleTransaction);
    }

    @Test
    void retrieveTransaction_shouldReturnTransactionWhenFound() {
        UUID externalId = sampleTransaction.getTransactionExternalId();
        when(transactionRepository.findByExternalId(externalId))
                .thenReturn(Optional.of(sampleTransaction));

        Transaction result = transactionService.retrieveTransaction(externalId);

        assertThat(result.getTransactionExternalId()).isEqualTo(externalId);
    }

    @Test
    void retrieveTransaction_shouldThrowNotFoundWhenMissing() {
        UUID externalId = UUID.randomUUID();
        when(transactionRepository.findByExternalId(externalId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> transactionService.retrieveTransaction(externalId))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining(externalId.toString());
    }

    @Test
    void updateTransactionStatus_shouldDelegateToRepository() {
        UUID externalId = UUID.randomUUID();
        doNothing().when(transactionRepository).updateStatus(any(UUID.class), any(TransactionStatus.class));

        transactionService.updateTransactionStatus(externalId, TransactionStatus.APPROVED);

        verify(transactionRepository).updateStatus(externalId, TransactionStatus.APPROVED);
        verify(transactionEventPort, never()).publishValidationRequest(any());
    }
}

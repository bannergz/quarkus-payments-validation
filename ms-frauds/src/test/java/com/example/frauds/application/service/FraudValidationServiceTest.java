package com.example.frauds.application.service;

import com.example.frauds.domain.model.Transaction;
import com.example.frauds.domain.model.TransactionStatus;
import com.example.frauds.domain.model.TransactionType;
import com.example.frauds.domain.port.out.FraudValidationEventPort;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

@QuarkusTest
class FraudValidationServiceTest {

    @Inject
    FraudValidationService fraudValidationService;

    @InjectMock
    FraudValidationEventPort fraudValidationEventPort;

    private Transaction buildTransaction(BigDecimal value) {
        return new Transaction(
                UUID.randomUUID(),
                new TransactionType(1, "DEBIT"),
                TransactionStatus.PENDING,
                value,
                Instant.now()
        );
    }

    @BeforeEach
    void setUp() {
        doNothing().when(fraudValidationEventPort).publishValidationResponse(any(Transaction.class));
    }

    @Test
    void validateTransaction_shouldApproveWhenValueIsExactlyAtThreshold() {
        Transaction transaction = buildTransaction(BigDecimal.valueOf(1000));

        fraudValidationService.validateTransaction(transaction);

        ArgumentCaptor<Transaction> captor = ArgumentCaptor.forClass(Transaction.class);
        verify(fraudValidationEventPort).publishValidationResponse(captor.capture());
        assertThat(captor.getValue().getTransactionStatus().getName()).isEqualTo("APPROVED");
    }

    @ParameterizedTest
    @ValueSource(doubles = {0.01, 500.0, 999.99, 1000.0})
    void validateTransaction_shouldApproveTransactionsBelowOrAtThreshold(double amount) {
        Transaction transaction = buildTransaction(BigDecimal.valueOf(amount));

        fraudValidationService.validateTransaction(transaction);

        ArgumentCaptor<Transaction> captor = ArgumentCaptor.forClass(Transaction.class);
        verify(fraudValidationEventPort).publishValidationResponse(captor.capture());
        assertThat(captor.getValue().getTransactionStatus().getName()).isEqualTo("APPROVED");
    }

    @ParameterizedTest
    @ValueSource(doubles = {1000.01, 1500.0, 9999.99})
    void validateTransaction_shouldRejectTransactionsAboveThreshold(double amount) {
        Transaction transaction = buildTransaction(BigDecimal.valueOf(amount));

        fraudValidationService.validateTransaction(transaction);

        ArgumentCaptor<Transaction> captor = ArgumentCaptor.forClass(Transaction.class);
        verify(fraudValidationEventPort).publishValidationResponse(captor.capture());
        assertThat(captor.getValue().getTransactionStatus().getName()).isEqualTo("REJECTED");
    }

    @Test
    void validateTransaction_shouldNotThrowWhenEventPublishingFails() {
        doThrow(new RuntimeException("Kafka unavailable"))
                .when(fraudValidationEventPort).publishValidationResponse(any(Transaction.class));

        Transaction transaction = buildTransaction(BigDecimal.valueOf(200));

        // Should not propagate the exception
        fraudValidationService.validateTransaction(transaction);
    }
}

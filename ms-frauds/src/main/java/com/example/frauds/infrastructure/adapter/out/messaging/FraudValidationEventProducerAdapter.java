package com.example.frauds.infrastructure.adapter.out.messaging;

import com.example.frauds.domain.model.Transaction;
import com.example.frauds.domain.port.out.FraudValidationEventPort;
import com.example.frauds.infrastructure.adapter.out.messaging.dto.ResponseTransactionDto;
import com.example.frauds.infrastructure.adapter.out.messaging.dto.ResponseTransactionStatusDto;
import com.example.frauds.infrastructure.adapter.out.messaging.dto.TransactionValidationResponseDto;
import io.smallrye.reactive.messaging.kafka.api.OutgoingKafkaRecordMetadata;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.jboss.logging.Logger;

import java.time.Instant;
import java.util.UUID;

/**
 * Adapter implementing FraudValidationEventPort via SmallRye Reactive
 * Messaging.
 * Publishes fraud validation response events to Kafka using Confluent JSON
 * Schema serde.
 */
@ApplicationScoped
public class FraudValidationEventProducerAdapter implements FraudValidationEventPort {

  private static final Logger LOG = Logger.getLogger(FraudValidationEventProducerAdapter.class);

  @Inject
  @Channel("transaction-validation-response")
  Emitter<TransactionValidationResponseDto> emitter;

  @Override
  public void publishValidationResponse(Transaction transaction) {
    TransactionValidationResponseDto dto = buildResponseDto(transaction);

    Message<TransactionValidationResponseDto> message = Message.of(dto)
        .addMetadata(OutgoingKafkaRecordMetadata.<String>builder()
            .withKey(transaction.getTransactionExternalId().toString())
            .build());

    emitter.send(message);
    LOG.infof("Published validation response for transaction %s: status=%s",
        transaction.getTransactionExternalId(),
        transaction.getTransactionStatus().getName());
  }

  private TransactionValidationResponseDto buildResponseDto(Transaction transaction) {
    String eventType = switch (transaction.getTransactionStatus().getName()) {
      case "APPROVED" -> "TRANSACTION_APPROVED";
      case "REJECTED" -> "TRANSACTION_REJECTED";
      default -> "TRANSACTION_UPDATED";
    };

    ResponseTransactionStatusDto statusDto = new ResponseTransactionStatusDto(
        transaction.getTransactionStatus().getId(),
        transaction.getTransactionStatus().getName());

    ResponseTransactionDto transactionDto = new ResponseTransactionDto(
        transaction.getTransactionExternalId().toString(),
        statusDto,
        transaction.getValue());

    return new TransactionValidationResponseDto(
        UUID.randomUUID().toString(),
        Instant.now().toString(),
        eventType,
        transactionDto);
  }
}

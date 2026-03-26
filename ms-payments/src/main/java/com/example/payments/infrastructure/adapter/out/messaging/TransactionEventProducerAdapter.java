package com.example.payments.infrastructure.adapter.out.messaging;

import com.example.payments.domain.model.Transaction;
import com.example.payments.domain.port.out.TransactionEventPort;
import com.example.payments.infrastructure.adapter.out.messaging.dto.TransactionEventDto;
import com.example.payments.infrastructure.adapter.out.messaging.dto.TransactionStatusDto;
import com.example.payments.infrastructure.adapter.out.messaging.dto.TransactionTypeDto;
import com.example.payments.infrastructure.adapter.out.messaging.dto.TransactionValidationRequestDto;
import io.smallrye.reactive.messaging.kafka.api.OutgoingKafkaRecordMetadata;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.jboss.logging.Logger;

import java.time.Instant;
import java.util.UUID;

/**
 * Adapter implementing TransactionEventPort via SmallRye Reactive Messaging.
 * Publishes transaction validation request events to Kafka using Confluent JSON
 * Schema serde.
 */
@ApplicationScoped
public class TransactionEventProducerAdapter implements TransactionEventPort {

  private static final Logger LOG = Logger.getLogger(TransactionEventProducerAdapter.class);
  private static final String EVENT_TYPE_CREATED = "TRANSACTION_CREATED";

  @Inject
  @Channel("transaction-validation-request")
  Emitter<TransactionValidationRequestDto> emitter;

  @Override
  @Transactional(Transactional.TxType.NOT_SUPPORTED)
  public void publishValidationRequest(Transaction transaction) {
    TransactionValidationRequestDto dto = buildEventDto(transaction);

    Message<TransactionValidationRequestDto> message = Message.of(dto)
        .addMetadata(OutgoingKafkaRecordMetadata.<String>builder()
            .withKey(transaction.getTransactionExternalId().toString())
            .build());

    emitter.send(message);
    LOG.infof("Published validation request for transaction %s",
        transaction.getTransactionExternalId());
  }

  private TransactionValidationRequestDto buildEventDto(Transaction transaction) {
    TransactionTypeDto typeDto = new TransactionTypeDto(
        transaction.getTransactionType().getId(),
        transaction.getTransactionType().getName());

    TransactionStatusDto statusDto = new TransactionStatusDto(
        transaction.getTransactionStatus().getId(),
        transaction.getTransactionStatus().getName());

    TransactionEventDto transactionDto = new TransactionEventDto(
        transaction.getTransactionExternalId().toString(),
        typeDto,
        statusDto,
        transaction.getValue(),
        transaction.getCreatedAt().toString());

    return new TransactionValidationRequestDto(
        UUID.randomUUID().toString(),
        Instant.now().toString(),
        EVENT_TYPE_CREATED,
        transactionDto);
  }
}

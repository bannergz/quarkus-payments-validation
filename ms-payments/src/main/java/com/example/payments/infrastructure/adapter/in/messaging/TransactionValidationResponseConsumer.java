package com.example.payments.infrastructure.adapter.in.messaging;

import com.example.payments.domain.model.TransactionStatus;
import com.example.payments.domain.port.in.UpdateTransactionStatusUseCase;
import com.example.payments.infrastructure.adapter.in.messaging.dto.TransactionValidationResponseDto;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.annotation.PostConstruct;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.jboss.logging.Logger;
import io.quarkus.runtime.Startup;

import java.util.UUID;
import java.util.concurrent.CompletionStage;

/**
 * Kafka consumer for transaction-validation-response events.
 * Receives fraud validation results and updates transaction status.
 */
@Startup
@ApplicationScoped
public class TransactionValidationResponseConsumer {

  private static final Logger LOG = Logger.getLogger(TransactionValidationResponseConsumer.class);

  @Inject
  UpdateTransactionStatusUseCase updateTransactionStatusUseCase;

  @PostConstruct
  public void init() {
    LOG.info("========================================");
    LOG.info("TransactionValidationResponseConsumer INITIALIZED");
    LOG.info("Listening on topic: transaction-validation-response");
    LOG.info("Consumer group: ms-payments-consumer-group");
    LOG.info("Channel name: transaction-validation-response");
    LOG.info("========================================");
  }

  @Incoming("transaction-validation-response")
  public CompletionStage<Void> onTransactionValidationResponse(Message<TransactionValidationResponseDto> message) {
    System.out.println(">>> onTransactionValidationResponse CALLED <<<");
    LOG.info(">>> onTransactionValidationResponse CALLED - Starting to process message");
    TransactionValidationResponseDto event = message.getPayload();
    try {
      LOG.infof("Received validation response for transaction %s with status %s",
          event.getTransaction().getTransactionExternalId(),
          event.getTransaction().getTransactionStatus().getName());

      UUID externalId = UUID.fromString(event.getTransaction().getTransactionExternalId());
      TransactionStatus status = new TransactionStatus(
          event.getTransaction().getTransactionStatus().getId(),
          event.getTransaction().getTransactionStatus().getName());

      updateTransactionStatusUseCase.updateTransactionStatus(externalId, status);
      return message.ack();
    } catch (Exception e) {
      LOG.errorf(e, "Error processing transaction-validation-response for transaction %s",
          event.getTransaction().getTransactionExternalId());
      return message.nack(e);
    }
  }
}

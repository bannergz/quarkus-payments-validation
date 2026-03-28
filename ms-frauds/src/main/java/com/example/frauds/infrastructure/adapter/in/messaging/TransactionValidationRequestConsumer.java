package com.example.frauds.infrastructure.adapter.in.messaging;

import com.example.frauds.domain.model.Transaction;
import com.example.frauds.domain.model.TransactionStatus;
import com.example.frauds.domain.model.TransactionType;
import com.example.frauds.domain.port.in.ValidateFraudUseCase;
import com.example.frauds.infrastructure.adapter.in.messaging.dto.TransactionValidationRequestDto;
import io.quarkus.runtime.Startup;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.annotation.PostConstruct;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.jboss.logging.Logger;

import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.CompletionStage;

/**
 * Kafka consumer for transaction-validation-request events.
 * Triggers fraud validation on each received transaction using Confluent JSON
 * Schema serde.
 */
@Startup
@ApplicationScoped
public class TransactionValidationRequestConsumer {

  private static final Logger LOG = Logger.getLogger(TransactionValidationRequestConsumer.class);

  @Inject
  ValidateFraudUseCase validateFraudUseCase;

  @PostConstruct
  public void init() {
    LOG.info("TransactionValidationRequestConsumer initialized - listening on topic: transaction-validation-request");
  }

  @Incoming("transaction-validation-request")
  public CompletionStage<Void> consume(Message<TransactionValidationRequestDto> message) {
    TransactionValidationRequestDto event = message.getPayload();
    try {
      LOG.infof("Received validation request for transaction %s",
          event.getTransaction().getTransactionExternalId());

      Transaction domain = mapToDomain(event);
      validateFraudUseCase.validateTransaction(domain);
      return message.ack();
    } catch (Exception e) {
      LOG.errorf(e, "Error processing transaction-validation-request for transaction %s",
          event.getTransaction().getTransactionExternalId());
      return message.nack(e);
    }
  }

  private Transaction mapToDomain(TransactionValidationRequestDto event) {
    var tx = event.getTransaction();
    return new Transaction(
        UUID.fromString(tx.getTransactionExternalId()),
        new TransactionType(tx.getTransactionType().getId(), tx.getTransactionType().getName()),
        new TransactionStatus(tx.getTransactionStatus().getId(), tx.getTransactionStatus().getName()),
        tx.getValue(),
        Instant.parse(tx.getCreatedAt()));
  }
}

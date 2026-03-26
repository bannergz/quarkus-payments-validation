package com.example.payments.infrastructure.adapter.in.graphql;

import com.example.payments.domain.model.Transaction;
import com.example.payments.domain.model.TransactionStatus;
import com.example.payments.domain.model.TransactionType;
import com.example.payments.domain.port.in.CreateTransactionUseCase;
import com.example.payments.domain.port.in.RetrieveTransactionUseCase;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.ws.rs.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@QuarkusTest
class TransactionGraphQLResourceIT {

    @InjectMock
    CreateTransactionUseCase createTransactionUseCase;

    @InjectMock
    RetrieveTransactionUseCase retrieveTransactionUseCase;

    private Transaction sampleTransaction;
    private UUID sampleExternalId;

    @BeforeEach
    void setUp() {
        sampleExternalId = UUID.randomUUID();
        sampleTransaction = new Transaction(
                sampleExternalId,
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
    void createTransaction_shouldReturn200WithTransactionData() {
        when(createTransactionUseCase.createTransaction(any(Transaction.class)))
                .thenReturn(sampleTransaction);

        String mutation = """
                mutation {
                  createTransaction(createTransactionInput: {
                    accountExternalIdDebit: "550e8400-e29b-41d4-a716-446655440000"
                    accountExternalIdCredit: "550e8400-e29b-41d4-a716-446655440001"
                    transferTypeId: 1
                    value: 500.00
                  }) {
                    transactionExternalId
                    transactionType { name }
                    transactionStatus { name }
                    value
                  }
                }
                """;

        given()
                .contentType(ContentType.JSON)
                .body("{\"query\": \"" + mutation.replace("\n", " ").replace("\"", "\\\"") + "\"}")
                .when()
                .post("/graphql")
                .then()
                .statusCode(200)
                .body("data.createTransaction", notNullValue())
                .body("data.createTransaction.transactionType.name", equalTo("DEBIT"));
    }

    @Test
    void retrieveTransaction_shouldReturnNotFoundError() {
        UUID unknownId = UUID.randomUUID();
        when(retrieveTransactionUseCase.retrieveTransaction(unknownId))
                .thenThrow(new NotFoundException("Transaction not found: " + unknownId));

        String query = String.format("""
                { retrieveTransaction(externalId: "%s") { transactionExternalId } }
                """, unknownId);

        given()
                .contentType(ContentType.JSON)
                .body("{\"query\": \"" + query.strip() + "\"}")
                .when()
                .post("/graphql")
                .then()
                .statusCode(200)
                .body("errors", notNullValue());
    }
}

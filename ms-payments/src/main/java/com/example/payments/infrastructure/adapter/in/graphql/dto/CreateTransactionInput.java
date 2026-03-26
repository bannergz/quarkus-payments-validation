package com.example.payments.infrastructure.adapter.in.graphql.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.eclipse.microprofile.graphql.Description;
import org.eclipse.microprofile.graphql.Name;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * GraphQL input type for creating a new transaction.
 */
@Data
@NoArgsConstructor
@Name("CreateTransactionInput")
@Description("Input for creating a new payment transaction")
public class CreateTransactionInput {

    @NotNull(message = "accountExternalIdDebit is required")
    @Description("External UUID of the debit account")
    private UUID accountExternalIdDebit;

    @NotNull(message = "accountExternalIdCredit is required")
    @Description("External UUID of the credit account")
    private UUID accountExternalIdCredit;

    @NotNull(message = "transferTypeId is required")
    @Description("ID of the transaction type")
    private Integer transferTypeId;

    @NotNull(message = "value is required")
    @DecimalMin(value = "0.01", message = "Transaction value must be greater than zero")
    @Description("Amount to transfer")
    private BigDecimal value;
}

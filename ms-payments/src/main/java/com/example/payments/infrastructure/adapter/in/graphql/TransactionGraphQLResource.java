package com.example.payments.infrastructure.adapter.in.graphql;

import com.example.payments.domain.model.Transaction;
import com.example.payments.domain.port.in.CreateTransactionUseCase;
import com.example.payments.domain.port.in.RetrieveTransactionUseCase;
import com.example.payments.infrastructure.adapter.in.graphql.dto.CreateTransactionInput;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import org.eclipse.microprofile.graphql.Description;
import org.eclipse.microprofile.graphql.GraphQLApi;
import org.eclipse.microprofile.graphql.Mutation;
import org.eclipse.microprofile.graphql.Name;
import org.eclipse.microprofile.graphql.Query;
import org.jboss.logging.Logger;

import java.util.UUID;

/**
 * GraphQL resource exposing transaction operations via MicroProfile GraphQL.
 */
@GraphQLApi
public class TransactionGraphQLResource {

    private static final Logger LOG = Logger.getLogger(TransactionGraphQLResource.class);

    @Inject
    CreateTransactionUseCase createTransactionUseCase;

    @Inject
    RetrieveTransactionUseCase retrieveTransactionUseCase;

    @Inject
    TransactionGraphQLMapper mapper;

    @Mutation("createTransaction")
    @Description("Creates a single transaction to be validated and processed")
    public Transaction createTransaction(
            @Name("createTransactionInput") @Valid CreateTransactionInput input) {
        LOG.infof("Creating transaction for debit account %s", input.getAccountExternalIdDebit());
        Transaction domain = mapper.createInputToDomain(input);
        return createTransactionUseCase.createTransaction(domain);
    }

    @Query("retrieveTransaction")
    @Description("Retrieves a single transaction by its external ID")
    public Transaction retrieveTransaction(
            @Name("externalId") UUID externalId) {
        LOG.infof("Retrieving transaction with externalId=%s", externalId);
        return retrieveTransactionUseCase.retrieveTransaction(externalId);
    }
}

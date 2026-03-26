package com.example.payments.infrastructure.adapter.out.persistence.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.NoArgsConstructor;

/**
 * JPA entity for transaction_type table.
 */
@Entity
@Table(name = "transaction_type", indexes = {
        @Index(name = "idx_transaction_type_name", columnList = "name")
})
@NoArgsConstructor
public class TransactionTypeEntity extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @NotBlank
    @Column(name = "name", nullable = false, unique = true)
    public String name;

    public TransactionTypeEntity(String name) {
        this.name = name;
    }
}

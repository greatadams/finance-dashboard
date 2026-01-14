package com.pm.greatadamu.transactionservice.repository;

import com.pm.greatadamu.transactionservice.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByCustomerId(Long customerId);

    Optional<Transaction> findByIdempotencyKey(String idempotencyKey);

}
